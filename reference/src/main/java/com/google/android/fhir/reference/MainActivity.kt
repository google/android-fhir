/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.reference

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import com.google.android.fhir.reference.data.FhirPeriodicSyncWorker
import com.google.android.fhir.reference.databinding.ActivityMainBinding
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.Sync
import java.util.concurrent.TimeUnit

const val MAX_RESOURCE_COUNT = 20

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    val toolbar = binding.toolbar
    setSupportActionBar(toolbar)
    toolbar.title = title

    Sync.periodicSync<FhirPeriodicSyncWorker>(
      this,
      PeriodicSyncConfiguration(
        syncConstraints = Constraints.Builder().build(),
        repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
      )
    )
  }
}
