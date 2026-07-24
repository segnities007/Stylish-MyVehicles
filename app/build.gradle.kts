plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.aboutlibraries)
    jacoco
    kotlin("android")
}

android {
    namespace = "com.segnities007.stylish_myvehicles"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.segnities007.stylish_myvehicles"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testCoverage {
        jacocoVersion = "0.8.12"
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":stylish-ui"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil.compose)
    implementation(libs.mlkit.text.recognition.japanese)
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*",
        "**/databinding/**", "**/DataBinderMapperImpl*.*",
        // Composable UI（スクリーンショットテスト向き）
        "**/*Screen*.class", "**/*ScreenKt*.class",
        "**/components/**", "**/theme/**",
        "**/*Layout*.class", "**/*LayoutKt*.class",
        // Android フレームワーク（計装テスト向き）
        "**/app/StylishMyVehiclesApp*.class",
        "**/app/MainActivity*.class",
        "**/app/di/**",
        "**/navigation/**",
        "**/data/trip/TripTrackingService*.class",
        "**/data/notification/**",
        "**/data/worker/**",
        "**/data/ocr/ReceiptScanner.class",
        // Room 生成コード・DB（計装テスト向き）
        "**/data/local/dao/**",
        "**/data/local/AppDatabase*.class",
        "**/data/local/Converters*.class",
        // Repository 実装（Room DAO への薄委譲、計装テスト向き）
        "**/data/repository/**",
    )
    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

tasks.register<JacocoCoverageVerification>("jacocoVerify") {
    dependsOn("jacocoTestReport")

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*",
        "**/databinding/**", "**/DataBinderMapperImpl*.*",
        "**/*Screen*.class", "**/*ScreenKt*.class",
        "**/components/**", "**/theme/**",
        "**/*Layout*.class", "**/*LayoutKt*.class",
        "**/app/StylishMyVehiclesApp*.class",
        "**/app/MainActivity*.class",
        "**/app/di/**",
        "**/navigation/**",
        "**/data/trip/TripTrackingService*.class",
        "**/data/notification/**",
        "**/data/worker/**",
        "**/data/ocr/ReceiptScanner.class",
        "**/data/local/dao/**",
        "**/data/local/AppDatabase*.class",
        "**/data/local/Converters*.class",
        "**/data/repository/**",
    )
    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })

    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}
