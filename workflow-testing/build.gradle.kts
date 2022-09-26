plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk

  defaultConfig {
    minSdk = Sdk.minSdkWorkflow
    targetSdk = Sdk.targetSdk
  }
  compileOptions {
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }
}

dependencies {
  compileOnly(Dependencies.Cql.engine)
  compileOnly(Dependencies.Cql.evaluator)
  compileOnly(Dependencies.Cql.evaluatorBuilder)
  compileOnly(Dependencies.Cql.evaluatorDagger)
  compileOnly(Dependencies.Cql.evaluatorPlanDef)
  compileOnly(Dependencies.Cql.translatorCqlToElm)
  compileOnly(Dependencies.Cql.translatorElm)
  compileOnly(Dependencies.Cql.translatorFhirR4)
  compileOnly(Dependencies.Cql.translatorModel)
  compileOnly(Dependencies.Cql.translatorQDM)

  compileOnly(Dependencies.junit)
  compileOnly(Dependencies.jsonAssert)
  compileOnly(Dependencies.woodstox)
  compileOnly(Dependencies.xmlUnit)
  compileOnly(Dependencies.truth)
}
