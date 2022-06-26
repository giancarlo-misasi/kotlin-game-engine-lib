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
                // implementation(project.dependencies.platform("$lwjglBase:lwjgl-bom:$lwjglVersion"))
                val lwjglModules = arrayOf("lwjgl", "lwjgl-opengl", "lwjgl-glfw")
                val lwjglClassifier = when {
                    hostOs.isWindows -> "natives-windows"
                    hostOs.isLinux -> "natives-linux"
                    hostOs.isMacOsX -> "natives-macos"
                    else -> throw GradleException("Host OS is not supported in lwjgl.")
                }
                lwjglModules.map { "org.lwjgl:$it:$lwjglVersion" }.forEach {
                    implementation(it)
                    runtimeOnly("$it:$lwjglClassifier")
                }
            }
        }
        val jvmTest by getting
    }
}