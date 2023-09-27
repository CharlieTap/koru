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

    sourceSets {
        val linuxMain by getting {
            dependencies {
                implementation(libs.result)
                implementation(projects.lib)
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
