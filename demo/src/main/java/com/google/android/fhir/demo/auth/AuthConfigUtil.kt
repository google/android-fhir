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

package com.google.android.fhir.demo.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.google.gson.Gson
import org.json.JSONObject

object AuthConfigUtil {
  private val gson = Gson()

  class InvalidConfigurationException : Exception {
    internal constructor(reason: String?) : super(reason)
    internal constructor(reason: String?, cause: Throwable?) : super(reason, cause)
  }
  private fun getStringIfNotNull(propName: String?): String? {
    if (TextUtils.isEmpty(propName)) {
      throw InvalidConfigurationException("Missing field in the configuration JSON")
    }
    return propName
  }
  fun isRequiredConfigString(propName: String?) {
    getStringIfNotNull(propName)
  }
  fun isRequiredConfigUri(propName: String?) {
    val uriStr = getStringIfNotNull(propName)
    val uri: Uri =
      try {
        Uri.parse(uriStr)
      } catch (ex: Throwable) {
        throw InvalidConfigurationException("$propName could not be parsed", ex)
      }
    if (!uri.isHierarchical || !uri.isAbsolute) {
      throw InvalidConfigurationException("$propName must be hierarchical and absolute")
    }
    if (!TextUtils.isEmpty(uri.encodedUserInfo)) {
      throw InvalidConfigurationException("$propName must not have user info")
    }
    if (!TextUtils.isEmpty(uri.encodedQuery)) {
      throw InvalidConfigurationException("$propName must not have query parameters")
    }
    if (!TextUtils.isEmpty(uri.encodedFragment)) {
      throw InvalidConfigurationException("$propName must not have a fragment")
    }
  }
  fun isRequiredConfigWebUri(propName: String?): Uri {
    isRequiredConfigUri(propName)
    val uri: Uri = Uri.parse(propName)
    val scheme = uri.scheme
    if (TextUtils.isEmpty(scheme) || !("http" == scheme || "https" == scheme)) {
      throw InvalidConfigurationException("$propName must have an http or https scheme")
    }
    return uri
  }
  fun isRedirectUriRegistered(context: Context, redirectUri: String) {
    // ensure that the redirect URI declared in the configuration is handled by some activity
    // in the app, by querying the package manager speculatively
    val redirectIntent = Intent()
    redirectIntent.setPackage(context.packageName)
    redirectIntent.action = Intent.ACTION_VIEW
    redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE)
    redirectIntent.data = Uri.parse(redirectUri)
    if (context.packageManager.queryIntentActivities(redirectIntent, 0).isEmpty()) {
      throw InvalidConfigurationException(
        "redirect_uri is not handled by any activity in this app! " +
          "Ensure that the appAuthRedirectScheme in your build.gradle file " +
          "is correctly configured, or that an appropriate intent filter " +
          "exists in your app manifest."
      )
    }
  }

  fun replaceLocalhost(jsonString: String): JSONObject {
    val localhostEndpoints = gson.fromJson(jsonString, EndpointConfig::class.java)
    val updatedAuthEndpoint =
      localhostEndpoints.authorizationEndpoint.replace("localhost", "10.0.2.2")
    val updatedTokenEndpoint = localhostEndpoints.tokenEndpoint.replace("localhost", "10.0.2.2")
    val updatedEndpointConfig = EndpointConfig(updatedAuthEndpoint, updatedTokenEndpoint)
    val oldDiscoveryDoc = JSONObject(jsonString).getString("discoveryDoc")
    val updatedDiscoveryDoc = oldDiscoveryDoc.replace("localhost", "10.0.2.2")
    val newJson = JSONObject(gson.toJson(updatedEndpointConfig))
    newJson.put("discoveryDoc", JSONObject(updatedDiscoveryDoc))
    return newJson
  }
}

data class EndpointConfig(
  val authorizationEndpoint: String,
  val tokenEndpoint: String,
)
