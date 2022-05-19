/*
 * Copyright 2021 Google LLC
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

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.demo.databinding.ActivityLoginBinding
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLoginBinding
  private val viewModel: LoginActivityViewModel by viewModels()

  private val getContent =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      lifecycleScope.launchWhenResumed {
        if (result.resultCode == RESULT_OK) {
          Timber.i("Exchange for token")
          val response = result.data?.let { AuthorizationResponse.fromIntent(it) }
          val ex = AuthorizationException.fromIntent(result.data)
          viewModel.handleResponse(response, ex)
          val mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
          startActivity(mainActivityIntent)
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
    lifecycleScope.launchWhenResumed {
      val intent = viewModel.createIntent()
      binding.buttonLogin.setOnClickListener { getContent.launch(intent) }
    }
  }
}
