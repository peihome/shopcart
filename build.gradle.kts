buildscript {
    val kotlin_version by extra("1.9.0")
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
    repositories {
        mavenCentral()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}