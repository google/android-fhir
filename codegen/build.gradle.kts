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

tasks.create<JavaExec>("runCodeGenerator") {
  group = "kotlinpoet"
  classpath = sourceSets.getByName("main").runtimeClasspath
  mainClass.set("com.google.android.fhir.codegen.SearchParameterRepositoryGeneratorKt")
  run {
    args =
      mutableListOf(
        project.rootDir.absolutePath + "/codegen/src/main/res/search-parameters.json",
        project.rootDir.absolutePath + "/engine/src/main/java"
      )
  }
}
