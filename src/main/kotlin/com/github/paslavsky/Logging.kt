package com.github.paslavsky

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

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
