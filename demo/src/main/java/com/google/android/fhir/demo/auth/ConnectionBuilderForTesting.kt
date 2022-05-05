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

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import net.openid.appauth.Preconditions
import net.openid.appauth.connectivity.ConnectionBuilder
import timber.log.Timber

/**
 * TODO: DELETE THIS CLASS WHEN YOU HAVE AN HTTPS IDP An example implementation of
 * [ConnectionBuilder] that permits connecting to http links, and ignores certificates for https
 * connections. *THIS SHOULD NOT BE USED IN PRODUCTION CODE*. It is intended to facilitate easier
 * testing of AppAuth against development servers only.
 */
object ConnectionBuilderForTesting : ConnectionBuilder {
  private val CONNECTION_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(15).toInt()
  private val READ_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10).toInt()
  private const val HTTP = "http"
  private const val HTTPS = "https"
  @SuppressLint("TrustAllX509TrustManager")
  private val ANY_CERT_MANAGER =
    arrayOf<TrustManager>(
      object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate>? {
          return null
        }
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
      }
    )
  @SuppressLint("BadHostnameVerifier")
  private val ANY_HOSTNAME_VERIFIER = HostnameVerifier { _, _ -> true }
  private var TRUSTING_CONTEXT: SSLContext? = null
  init {
    val context: SSLContext? =
      try {
        SSLContext.getInstance("SSL")
      } catch (e: NoSuchAlgorithmException) {
        Log.e("ConnBuilder", "Unable to acquire SSL context")
        null
      }
    var initializedContext: SSLContext? = null
    if (context != null) {
      try {
        context.init(null, ANY_CERT_MANAGER, SecureRandom())
        initializedContext = context
      } catch (e: KeyManagementException) {
        Timber.e("Failed to initialize trusting SSL context")
      }
    }
    TRUSTING_CONTEXT = initializedContext
  }
  override fun openConnection(uri: Uri): HttpURLConnection {
    Preconditions.checkNotNull(uri, "url must not be null")
    Preconditions.checkArgument(
      HTTP == uri.scheme || HTTPS == uri.scheme,
      "scheme or uri must be http or https"
    )
    val conn = URL(uri.toString()).openConnection() as HttpURLConnection
    conn.connectTimeout = CONNECTION_TIMEOUT_MS
    conn.readTimeout = READ_TIMEOUT_MS
    conn.instanceFollowRedirects = false
    if (conn is HttpsURLConnection && TRUSTING_CONTEXT != null) {
      conn.sslSocketFactory = TRUSTING_CONTEXT!!.socketFactory
      conn.hostnameVerifier = ANY_HOSTNAME_VERIFIER
    }
    return conn
  }
}
