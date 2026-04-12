import java.util.Locale

import org.gradle.api.GradleException
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    jacoco
}

jacoco {
    toolVersion = "0.8.11"
}

android {
    namespace = "com.example.ticketreservationapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.ticketreservationapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }

    val classFiles = fileTree("$buildDir/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*"
        )
    }

    classDirectories.setFrom(files(classFiles))
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        fileTree(buildDir) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
            )
        }
    )
}

tasks.register("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")

    doLast {
        val reportFile = file("$buildDir/reports/jacoco/jacocoTestReport/jacocoTestReport.csv")
        if (!reportFile.exists()) {
            throw GradleException("Jacoco report not found at ${reportFile.absolutePath}")
        }

        val reportLines = reportFile.readLines().filter { it.isNotBlank() }
        if (reportLines.size < 2) {
            throw GradleException("No line coverage data was found in the Jacoco report")
        }

        val header = reportLines.first().split(',')
        val lineMissedIndex = header.indexOf("LINE_MISSED")
        val lineCoveredIndex = header.indexOf("LINE_COVERED")
        if (lineMissedIndex == -1 || lineCoveredIndex == -1) {
            throw GradleException("No line coverage data was found in the Jacoco report")
        }

        var missed = 0L
        var covered = 0L

        for (dataLine in reportLines.drop(1)) {
            val values = dataLine.split(',')
            if (values.size <= maxOf(lineMissedIndex, lineCoveredIndex)) {
                continue
            }

            missed += values[lineMissedIndex].toLongOrNull()
                ?: throw GradleException("No line coverage data was found in the Jacoco report")
            covered += values[lineCoveredIndex].toLongOrNull()
                ?: throw GradleException("No line coverage data was found in the Jacoco report")
        }

        val total = covered + missed
        if (total == 0L) {
            throw GradleException("No line coverage data was found in the Jacoco report")
        }

        val coveragePercent = covered.toDouble() * 100.0 / total.toDouble()
        logger.lifecycle("Debug unit test coverage: ${String.format(Locale.US, "%.2f", coveragePercent)}%")

        if (coveragePercent < 80.0) {
            throw GradleException(
                "Debug unit test coverage ${String.format(Locale.US, "%.2f", coveragePercent)}% is below the required 80.00%"
            )
        }
    }
}

tasks.matching { it.name == "testDebugUnitTest" }.configureEach {
    finalizedBy("jacocoTestReport")
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn("jacocoTestCoverageVerification")
}



dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.common)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation("com.google.android.gms:play-services-base:18.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")

}