```groovy
buildscript {
    ext.kotlin_version = '1.3.10'

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'kotlin'

repositories {
    mavenCentral()
    maven { url "https://dl.bintray.com/paslavsky/maven" }
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib"
    compile 'com.github.paslavsky:slf4kotlin:0.0.1'
}
```