package com.github.paslavsky

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import java.util.concurrent.TimeUnit

typealias LogMethod = Logger.(String) -> Unit

private val iError: LogMethod = { error(it) }
private val iWarning: LogMethod = { warn(it) }
private val iInfo: LogMethod = { info(it) }
private val iDebug: LogMethod = { debug(it) }
private val iTrace: LogMethod = { trace(it) }

private val loggingMethods: Map<LogLevel, LogMethod> by lazy {
    mapOf(
            LogLevel.Error to iError,
            LogLevel.Warning to iWarning,
            LogLevel.Info to iInfo,
            LogLevel.Debug to iDebug,
            LogLevel.Trace to iTrace
    )
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

fun <T> Any.logSuccess(lazyMessage: () -> String, body: () -> T): T = body().also {
    if (logger.isInfoEnabled) {
        logger.info(lazyMessage())
    }
}

fun <T> Any.logSuccess(message: String, body: () -> T): T = body().also {
    if (this.logger.isInfoEnabled) {
        this.logger.info(message)
    }
}

fun <T> Any.todo(message: String, @Suppress("UNUSED_PARAMETER") block: () -> T) {
    if (this.logger.isWarnEnabled) {
        this.logger.warn("TODO $message \n\t ${Exception().stackTrace[1]}")
    }
}

fun <T> Any.logTime(name: String, level: LogLevel = LogLevel.Debug, block: () -> T): T {
    val start = System.currentTimeMillis()
    return try {
        block().also {
            loggingMethods[level]!!.invoke(logger, "The execution of the $name took ${tookFrom(start)}")
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
