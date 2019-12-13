package bio.tools.blast.assist;

import util.DummyLog;

/**
 * <p>A simple implementation that delegates all log requests to the Google Android
 * logging facilities. Note that this logger does not support {@link org.slf4j.Marker}.
 * Methods taking marker data as parameter simply invoke the eponymous method
 * without the Marker argument, discarding any marker data in the process.</p>
 *
 * <p>The logging levels specified for SLF4J can be almost directly mapped to
 * the levels that exist in the Google Android platform. The following table
 * shows the mapping implemented by this logger.</p>
 *
 * <table border="1">
 * <tr><th><b>SLF4J<b></th><th><b>Android</b></th></tr>
 * <tr><td>TRACE</td><td>{@link DummyLog.util.Log#VERBOSE}</td></tr>
 * <tr><td>DEBUG</td><td>{@link DummyLog.util.Log#DEBUG}</td></tr>
 * <tr><td>INFO</td><td>{@link DummyLog.util.Log#INFO}</td></tr>
 * <tr><td>WARN</td><td>{@link DummyLog.util.Log#WARN}</td></tr>
 * <tr><td>ERROR</td><td>{@link DummyLog.util.Log#ERROR}</td></tr>
 * </table>
 *
 * <p>Use loggers as usual:
 * <ul>
 *     <li>
 *         Declare a logger<br/>
 *         <code>private static final Logger logger = LoggerFactory.getLogger(MyClass.class);</code>
 *     </li>
 *     <li>
 *         Invoke logging methods, e.g.,<br/>
 *         <code>logger.debug("Some log message. Details: {}", someObject);</code><br/>
 *         <code>logger.debug("Some log message with varargs. Details: {}, {}, {}", someObject1, someObject2, someObject3);</code>
 *     </li>
 * </ul>
 * </p>
 *
 * <p>Logger instances created using the LoggerFactory are named either according to the name
 * or the fully qualified class name of the class given as a parameter.
 * Each logger name will be used as the log message tag on the Android platform.
 * However, tag names cannot be longer than 23 characters so if logger name exceeds this limit then
 * it will be truncated by the LoggerFactory. The following examples illustrate this.
 * <table border="1">
 * <tr><th><b>Original Name<b></th><th><b>Truncated Name</b></th></tr>
 * <tr><td>org.example.myproject.mypackage.MyClass</td><td>o*.e*.m*.m*.MyClass</td></tr>
 * <tr><td>o.e.myproject.mypackage.MyClass</td><td>o.e.m*.m*.MyClass</td></tr>
 * <tr><td>org.example.ThisNameIsWayTooLongAndWillBeTruncated</td><td>*LongAndWillBeTruncated</td></tr>
 * <tr><td>ThisNameIsWayTooLongAndWillBeTruncated</td><td>*LongAndWillBeTruncated</td></tr>
 * </table>
 * </p>
 *
 * @author Andrey Korzhevskiy <a.korzhevskiy@gmail.com>
 */
class AndroidLoggerAdapter extends MarkerIgnoringBase {
    private static final long serialVersionUID = -1227274521521287937L;

    /**
     * Package access allows only {@link AndroidLoggerFactory} to instantiate
     * SimpleLogger instances.
     */
    AndroidLoggerAdapter(String tag) {
        this.name = tag;
    }

    /**
     * Is this logger instance enabled for the VERBOSE level?
     *
     * @return True if this Logger is enabled for level VERBOSE, false otherwise.
     */
    public boolean isTraceEnabled() {
        return isLoggable(DummyLog.VERBOSE);
    }

    /**
     * Log a message object at level VERBOSE.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void trace(String msg) {
        log(DummyLog.VERBOSE, msg, null);
    }

    /**
     * Log a message at level VERBOSE according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for level VERBOSE.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void trace(String format, Object arg) {
        formatAndLog(DummyLog.VERBOSE, format, arg);
    }

    /**
     * Log a message at level VERBOSE according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the VERBOSE level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void trace(String format, Object arg1, Object arg2) {
        formatAndLog(DummyLog.VERBOSE, format, arg1, arg2);
    }

    /**
     * Log a message at level VERBOSE according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the VERBOSE level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void trace(String format, Object... argArray) {
        formatAndLog(DummyLog.VERBOSE, format, argArray);
    }

    /**
     * Log an exception (throwable) at level VERBOSE with an accompanying message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void trace(String msg, Throwable t) {
        log(DummyLog.VERBOSE, msg, t);
    }

    /**
     * Is this logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for level DEBUG, false otherwise.
     */
    public boolean isDebugEnabled() {
        return isLoggable(DummyLog.DEBUG);
    }

