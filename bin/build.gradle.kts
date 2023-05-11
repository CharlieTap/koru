import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> when (System.getProperty("os.arch")) {
            "aarch64" -> macosArm64("native")
            else -> macosX64("native")
        }
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    targets.configureEach {
        compilations.configureEach {
            kotlinOptions {
                freeCompilerArgs += listOf("-Xcontext-receivers")
            }
        }
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val liburing by creating   {
                    defFile(project.file("src/nativeInterop/cinterop/liburing.def"))
                    compilerOpts("-I/path")
                    includeDirs.allHeaders("path")
                }
            }
        }
        binaries {
            executable {
                baseName = "koru"
                entryPoint = "main"
                linkerOpts = mutableListOf("-L/usr/lib/x86_64-linux-gnu", "-luring")
            }
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {}
        }
        val nativeTest by getting
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(listOf())
    }
}
