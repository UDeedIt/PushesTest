// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    dependencies {
        // This forces Hilt to use a version of javapoet that has the canonicalName() method
        // needed for correcting crashes appeared due to hilt/ksp versions conflicts
        classpath("com.squareup:javapoet:1.13.0")
    }
}