    /**
     * Log a message object at level DEBUG.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void debug(String msg) {
        log(DummyLog.DEBUG, msg, null);
    }

    /**
     * Log a message at level DEBUG according to the specified format and argument.
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
    public void debug(String format, Object arg) {
        formatAndLog(DummyLog.DEBUG, format, arg);
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
    public void debug(String format, Object arg1, Object arg2) {
        formatAndLog(DummyLog.DEBUG, format, arg1, arg2);
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
     * @param argArray
     *          an array of arguments
     */
    public void debug(String format, Object... argArray) {
        formatAndLog(DummyLog.DEBUG, format, argArray);
    }

    /**
     * Log an exception (throwable) at level DEBUG with an accompanying message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void debug(String msg, Throwable t) {
        log(DummyLog.VERBOSE, msg, t);
    }

    /**
     * Is this logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level, false otherwise.
     */
    public boolean isInfoEnabled() {
        return isLoggable(DummyLog.INFO);
    }

    /**
     * Log a message object at the INFO level.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void info(String msg) {
        log(DummyLog.INFO, msg, null);
    }

    /**
     * Log a message at level INFO according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void info(String format, Object arg) {
        formatAndLog(DummyLog.INFO, format, arg);
    }

    /**
     * Log a message at the INFO level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(DummyLog.INFO, format, arg1, arg2);
    }

    /**
     * Log a message at level INFO according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void info(String format, Object... argArray) {
        formatAndLog(DummyLog.INFO, format, argArray);
    }

    /**
     * Log an exception (throwable) at the INFO level with an accompanying
     * message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void info(String msg, Throwable t) {
        log(DummyLog.INFO, msg, t);
    }

    /**
     * Is this logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level, false
     *         otherwise.
     */
    public boolean isWarnEnabled() {
        return isLoggable(DummyLog.WARN);
    }

    /**
     * Log a message object at the WARN level.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void warn(String msg) {
        log(DummyLog.WARN, msg, null);
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void warn(String format, Object arg) {
        formatAndLog(DummyLog.WARN, format, arg);
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(DummyLog.WARN, format, arg1, arg2);
    }

    /**
     * Log a message at level WARN according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void warn(String format, Object... argArray) {
        formatAndLog(DummyLog.WARN, format, argArray);
    }

    /**
     * Log an exception (throwable) at the WARN level with an accompanying
     * message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void warn(String msg, Throwable t) {
        log(DummyLog.WARN, msg, t);
    }

    /**
     * Is this logger instance enabled for level ERROR?
     *
     * @return True if this Logger is enabled for level ERROR, false otherwise.
     */
    public boolean isErrorEnabled() {
        return isLoggable(DummyLog.ERROR);
    }

    /**
     * Log a message object at the ERROR level.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void error(String msg) {
        log(DummyLog.ERROR, msg, null);
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void error(String format, Object arg) {
        formatAndLog(DummyLog.ERROR, format, arg);
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(DummyLog.ERROR, format, arg1, arg2);
    }

    /**
     * Log a message at level ERROR according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void error(String format, Object... argArray) {
        formatAndLog(DummyLog.ERROR, format, argArray);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying
     * message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void error(String msg, Throwable t) {
        log(DummyLog.ERROR, msg, t);
    }

    private void formatAndLog(int priority, String format, Object... argArray) {
        if (isLoggable(priority)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            logInternal(priority, ft.getMessage(), ft.getThrowable());
        }
    }

    private void log(int priority, String message, Throwable throwable) {
        if (isLoggable(priority)) {
            logInternal(priority, message, throwable);
        }
    }

    private boolean isLoggable(int priority) {
        return DummyLog.isLoggable(name, priority);
    }

    private void logInternal(int priority, String message, Throwable throwable) {
        if (throwable != null) {
            message += '\n' + DummyLog.getStackTraceString(throwable);
        }
        DummyLog.println(priority, name, message);
    }
}