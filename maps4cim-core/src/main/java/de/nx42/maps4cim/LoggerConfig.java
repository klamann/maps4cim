/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.StatusPrinter;

import org.slf4j.LoggerFactory;

/**
 * Configuration of the Logging framework (currently Logback, via SLF4J)
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class LoggerConfig {

    protected static final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    protected static final Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);

    /** the default logging pattern, as used in console and file output */
    public static final String defaultPattern = "%date{HH:mm:ss.SSS} %-5level %class{0}:%line [%thread] - %msg%n";
    /** name of the logfile, without extension (defaults to .log) */
    public static final String logFileName = "maps4cim";

    /**
     * Initializes the logger (file and console logging)
     * This method should be called before the first log entry is done, or
     * a new log file will be created in the directory of the executable.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void initLogger() {
        rootLogger.detachAndStopAllAppenders();

        // build & attach the console appender
        ConsoleAppender ca = getConsoleAppender();
        rootLogger.addAppender(ca);

        // get log file; also creates appdata-dir if nonexistent
        ResourceLoader.getAppDir();
        File logFile = getLogFile();

        // build & attach the file appender
        FileAppender fa = getFileAppender(logFile);
        rootLogger.addAppender(fa);

        // set log level
        rootLogger.setLevel(Level.DEBUG);

        // OPTIONAL: print logback internal status messages
        StatusPrinter.print(lc);

        // first log entries
        rootLogger.debug("---------- NEW SESSION ----------");
        rootLogger.debug("Writing log to {}", logFile.getAbsoluteFile());
    }

    /**
     * Retrieve the current log file (will be created on first log entry,
     * if nonexistent
     * @return the active log file
     */
    public static File getLogFile() {
        return new File(ResourceLoader.appdata, logFileName + ".log");
    }

    /**
     * @return the root logger
     */
    public static Logger getRootLogger() {
        return rootLogger;
    }

    /**
     * @param l sets the global log level
     */
    public static void setLogLevel(Level l) {
        rootLogger.setLevel(l);
    }

    /**
     * Adds an additional {@link Appender} to the root logger
     * @param appender the Appender to add
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void addLogAppender(Appender appender) {
        rootLogger.addAppender(appender);
    }

    /**
     * Creates a new Encoder for the specified pattern
     * @param pattern the pattern that shall be used by the encoder
     * @return a new Encoder using the specified pattern
     */
    @SuppressWarnings("rawtypes")
    public static Encoder getEncoder(String pattern) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern(pattern);
        encoder.start();
        return encoder;
    }

    /**
     * @return a new Encoder using the default pattern ({@link #defaultPattern})
     */
    @SuppressWarnings("rawtypes")
    public static Encoder getDefaultEncoder() {
        return getEncoder(defaultPattern);
    }

    /**
     * Creates the file appender for the logger with a custom logging policy
     * (RollingFileAppender with max size of 1 MB and up to 3 backups)
     * @param logFile the file to log into
     * @return the FileAppender for the specified File and current logging
     *         context
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static FileAppender getFileAppender(File logFile) {
        // init rolling file appender
        RollingFileAppender rfAppender = new RollingFileAppender();
        rfAppender.setContext(lc);
        rfAppender.setFile(logFile.getAbsolutePath());
        rfAppender.setAppend(true);

        // rolling policy: keep up to 3 rollover-files with postfix .%i.log
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(lc);
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern(logFileName + ".%i.log");
        rollingPolicy.setMinIndex(1);
        rollingPolicy.setMaxIndex(3);
        rollingPolicy.start();

        // rollover after logfixe exceeds 1MB
        SizeBasedTriggeringPolicy triggeringPolicy = new SizeBasedTriggeringPolicy();
        triggeringPolicy.setMaxFileSize("1MB");
        triggeringPolicy.start();

        // layout of the log entries
        Encoder encoder = getDefaultEncoder();

        // apply settings and start appender
        rfAppender.setEncoder(encoder);
        rfAppender.setRollingPolicy(rollingPolicy);
        rfAppender.setTriggeringPolicy(triggeringPolicy);
        rfAppender.start();

        return rfAppender;
    }

    /**
     * Creates a simple ConsoleAppender with the default pattern
     * @return a ConsoleAppender for the current logging context
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static ConsoleAppender getConsoleAppender() {
        ConsoleAppender cAppender = new ConsoleAppender();
        cAppender.setContext(lc);
        cAppender.setEncoder(getDefaultEncoder());
        cAppender.start();
        return cAppender;
    }

    protected static void stopLogging() {
        lc.stop();
    }

}
