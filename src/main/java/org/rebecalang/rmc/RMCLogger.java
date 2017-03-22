/*              In the name of Allah            */

package org.rebecalang.rmc;

import org.apache.log4j.PropertyConfigurator;

public class RMCLogger {
    private static RMCLogger ourInstance = new RMCLogger();
    private org.apache.log4j.Logger logger;

    public static RMCLogger getInstance() {
        return ourInstance;
    }

    private RMCLogger() {
        logger = org.apache.log4j.Logger.getLogger("RMC");
        PropertyConfigurator.configure("log4j.properties");
//        BasicConfigurator.configure();
//        logger.setLevel(Level.FATAL);
    }

    public org.apache.log4j.Logger getLogger() {
        return logger;
    }

    public void setLoggerName(String name) {
        logger = org.apache.log4j.Logger.getLogger(name);
        PropertyConfigurator.configure("log4j.properties");
    }
    
}