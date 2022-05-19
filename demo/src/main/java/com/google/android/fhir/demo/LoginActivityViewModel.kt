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

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import timber.log.Timber

class LoginActivityViewModel constructor(application: Application) : AndroidViewModel(application) {
  private val loginRepository = LoginRepository.getInstance(application.applicationContext)

  suspend fun createIntent(): Intent? {
    return withContext(Dispatchers.IO) {
      loginRepository.hasConfigurationChanged()
      loginRepository.initializeAppAuth()
      loginRepository.getAuthIntent()
    }
  }

  suspend fun handleResponse(response: AuthorizationResponse?, ex: AuthorizationException?) {
    return withContext(Dispatchers.IO) {
      if (response != null || ex != null) {
        loginRepository.updateAfterAuthorization(response, ex)
      }
      when {
        response?.authorizationCode != null -> {
          loginRepository.exchangeCodeForToken(response, ex)
        }
        ex != null -> {
          Timber.e("Authorization flow failed: " + ex.message)
        }
        else -> {
          Timber.e("No authorization state retained - reauthorization required")
        }
      }
    }
  }
}
