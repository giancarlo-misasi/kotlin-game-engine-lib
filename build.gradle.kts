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

plugins {
    kotlin("multiplatform") version "1.7.0"
}

group = "me.gianc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinStdlibVersion = "1.7.0"
val lwjglVersion = "3.3.1"
val hostOs: OperatingSystem = OperatingSystem.current()
val hostOsName = when {
    hostOs.isWindows -> "windows"
    hostOs.isLinux -> "linux"
    hostOs.isMacOsX -> "macos"
    else -> throw GradleException("Host OS is not supported.")
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
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinStdlibVersion")

                // https://www.lwjgl.org/customize
                arrayOf("lwjgl", "lwjgl-opengl", "lwjgl-glfw").map { "org.lwjgl:$it:$lwjglVersion" }.forEach {
                    implementation(it)
                    runtimeOnly("$it:natives-$hostOsName")
                }
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}