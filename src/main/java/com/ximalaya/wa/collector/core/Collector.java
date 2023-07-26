package com.ximalaya.wa.collector.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSON;
import com.clearspring.analytics.util.Lists;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ximalaya.dtres.util.FdfsDownloadUtil;
import com.ximalaya.dtres.util.FilePathUtils;
import com.ximalaya.dtres.util.HttpClientUtils;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.cache.RedisCache;
import com.ximalaya.wa.config.BizType;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Account;
import com.ximalaya.wa.model.xml.ResultInfo;
import com.ximalaya.wa.sender.util.FileUtil;
import com.ximalaya.wa.sender.util.ZipCompressUtil;
import com.ximalaya.wa.util.OxmUtil;

import jodd.util.StringUtil;

@SuppressWarnings("all")
public class Collector implements InitializingBean, SmartLifecycle {

    protected Logger LOG;

    protected static final String MODEL_PATH = "com.ximalaya.wa.model.";
    protected static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    protected int timeout = 300000;
    protected int interval = 10000;
    protected int batchSize = 1000;
    protected int accountBatchSize = 360;
    protected String queueName;
    protected String type;
    protected boolean isRunning;
    protected BlockingQueue queue;
    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected Lock drainLock = new ReentrantLock();
    protected volatile long nextRunTime;
    protected volatile boolean shutdown;
    
    @Value("${fdfs.paid.internal.http.domain}")
	private String internalFreeDomain;

    @Override
    public void start() {

        Thread main = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!shutdown) {
                    try {
                        // LOG.info(type+"Queue.size:" + queue.size());
                    	
                    	String queueName = StringUtil.uncapitalize(type);
                    	
                    	if (queueName.equals("account")) {
							
                    		if (System.currentTimeMillis() >= nextRunTime || queue.size() >= accountBatchSize) {
                                if (System.currentTimeMillis() >= nextRunTime) {
                                    nextRunTime = nextRunTime + timeout;
                                }
                                List datas = new ArrayList();
                                queue.drainTo(datas, batchSize);
                                Runnable task = new Runnable() {

                                    @Override
                                    public void run() {
                                		LOG.info("receive account size:{}",datas.size());
                                		packageAccountXmlAndPic(datas);// 打包丢到zip目录下，一起上传        
                                    }

                                };
                                executor.execute(task);

                            } else {
                                Thread.currentThread().sleep(interval);
                            }
                    		
						}else{
							if (System.currentTimeMillis() >= nextRunTime || queue.size() >= batchSize) {
	                            if (System.currentTimeMillis() >= nextRunTime) {
	                                nextRunTime = nextRunTime + timeout;
	                            }
	                            List datas = new ArrayList();
	                            queue.drainTo(datas, batchSize);
	                            Runnable task = new Runnable() {

	                                @Override
	                                public void run() {
	                                	toXmlFile(datas);
	                                }

	                            };
	                            executor.execute(task);

	                        } else {
	                            Thread.currentThread().sleep(interval);
	                        }
						}
                    	
                    } catch (InterruptedException e) {
                        break;
                    } catch (Throwable e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        });

