plugins {
  id(Plugins.BuildPlugins.javaLibrary)
  id(Plugins.BuildPlugins.kotlin)
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  implementation(Dependencies.kotlinPoet)
}
