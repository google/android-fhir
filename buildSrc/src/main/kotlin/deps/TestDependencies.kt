package deps

object TestDependencies {

  object AndroidxTest {
    const val core = "androidx.test:core:${versions.TestDependencies.Androidx.core}"
    const val extJunit = "androidx.test.ext:junit:${versions.TestDependencies.Androidx.extJunit}"
    const val extJunitKtx =
      "androidx.test.ext:junit-ktx:${versions.TestDependencies.Androidx.extJunit}"
    const val junit = "junit:junit:${versions.TestDependencies.Androidx.junit}"
    const val rules = "androidx.test:rules:${versions.TestDependencies.Androidx.rules}"
    const val runner = "androidx.test:runner:${versions.TestDependencies.Androidx.runner}"
  }

  const val truth = "com.google.truth:truth:${versions.TestDependencies.truth}"

  object Espresso {
    const val espresso =
      "androidx.test.espresso:espresso-core:${versions.TestDependencies.Espresso.espresso}"
  }
  const val roboelectric = "org.robolectric:robolectric:${versions.TestDependencies.Espresso.roboelectric}"
  const val standardRunner = "androidx.test.runner.AndroidJUnitRunner"
}
