plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.kotlin.multiplatform.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  jvmToolchain(21)

  androidLibrary {
    namespace = "com.google.android.fhir.engine"
    compileSdk = Sdk.COMPILE_SDK
    minSdk = Sdk.MIN_SDK
    withHostTestBuilder {}
  }

  jvm("desktop")

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  targets.configureEach {
    compilations.configureEach {
      compilerOptions.configure {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
          "kotlin.time.ExperimentalTime",
          "kotlin.uuid.ExperimentalUuidApi",
        )
      }
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlin.fhir)
        implementation(libs.fhir.path)
        implementation(libs.kermit)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.sqlite.bundled)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.encoding)
        implementation(libs.ktor.client.auth)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
    }
    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)
      }
    }
    val androidMain by getting {
      dependencies {
        implementation(libs.androidx.work.runtime)
        implementation(libs.androidx.lifecycle.livedata)
        implementation(libs.ktor.client.okhttp)
      }
    }
    val desktopMain by getting {
      dependencies {
        implementation(libs.ktor.client.java)
      }
    }
    iosMain {
      dependencies {
        implementation(libs.ktor.client.darwin)
      }
    }
    getByName("androidHostTest") {
      dependencies {
        implementation(libs.junit)
        implementation(libs.robolectric)
        implementation(libs.androidx.test.core)
        implementation(libs.androidx.work.testing)
        implementation(libs.kotlin.test.junit)
        implementation(libs.kotlinx.coroutines.test)
      }
    }
  }
}

dependencies {
  listOf(
    "kspAndroid",
    "kspDesktop",
    "kspIosX64",
    "kspIosArm64",
    "kspIosSimulatorArm64",
  ).forEach {
    add(it, libs.androidx.room.compiler)
  }
}
