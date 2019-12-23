package com.github.paslavsky

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import java.util.concurrent.TimeUnit

typealias LogMethod = Logger.(String) -> Unit
typealias LogMethodProvider = (LogLevel) -> LogMethod

private val loggingMethods: LogMethodProvider = {
    when (it) {
        LogLevel.Trace -> Logger::trace
        LogLevel.Debug -> Logger::debug
        LogLevel.Info -> Logger::info
        LogLevel.Warning -> Logger::warn
        LogLevel.Error -> Logger::error
    }
}

enum class LogLevel { Error, Warning, Info, Debug, Trace }

inline val Any.logger: Logger get() = logger(javaClass)

fun logger(targetClass: KClass<*>): Logger = LoggerFactory.getLogger(targetClass.java)
fun logger(targetClass: Class<*>): Logger = LoggerFactory.getLogger(targetClass)
fun logger(logger: String): Logger = LoggerFactory.getLogger(logger)

fun Any.logError(e: Throwable, message: () -> String = { e.message ?: "" }) {
    if (this.logger.isErrorEnabled) {
        this.logger.error(message(), e)
    }
}

fun Any.logError(message: () -> String) {
    if (this.logger.isErrorEnabled) {
        this.logger.error(message())
    }
}

fun Any.logWarning(message: () -> String) {
    if (this.logger.isWarnEnabled) {
        this.logger.warn(message())
    }
}

fun Any.logWarning(e: Throwable, message: () -> String = { e.message ?: "" }) {
    if (this.logger.isWarnEnabled) {
        this.logger.warn(message(), e)
    }
}

fun Any.logDebug(message: () -> String) {
    if (this.logger.isDebugEnabled) {
        this.logger.debug(message())
    }
}

fun Any.logDebug(e: Throwable, message: () -> String = { e.message ?: "" }) {
    if (this.logger.isDebugEnabled) {
        this.logger.debug(message(), e)
    }
}

fun Any.logTrace(lazyMessage: () -> String) {
    if (this.logger.isTraceEnabled) {
        this.logger.trace(lazyMessage())
    }
}

fun Any.logInfo(message: () -> String) {
    if (this.logger.isInfoEnabled) {
        this.logger.info(message())
    }
}

fun <T> Any.logSuccess(lazyMessage: () -> String, level: LogLevel = LogLevel.Info, body: () -> T): T =
        try {
            body().also {
                loggingMethods(level).invoke(logger, lazyMessage())
            }
        } catch (e: Exception) {
            logWarning { "Failed: ${lazyMessage()}" }
            throw e
        }

fun <T> Any.logSuccess(message: String, level: LogLevel = LogLevel.Info, body: () -> T): T =
        logSuccess({ message }, level, body)

fun <T> Any.todo(message: String, @Suppress("UNUSED_PARAMETER") block: () -> T) {
    if (this.logger.isWarnEnabled) {
        this.logger.warn("TODO $message \n\t ${Exception().stackTrace[1]}")
    }
}

fun <T> Any.logTime(name: String, level: LogLevel = LogLevel.Debug, block: () -> T): T {
    val start = System.currentTimeMillis()
    return try {
        block().also {
            loggingMethods(level).invoke(logger, "The execution of the $name took ${tookFrom(start)}")
        }
    } catch (e: Exception) {
        logWarning { "$name execution failed after ${tookFrom(start)}" }
        throw e
    }
}

private fun tookFrom(start: Long) = (System.currentTimeMillis() - start).let {
    when {
        it >= TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toDays(it)} day(s) and ${TimeUnit.MILLISECONDS.toHours(it)} hour(s)"
        it >= TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(it)} hour(s) and ${TimeUnit.MILLISECONDS.toMinutes(it)} minute(s)"
        it >= TimeUnit.MINUTES.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(it)} minute(s) and ${TimeUnit.MILLISECONDS.toSeconds(it)} second(s)"
        it >= TimeUnit.SECONDS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toSeconds(it)} second(s) and ${it % 1000} milliseconds"
        else -> "$it milliseconds"
    }
}
