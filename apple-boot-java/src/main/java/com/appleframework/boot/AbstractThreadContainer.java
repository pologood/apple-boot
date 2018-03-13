package com.appleframework.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.boot.core.Container;
import com.appleframework.boot.utils.ApplicationUtils;

/**
 * SpringContainer. (SPI, Singleton, ThreadSafe)
 * 
 * @author cruise.xu
 */
public abstract class AbstractThreadContainer implements Container {

	private static Logger logger = LoggerFactory.getLogger(AbstractThreadContainer.class);
        
	private static long startTime = System.currentTimeMillis();
	
	private Thread schedulerThread;
	
	public abstract void doStart();

	public void start() {
		startTime = System.currentTimeMillis();
		schedulerThread = new Thread() {
			@Override
			public void run() {
				doStart();
			}
		};
		schedulerThread.setName("Java Container [" + this.getName() + "]");
		schedulerThread.setDaemon(true);
		schedulerThread.start();
	}

    @SuppressWarnings("deprecation")
	public void stop() {
        try {
        	schedulerThread.resume();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

	@Override
	public void restart() {
		try {
			schedulerThread.run();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
	}

	@Override
	public boolean isRunning() {
    	return schedulerThread.isAlive();
	}
	
	@Override
	public String getName() {
    	return ApplicationUtils.getApplicationName();
	}
    
	@Override
	public String getType() {
		return "JavaContainer";
	}

	@Override
	public long getStartTime() {
		return startTime;
	}
}