        main.start();
        isRunning = true;

    }

    protected <T> void write(ResultInfo<T> result) throws Exception {
    	/**
    	 * 需要一个临时目录，否则容易在创建的过程中，文件还没有写完就被上传，导致文件不完整
    	 */
    	
        if (result!=null&&result.getResult()!=null) {
            String tempPath = DataAssembler.TEMP_PATH;
            String filePath = DataAssembler.FILE_PATH;
            String fileName = result.getFileName();
            File tempFile = new File(tempPath + fileName);
            if (!tempFile.exists())
                FileUtil.newFile(tempPath, fileName);
            
            File file = new File(filePath + fileName);
            if (!file.exists())
                FileUtil.newFile(filePath, fileName);
            OutputStream os = new FileOutputStream(tempFile);

            try {
                writeXmlHeader(os);
                OxmUtil.toXML(result.getResult(), os);
               if (!tempFile.renameTo(file)) {
            	   LOG.error("rename temp file to file failured,tempFile : {},file : {}",tempFile.getAbsoluteFile(),file.getAbsoluteFile());
               }
            } finally {
				os.close();
			}

            
        }
    }

    protected void toXmlFile(List datas) {
        if (CollectionUtils.isEmpty(datas))
            return;
        try {
            long start = System.currentTimeMillis();
            ResultInfo result = WaConverter.convertToResult(BizType.REPORT, datas, null);
            long end = System.currentTimeMillis();
            LOG.info("filtered size : {}, toxml beans size :{} ", datas.size() - result.getRecordNum(),result.getRecordNum());
            if(result.getResult()!=null){

                RedisCache.setRecordNum(result.getFileName(), result.getRecordNum());
                write(result);
            }

        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);

        }
    }

    protected void writeXmlHeader(OutputStream os) throws IOException {
        os.write((HEADER + System.getProperty("line.separator", "\r\n")).getBytes());
    }

    protected void packageAccountXmlAndPic(List<Account> datas){
    	
    	 if (CollectionUtils.isEmpty(datas)){
    		 LOG.info("account msg has no data...");
    		 return;
    	 } // data 校验
    	 
    	 String dir = null;
    	 long start = System.currentTimeMillis();
    	 
         try {
        	 
        	 String filePath = DataAssembler.ACCOUNT_PATH;
        	 String temp = String.valueOf(System.currentTimeMillis());
        	 dir = FilePathUtils.assembleLocalFilePath(filePath,temp); // 按照时间戳来命名文件夹
        	 File dirFile = new File(dir);
        	 if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
        	 
        	 // 1.download pic and update userphoto data...
        	 for (Account account : datas) {// 先下载图片
     			
         		String playPath = account.getAvatarPath();
         		LOG.info("get data userphoto {}...",playPath);
         		String fileName = null;
         		
         		if (playPath != null) {
     				try {
     					
     					start = System.currentTimeMillis();
     					
     					if (playPath.startsWith("group")) { 
     					
     						String[] str = playPath.split("/");
     						fileName = str[str.length - 1];
     						String localFilePath = FilePathUtils.assembleLocalFilePath(dir, fileName);
							FdfsDownloadUtil.downloadByHttp(playPath, localFilePath,0,null,internalFreeDomain);
     						// 站内图片
     					}else{ 
     					
     						String ext = ("").equals(FilePathUtils.getFileExtName(playPath)) ? "jpg" : FilePathUtils.getFileExtName(playPath);
     						fileName = FilePathUtils.getRandomFileNameWithExt(ext);
    						String localFilePath = FilePathUtils.assembleLocalFilePath(dir, fileName);
    						HttpClientUtils.downloadFile(playPath, localFilePath);
     						// 站外图片
     					}
     					
     					LOG.info("download pic {} cost time : {}ms",playPath,(System.currentTimeMillis() - start));
     					
     					
     					File file = new File(FilePathUtils.assembleLocalFilePath(dir,fileName));
     					if (file.exists()) {
     						account.setAvatarPath(fileName);// 文件名写入到accout
						}else{
							account.setAvatarPath(null);
						}
     					
     				} catch (Exception e) {
     					LOG.warn("download pic {} error...",playPath,e);
     					account.setAvatarPath(null);// 下载失败，文件名设为null
     				}
     			} 
         		 
     		}
        	 
        	 
        	 
        	 
        	 // 2.write data to xml...
        	 start = System.currentTimeMillis();
             ResultInfo result = WaConverter.convertToResult(BizType.REPORT, datas, null);
             long end = System.currentTimeMillis();
             LOG.info("filtered size : {}, toxml beans size :{} ", datas.size() - result.getRecordNum(),result.getRecordNum());
             // create result
            
             String fileName = null;
             if(result.getResult()!=null){
                 
            	 RedisCache.setRecordNum(result.getFileName(), result.getRecordNum());
            	 // write to redis
                 
                 if (result!=null&&result.getResult()!=null) {
                     
                     fileName = result.getFileName();
                     // create filePath
                     
                     File file = new File(FilePathUtils.assembleLocalFilePath(dir,fileName));
                     if (!file.exists())  FileUtil.newFile(dir, fileName);
                     OutputStream os = new FileOutputStream(file);
                     writeXmlHeader(os);
                     OxmUtil.toXML(result.getResult(), os); 
                     
                     LOG.info("write account data to xml cost time :{}ms",System.currentTimeMillis() - start);
                     // write to xml
                     
                 }
                 
             }
             
              // compress file...
             if (fileName == null) {
				LOG.error("create xml file error...");
				return;
			}
             
            String zipname = fileName.replace(".xml", ".zip");
            ZipCompressUtil.zipCompressDir(dir,zipname);

         } catch (Throwable e) {
             LOG.error(e.getMessage(), e);
         }finally{
        	 
        	 // 删除目录
        	 File file = new File(dir);
        	 if (file.exists()) {
        		com.ximalaya.wa.sender.util.FileUtil.deleteDir(file);
			}
        	 
         }

    	
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (type == null)
            throw new RuntimeException("type must config!");
        LOG = LoggerFactory.getLogger(Collector.class.getName() + ":" + type);
        queueName = StringUtils.uncapitalize(type) + "Queue";
        nextRunTime = System.currentTimeMillis() + timeout;
        Field field = ReflectionUtils.findField(DataHub.class, queueName);
        field.setAccessible(true);
        queue = (BlockingQueue) field.get(DataHub.class);
        enqueueTaskInFile();
    }

    /**
     * 启动的时候将文件里面的消息再写入到queue里面
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    protected void enqueueTaskInFile() throws IOException, InterruptedException, ClassNotFoundException {

        // 获取queue的参数类型
        Class clazz = Class.forName(MODEL_PATH + type);

        // 读文件进queue，发生异常则将未读入queue的继续写回dump文件
        File backFile = new File(DataAssembler.TEMP_PATH + queueName);
        if (!backFile.exists())
            return;
        List<String> readLines = Files.readLines(backFile, Charsets.UTF_8);
        backFile.delete();

        Iterator<String> eleIt = readLines.iterator();
        try {
            while (eleIt.hasNext()) {
                queue.put(JSON.parseObject(eleIt.next(), clazz));
                eleIt.remove();
            }

        } finally {
            if (CollectionUtils.isNotEmpty(readLines)) {
                backFile.createNewFile();
                for (Object o : readLines) {
                    try {
                        Files.append(JSON.toJSONString(o), backFile, Charsets.UTF_8);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }

    }

    @Override
    public void stop() {
        shutdown = true;
        executor.shutdownNow();
        dequeueTaskToFile();
    }

    protected void dequeueTaskToFile() {

        File backFile = new File(DataAssembler.TEMP_PATH + queueName);
        if (CollectionUtils.isEmpty(queue))
            return;
        if (!backFile.exists())
            try {
                backFile.createNewFile();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        // 用drainTo不用iterator防止并发错误（见#start#）
        List list = Lists.newArrayList();
        queue.drainTo(list);
        for (Object o : list) {
            try {
                Files.append(JSON.toJSONString(o) + System.getProperty("line.separator", "\r\n"), backFile,
                    Charsets.UTF_8);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }

    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

}
