plugins {
  id(Plugins.BuildPlugins.javaLibrary)
  id(Plugins.BuildPlugins.kotlin)
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation(Dependencies.HapiFhir.structuresR4)
  implementation(Dependencies.kotlinPoet)
}

tasks.create<JavaExec>("runCodeGenerator") {
  group = "kotlinpoet"
  classpath = sourceSets.getByName("main").runtimeClasspath
  mainClass.set("com.google.android.fhir.codegen.SearchParameterRepositoryGeneratorKt")
  run {
    args =
      mutableListOf(
        "${project.rootDir.absolutePath}/codegen/src/main/res/search-parameters.json",
        "${project.rootDir.absolutePath}/engine/src/main/java",
        "${project.rootDir.absolutePath}engine/src/test/java"
      )
  }
  finalizedBy(":spotlessGenerated")
}
