package com.github.paslavsky

import org.junit.After
import org.junit.Test

import org.junit.Before
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.concurrent.TimeUnit
import kotlin.test.*

class LoggingTest {
    private lateinit var output: ByteArrayOutputStream

    companion object {
        val originalOut = System.out!!
        val originalErr = System.err!!

        init {
            System.setProperty("org.slf4j.simpleLogger.warnLevelString", "WARN")
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
            System.setProperty("org.slf4j.simpleLogger.logFile", "System.out")
        }
    }

    @Before
    fun setUp() {
        output = ByteArrayOutputStream()
        val printStream = PrintStream(output)
        System.setOut(printStream)
        System.setErr(printStream)
    }

    @After
    fun tearDown() {
        System.setOut(originalOut)
        System.setErr(originalErr)
    }

    @Test
    fun getLogger() {
        assertNotNull(this.logger)
    }

    @Test
    fun logger() {
        assertNotNull(logger(LoggingTest::class))
    }

    @Test
    fun logger1() {
        assertNotNull(logger(javaClass))
    }

    @Test
    fun logger2() {
        assertNotNull(logger("Test"))
    }

    @Test
    fun logError() {
        logError {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("ERROR") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logError1() {
        logError(RuntimeException(">>>EXCEPTION<<<")) {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("ERROR") &&
                    wroteText.contains(">>>EXCEPTION<<<") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logWarning() {
        logWarning {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("WARN") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logWarning1() {
        logWarning(RuntimeException(">>>EXCEPTION<<<")) {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("WARN") &&
                    wroteText.contains(">>>EXCEPTION<<<") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logDebug() {
        logDebug {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("DEBUG") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logDebug1() {
        logDebug(RuntimeException(">>>EXCEPTION<<<")) {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("DEBUG") &&
                    wroteText.contains(">>>EXCEPTION<<<") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logTrace() {
        logTrace {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("TRACE") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logInfo() {
        logInfo {
            ">>>MESSAGE<<<"
        }
        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("INFO") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logSuccess() {
        var testArray: Array<Int>? = null
        logSuccess(">>>MESSAGE<<<") {
            // Some dummy actions
            testArray = Array(10) {
                it * it
            }
        }

        // Check that testArray is not null.
        // In this case, we can be sure that the body function was called
        assertNotNull(testArray)

        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("INFO") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun logSuccess1() {
        var testArray: Array<Int>? = null
        logSuccess({ ">>>MESSAGE<<<" }) {
            // Some dummy actions
            testArray = Array(10) {
                it * it
            }
        }

        // Check that testArray is not null.
        // In this case, we can be sure that the body function was called
        assertNotNull(testArray)

        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("INFO") &&
                    wroteText.contains(">>>MESSAGE<<<")
        }
    }

    @Test
    fun todo() {
        var testArray: Array<Int>? = null
        todo(">>>TODO-MESSAGE<<<") {
            // Some dummy actions
            testArray = Array(10) {
                it * it
            }
        }

        // Check that testArray is null.
        // In this case, we can be sure that the function was NOT called
        assertNull(testArray)

        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("WARN") &&
                    wroteText.contains("TODO") &&
                    wroteText.contains(">>>TODO-MESSAGE<<<")
        }
    }

    @Test
    fun logTime() {
        logTime("PROCESS_NAME") {
            // Some dummy actions
            TimeUnit.SECONDS.sleep(1)
        }

        val wroteText = output.toString()
        assertEquals("", wroteText)
        assertTrue {
            wroteText.contains("DEBUG") &&
                    wroteText.contains("PROCESS_NAME") &&
                    wroteText.contains("took 1 second(s)")
        }
    }

    @Test
    fun logTimeInfo() {
        logTime("PROCESS_NAME", LogLevel.Info) {
            // Some dummy actions
            TimeUnit.SECONDS.sleep(1)
        }

        assertTrue {
            val wroteText = output.toString()
            wroteText.contains("INFO") &&
                    wroteText.contains("PROCESS_NAME") &&
                    wroteText.contains("took 1 second(s)")
        }
    }

    @Test
    fun logTimeFail() {
        try {
            logTime("PROCESS_NAME", LogLevel.Info) {
                // Some dummy actions
                TimeUnit.SECONDS.sleep(1)
                throw UnsupportedOperationException()
            }
            @Suppress("UNREACHABLE_CODE")
            fail()
        } catch (e: UnsupportedOperationException) {
            assertTrue {
                val wroteText = output.toString()
                wroteText.contains("WARN") &&
                        wroteText.contains("PROCESS_NAME") &&
                        wroteText.contains("execution failed after 1 second(s)")
            }
        }
    }
}