package com.ximalaya.wa.helper;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.impl.Log4jLoggerAdapter;
import org.slf4j.spi.LocationAwareLogger;

public class SampleLogger implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 4880955964341096197L;
    private static final String prefix = "scheduled.";
    private final int interval;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final String source;
    private final Runnable task = new Runnable() {               
        public void run() {
            logger.setLevel(Level.DEBUG);            
        }
    };
    public static SampleLogger getLogger(Class clazz,int interval){
        return new SampleLogger(clazz.getName(),interval);
    }
    private SampleLogger(String source,int interval){
        this.interval=interval;
        this.source=prefix+source;
        logger= Logger.getLogger(source);
        executor.scheduleAtFixedRate(task, 0, interval, TimeUnit.SECONDS);
    }

    final transient org.apache.log4j.Logger logger;

    /**
     * Following the pattern discussed in pages 162 through 168 of "The complete
     * log4j manual".
     */
    final static String FQCN = SampleLogger.class.getName();


    /**
     * Log a message object at level DEBUG.
     * 
     * @param msg
     *          - the message object to be logged
     */
    public void log(String msg) {
      logger.log(FQCN, Level.DEBUG, msg, null);
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for level DEBUG.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void log(String format, Object arg) {
      if (logger.isDebugEnabled()) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        logger.setLevel(Level.INFO);
      }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void log(String format, Object arg1, Object arg2) {
      if (logger.isDebugEnabled()) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        logger.setLevel(Level.INFO);
      }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arguments an array of arguments
     */
    public void log(String format, Object... arguments) {
      if (logger.isDebugEnabled()) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        logger.setLevel(Level.INFO);
      }
    }

    /**
     * Log an exception (throwable) at level DEBUG with an accompanying message.
     * 
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void log(String msg, Throwable t) {
      logger.log(FQCN, Level.DEBUG, msg, t);
      logger.setLevel(Level.INFO);
    }



    
}
