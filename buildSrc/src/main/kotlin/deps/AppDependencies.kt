package deps

object AppDependencies {

  object CoreDeps {
    const val activity = "androidx.activity:activity:${versions.AppDependencies.CoreDeps.activity}"
    const val appCompat = "androidx.appcompat:appcompat:${versions.AppDependencies.CoreDeps.appCompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${versions.AppDependencies.CoreDeps.constraintLayout}"
    const val fragment = "androidx.fragment:fragment-ktx:${versions.AppDependencies.CoreDeps.fragment}"
    const val desugar = "com.android.tools:desugar_jdk_libs:${versions.AppDependencies.CoreDeps.desugar}"
    const val materialDesign = "com.google.android.material:material:${versions.AppDependencies.CoreDeps.materialDesign}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${versions.AppDependencies.CoreDeps.recyclerView}"
    const val work = "androidx.work:work-runtime-ktx:${versions.AppDependencies.CoreDeps.work}"

    object Room {
      const val compiler = "androidx.room:room-compiler:${versions.AppDependencies.CoreDeps.room}"
      const val ktx = "androidx.room:room-ktx:${versions.AppDependencies.CoreDeps.room}"
      const val runtime = "androidx.room:room-runtime:${versions.AppDependencies.CoreDeps.room}"
    }

    object Cql {
      const val cqlEngineCore = "org.opencds.cqf:cql-engine:${versions.AppDependencies.CoreDeps.Cql.cqlEngine}"
      const val cqlEngineFhir = "org.opencds.cqf:cql-engine-fhir:${versions.AppDependencies.CoreDeps.Cql.cqlEngine}"
      const val hapiR4 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${versions.AppDependencies.CoreDeps.Cql.hapiR4}"
    }

  }

  object Kotlin {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${versions.AppDependencies.Kotlin.kotlin}"
    const val kotlinTesting = "org.jetbrains.kotlin:kotlin-test-junit:${versions.AppDependencies.Kotlin.kotlin}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${versions.AppDependencies.Kotlin.androidxCoreKtx}"
    const val coreKtCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.AppDependencies.Kotlin.coreKtCoroutines}"
    const val androidCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.AppDependencies.Kotlin.coreKtCoroutines}"
  }

  object Navigation {
    const val navFragment = "androidx.navigation:navigation-fragment-ktx:${versions.AppDependencies.CoreDeps.navigation}"
    const val navUi = "androidx.navigation:navigation-ui-ktx:${versions.AppDependencies.CoreDeps.navigation}"
  }

  object Lifecycle {
    const val extensions = "androidx.lifecycle:lifecycle-extensions:${versions.AppDependencies.CoreDeps.lifecyle}"
    const val liveDataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:${versions.AppDependencies.CoreDeps.lifecyle}"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${versions.AppDependencies.CoreDeps.lifecyle}"
    const val runtime = "androidx.lifecycle:lifecycle-runtime:${versions.AppDependencies.CoreDeps.lifecyle}"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.AppDependencies.CoreDeps.lifecyle}"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.AppDependencies.CoreDeps.lifecyle}"
  }

  object Networking {
    const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:${versions.AppDependencies.Networking.httpInterceptor}"
    const val coreRetrofit = "com.squareup.retrofit2:retrofit:${versions.AppDependencies.Networking.retrofit}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${versions.AppDependencies.Networking.retrofit}"
    const val mockRetrofit = "com.squareup.retrofit2:retrofit-mock:${versions.AppDependencies.Networking.retrofit}"
  }

  object Externals {
  const val jsonTools = "com.github.java-json-tools:json-patch:${versions.AppDependencies.Externals.jsonTools}"
  const val caffeine = "com.github.ben-manes.caffeine:caffeine:${versions.AppDependencies.Externals.caffeine}"
  const val guava = "com.google.guava:guava:${versions.AppDependencies.Externals.guava}"
  }
}
