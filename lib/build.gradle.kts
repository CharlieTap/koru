import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val linuxTarget = when {
        hostOs == "Linux" -> when (System.getProperty("os.arch")) {
            "aarch64" -> linuxArm64("linux")
            else -> linuxX64("linux")
        }
        else -> throw GradleException(" This project uses linux only apis.")
    }

    targets.configureEach {
        compilations.configureEach {
            kotlinOptions {
                freeCompilerArgs += listOf("-Xcontext-receivers")
            }
        }
    }

    val nativeLinkerOpts = mutableListOf<String>()

    linuxTarget.apply {

        compilations.getByName("main") {
            cinterops {
                val libio by creating {
                    defFile(project.file("src/ffi/cinterop/lib.def"))
                }

                val liburing by creating   {
                    defFile(project.file("src/ffi/cinterop/liburing.def"))
                }
            }
        }

        binaries {
            executable {
                baseName = "koru"
                entryPoint = "main"
                linkerOpts = nativeLinkerOpts
            }
        }
    }

    sourceSets {
        val linuxMain by getting {
            dependencies {
                implementation(projects.sys)
            }
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        val linuxTest by getting {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(listOf())
    }
}
