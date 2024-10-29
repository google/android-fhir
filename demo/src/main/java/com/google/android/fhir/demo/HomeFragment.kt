/*
 * Copyright 2022-2024 Google LLC
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

package com.google.android.fhir.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment(R.layout.fragment_home) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = resources.getString(R.string.app_name)
      setDisplayHomeAsUpEnabled(false)
    }
    setOnClicks()
  }

  private fun setOnClicks() {
    requireView().findViewById<CardView>(R.id.item_patient_list).setOnClickListener {
      findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPatientList())
    }
    requireView().findViewById<CardView>(R.id.item_search).setOnClickListener {
      findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPatientList())
    }
    requireView().findViewById<CardView>(R.id.item_sync).setOnClickListener {
      findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSyncFragment())
    }
  }
}
