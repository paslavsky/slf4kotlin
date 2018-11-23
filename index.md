# Kotlin wrapper for [slf4j](https://www.slf4j.org/manual.html) library

[![Apache License 2.0](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/paslavsky/slf4kotlin.svg?branch=master)](https://travis-ci.org/paslavsky/slf4kotlin)
[![Download](https://api.bintray.com/packages/paslavsky/maven/slf4kotlin/images/download.svg) ](https://bintray.com/paslavsky/maven/slf4kotlin/_latestVersion)
[![Kotlin version](https://img.shields.io/badge/kotlin-1.3.10-blue.svg)](https://kotlinlang.org/)

Slf4kotlin provide simple API to write logs in Kotlin way:
```kotlin
import com.github.paslavsky.logDebug

fun sayHello() {
    logDebug { "Hello again" }
}
```
You don't need to use `LoggerFactory` to create new logger and save them into some class fields. 
Slf4Kotlin provides few extension functions that applicable for any _not null_ object:

* `logError`
* `logWarning`
* `logInfo`
* `logDebug`
* `logTrace`
* `logSuccess`
* `todo`

---
* Getting started with [Gradle](gradle.md)
* Getting started with [Maven](maven.md)