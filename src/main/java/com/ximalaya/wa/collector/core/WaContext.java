package com.ximalaya.wa.collector.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.ximalaya.wa.annotation.Ignore;
import com.ximalaya.wa.annotation.Mapped;

@SuppressWarnings("all")
@Repository
public class WaContext implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(WaContext.class);

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private static final Map<Class, Map<Field, Mapped>> fieldMappedInfo = Maps.newHashMap();

    private static final String packagesToScan = "com.ximalaya.wa.model";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
            .getResourcePatternResolver(applicationContext);

        String[] packages = org.springframework.util.StringUtils
            .tokenizeToStringArray(
                packagesToScan,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        try {
            for (String pkg : packages) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(pkg)
                    + RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver
                    .getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
                    resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory
                            .getMetadataReader(resource);
                        String className = reader.getClassMetadata()
                            .getClassName();
                        Class clazz = Class.forName(className);
                        final Map<Field, Mapped> mappedFields = Maps.newHashMap();
                        ReflectionUtils.doWithFields(clazz, new FieldCallback() {

                            @Override
                            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                                field.setAccessible(true);
                                Mapped ann = field.getAnnotation(Mapped.class);
                                if (ann != null) {
                                    mappedFields.put(field, ann);
                                }
                            }
                        });
                        filter(mappedFields);
                        fieldMappedInfo.put(clazz, mappedFields);

                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Failed to scan classpath for unlisted classes", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("Failed to load annotated classes from classpath", ex);

        }
    }

    private void filter(Map<Field, Mapped> mappedFields) {
        // 过滤掉@Ignore
        Iterator<Map.Entry<Field, Mapped>> it = mappedFields.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getKey().isAnnotationPresent(Ignore.class)) {
                it.remove();
            }
        }
        // 筛选掉父类和子类都有的field，只保留子类的
        Map<String, Collection<Field>> categorizedFields =
            Multimaps.index(mappedFields.keySet(), new Function<Field, String>() {

                @Override
                public String apply(Field field) {
                    return field.getName();
                }
            }).asMap();

        for (Map.Entry<String, Collection<Field>> categorizedField : categorizedFields.entrySet()) {
            if (categorizedField.getValue().size() > 1) {

                Field son = null;

                for (Field field : categorizedField.getValue()) {
                    if (son == null) {
                        son = field;
                    } else if (son.getDeclaringClass().isAssignableFrom(field.getDeclaringClass())) {
                        mappedFields.remove(son);
                        son = field;
                    } else {
                        mappedFields.remove(field);
                    }
                }

            }
        }

    }

    public static Map<Field, Mapped> getAnnotatedFields(Class clazz) {
        return fieldMappedInfo.get(clazz);
    }

}