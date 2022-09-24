package de.cofinpro.jsondb;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class Log4j2Test {

    @Test
    void log4jActiveAndErrorTest() {
        Object loggerPath = org.apache.logging.log4j.Logger.class
                .getResource("/org/apache/logging/log4j/Logger.class");
        assertNotNull(loggerPath);
        System.out.println(loggerPath);
        Object appenderPath = org.apache.logging.log4j.Logger.class
                .getResource("/org/apache/logging/log4j/core/Appender.class");
        assertNotNull(appenderPath);
        System.out.println(appenderPath);
        log.info("Hallo");
    }

    @Test
    void log4j2xmlInCPAndPrintTest() {
        Object resourcePath = org.apache.logging.log4j.Logger.class.getResource("/log4j2.xml");
        assertNotNull(resourcePath);
        System.out.println(resourcePath);
        log.warn("Warn");
    }

    @Test
    void logLevelSetToInfoTest() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Level rootLogLevel = context.getConfiguration().getLoggerConfig(LogManager.ROOT_LOGGER_NAME).getLevel();
        System.out.println("Root - Loglevel Slf4J is : " + rootLogLevel);
        assertEquals("INFO", rootLogLevel.toString());
    }
}