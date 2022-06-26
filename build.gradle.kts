/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

group = "me.gianc"
version = "1.0-SNAPSHOT"

// For changing build dependencies by build type
val buildVariant: String by project
fun dependsOn(sourceSet: KotlinSourceSet, debug: KotlinSourceSet, release: KotlinSourceSet) {
    if (buildVariant == "debug") {
        sourceSet.dependsOn(debug)
    } else {
        sourceSet.dependsOn(release)
    }
}

// Versions
val kotlinVersion = "1.7.0"
val lwjglVersion = "3.3.1"
val hostOs: OperatingSystem = OperatingSystem.current()
val hostOsName = when {
    hostOs.isWindows -> "windows"
    hostOs.isLinux -> "linux"
    hostOs.isMacOsX -> "macos"
    else -> throw GradleException("Host OS is not supported.")
}

plugins {
    kotlin("multiplatform") version "1.7.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
            }
        }
        val commonDebug by creating {
            dependencies {
                dependsOn(commonMain)
            }
        }
        val commonRelease by creating {
            dependencies {
                dependsOn(commonMain)
            }
        }
        val commonTest by getting {
            val that = this
            dependencies {
                dependsOn(commonMain)
                dependsOn(that, commonDebug, commonRelease)

                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            val that = this
            dependencies {
                dependsOn(commonMain)
                dependsOn(that, commonDebug, commonRelease)

                // https://www.lwjgl.org/customize
                arrayOf("lwjgl", "lwjgl-opengl", "lwjgl-glfw").map { "org.lwjgl:$it:$lwjglVersion" }.forEach {
                    implementation(it)
                    runtimeOnly("$it:natives-$hostOsName")
                }
            }
        }
        val jvmTest by getting {
            dependencies {
                dependsOn(jvmMain)

                implementation(kotlin("test"))
            }
        }
    }
}