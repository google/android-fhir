package versions

object AppDependencies {

  object CoreDeps {
    const val activity = "1.2.1"
    const val appCompat = "1.1.0"
    const val constraintLayout = "1.1.3"
    const val fragment = "1.3.1"
    const val desugar = "1.0.9"
    const val lifecyle = "2.2.0"
    const val materialDesign = "1.3.0"
    const val recyclerView = "1.1.0"
    const val room = "2.2.5"
    const val work = "2.3.4"
    const val navigation = "2.3.4"

    object Cql {
      const val cqlEngine = "1.3.14-SNAPSHOT"
      const val hapiR4 = "5.3.0"
    }
  }

  object Kotlin {
    const val kotlin = "1.4.30"
    const val androidxCoreKtx = "1.2.0"
    const val coreKtCoroutines = "1.4.2"
  }

  object Networking {
    const val httpInterceptor = "4.0.0"
    const val retrofit = "2.7.2"
  }

  object Externals {
    const val jsonTools = "1.13"
  }
}
