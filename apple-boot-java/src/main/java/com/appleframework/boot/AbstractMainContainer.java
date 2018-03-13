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
public abstract class AbstractMainContainer implements Container {

	private static Logger logger = LoggerFactory.getLogger(AbstractMainContainer.class);
        
	private static long startTime = System.currentTimeMillis();
	
	public abstract void doStart();

	public void start() {
		startTime = System.currentTimeMillis();
		doStart();
	}

	public void stop() {
        try {
        	System.exit(-1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

	@Override
	public void restart() {
		try {
			
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
	}

	@Override
	public boolean isRunning() {
    	return true;
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