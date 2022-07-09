plugins { id("com.gradle.enterprise") version ("3.10") }

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    capture { isTaskInputFiles = true }
  }
}

include(":benchmark")

include(":catalog")

include(":common")

include(":contrib:barcode")

include(":datacapture")

include(":demo")

include(":engine")

include(":workflow")

include(":testing")
