package com.google.android.fhir.implementationguide.npm

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import org.hl7.fhir.r4.model.ImplementationGuide
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NpmPackageManagerTest {

  @Test
  fun smoketest() {
    val cacheFolderPath = ApplicationProvider.getApplicationContext<Context>().cacheDir.absolutePath
      NpmPackageManager.fromResource(cacheFolderPath, ImplementationGuide(), "4.0.1", "https://packages.fhir.org", "https://packages.simplifier.net")
  }
}