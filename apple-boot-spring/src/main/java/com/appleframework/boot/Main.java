package com.appleframework.boot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.boot.core.Container;
import com.appleframework.boot.core.ContainerFactory;
import com.appleframework.boot.core.ContainerHandle;
import com.appleframework.boot.core.logging.LoggingContainerFactory;
import com.appleframework.boot.core.monitor.MonitorContainerFactory;
import com.appleframework.boot.spring.SpringContainer;

/**
 * Main. (API, Static, ThreadSafe)
 * 
 * @author cruise.xu
 */
public class Main {
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static final String SHUTDOWN_HOOK_KEY = "shutdown.hook";
    
    private static volatile boolean running = true;

    public static void main(String[] args) {
        try {
        	StartUpInit.init(args);
        	            
        	final List<Container> containers = new ArrayList<Container>();
        	containers.add(MonitorContainerFactory.getContainer());
			containers.add(LoggingContainerFactory.getContainer());
            containers.add(ContainerFactory.create(SpringContainer.class));
            
            logger.info("Use container type(" + Arrays.toString(args) + ") to run serivce.");
            
            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    for (Container container : containers) {
	                        try {
	                            container.stop();
	                            logger.info("Service " + container.getClass().getSimpleName() + " stopped!");
	                        } catch (Throwable t) {
	                            logger.error(t.getMessage(), t);
	                        }
	                        synchronized (Main.class) {
	                            running = false;
	                            Main.class.notify();
	                        }
	                    }
	                }
	            });
            }
            
            ContainerHandle.jmx(containers);

			ContainerHandle.start(containers);
            
            logger.warn(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " 所有服务启动成功!");
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                	logger.error(e.getMessage(), e);
                }
            }
        }
    }
    
}