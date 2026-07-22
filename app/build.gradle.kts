plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "pro.udeedit.devtools.pushestest"
    compileSdk = 36

    defaultConfig {
        applicationId = "pro.udeedit.devtools.pushestest"
        minSdk = 24
        targetSdk = 36
        versionCode = 7
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // vc7, 2.1.0 - replace cushystorage module with maven dependency
        // vc6, 2.0.0 - ready transferred to compose version
        // vc5, 2.0.0 - feat(PT-35): complete migration to Jetpack Compose and MVI architecture
//        - Migrated entire UI from XML layouts to Jetpack Compose with Material 3.
//        - Implemented MVI (Model-View-Intent) pattern with a centralized SettingsState.
//        - using Clean Architecture paradigm (domain/use cases)
//        - Integrated Hilt for dependency injection and KSP for modern annotation processing.
//        - Refactored notification logic into a Hilt-injected PublishNotificationUseCase.
//        - Added responsive 'widthIn' constraints for centered Tablet (sw600dp) optimization.
//        - Implemented 'Live' spring animations and haptic feedback for all UI interactions.
//        - Stabilized Full-Screen Intent (FSI) logic for Android 10 (Samsung) and Android 15 (Xiaomi).
//        - Consolidated system-level logic (FSI, Haptics, Permissions) into MVI Flow.
//        - Added comprehensive KDoc documentation across all logic and UI components.
//        - Integrated GitHub Actions CI/CD pipeline and expanded Unit/UI test coverage.
//        - Removed all legacy XML layouts, menus, and ViewBinding.

        // vc4, 1.2.0 - first release ready cleanup
        // vc3, 1.1.1 - add CushyStorage module
        // vc2, 1.1.0 - work on epic PT-11
        // vc1, 1.0.0 - create develop branch
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    testOptions {
        unitTests {
            // Prevents "Method not mocked" crashes
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    implementation(libs.google.hilt.android)
    // For Hilt + ViewModel + Compose integration
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.google.hilt.compiler) // Use ksp configuration

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("pro.udeedit.devtools:cushystorage:1.0.3")
    implementation(project(":anarchist"))
}