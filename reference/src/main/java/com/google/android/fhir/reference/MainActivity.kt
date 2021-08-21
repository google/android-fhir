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
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import com.google.android.fhir.reference.data.FhirPeriodicSyncWorker
import com.google.android.fhir.reference.databinding.ActivityMainBinding
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.State
import com.google.android.fhir.sync.Sync
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val MAX_RESOURCE_COUNT = 20

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private val TAG = javaClass.name

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    val toolbar = binding.toolbar
    setSupportActionBar(toolbar)
    toolbar.title = title

    val job = Sync.basicSyncJob(this)
    val flow =
      job.poll(
        PeriodicSyncConfiguration(
          syncConstraints = Constraints.Builder().build(),
          repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
        ),
        FhirPeriodicSyncWorker::class.java
      )

    lifecycleScope.launch {
      Log.i(TAG, "listening to state update with last sync ${job.lastSyncTimestamp() ?: ""}")

      flow.collect {
        when (it) {
          is State.Started -> showToast("Started sync")
          is State.InProgress -> showToast("InProgress with ${it.resourceType?.name}")
          is State.Finished -> showToast("Success at ${it.result.timestamp}")
          is State.Failed -> showToast("Failed at ${it.result.timestamp}")
        }
      }
    }
  }

  private fun showToast(message: String) {
    Log.i(TAG, "Sync service progress $message")
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}
