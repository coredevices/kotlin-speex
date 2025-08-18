import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "coredevices.speex"
version = "1.0.0"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/coredevices/kotlin-speex")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

android {
    externalNativeBuild {
        cmake {
            path("${rootDir}/speex/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    macosArm64 {
        compilations.getByName("main") {
            cinterops {
                val speex by creating {
                    extraOpts("-libraryPath", "${rootDir}/speex/lib/macos")
                    compilerOpts.add("-I${rootDir}/speex/include")
                    compilerOpts.add("-I${rootDir}/speex/source/include")
                }
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.compilations.getByName("main") {
            cinterops {
                val speex by creating {
                    extraOpts("-libraryPath", "${rootDir}/speex/lib/ios")
                    compilerOpts.add("-I${rootDir}/speex/include")
                    compilerOpts.add("-I${rootDir}/speex/source/include")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.io)
            }
        }
    }
}

android {
    namespace = "coredevices.speex"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    coordinates(group.toString(), "speex", version.toString())
}
