import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
    kotlin("android")
}

group = "io.github.segnities007"
version = "0.1.0"

android {
    namespace = "com.segnities007.stylishui"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.foundation)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui.tooling.preview)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = "io.github.segnities007",
        artifactId = "stylish-ui",
        version = version.toString(),
    )

    pom {
        name = "Stylish UI"
        description = "Compose UI components for Stylish My Vehicles."
        url = "https://github.com/segnities007/StylishMyVehicles"
        inceptionYear = "2026"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "segnities007"
                name = "segnities007"
                url = "https://github.com/segnities007"
            }
        }

        scm {
            url = "https://github.com/segnities007/StylishMyVehicles"
            connection = "scm:git:git://github.com/segnities007/StylishMyVehicles.git"
            developerConnection = "scm:git:ssh://git@github.com:segnities007/StylishMyVehicles.git"
        }
    }
}
