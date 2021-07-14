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

package com.google.android.fhir.datacapture.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.datacapture.gallery.databinding.ActivityMainBinding
import com.google.android.fhir.datacapture.gallery.utils.LocaleUtils

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var languageList: List<LocaleUtils.Language>
  private lateinit var sharedPreferences: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    languageList = LocaleUtils.getLanguageList(this)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.top_bar_menu, menu)

    menu.findItem(R.id.action_submit).isVisible = false
    menu.findItem(R.id.action_language).isVisible = true
    return true
  }

  private fun refreshToSelectedLanguage(language: LocaleUtils.Language, context: Activity) {

    LocaleUtils.saveSelectedLanguage(language, sharedPreferences)

    this reload context
  }

  override fun attachBaseContext(baseContext: Context) {
    sharedPreferences =
      baseContext.getSharedPreferences(
        BuildConfig.APPLICATION_ID.plus(this.javaClass.canonicalName),
        Context.MODE_PRIVATE
      )

    var newContextResourceConfiguration: Configuration?

    LocaleUtils.getSelectedLanguage(sharedPreferences).also {
      newContextResourceConfiguration =
        LocaleUtils.updateContextResourceConfiguration(baseContext, it)
    }

    super.attachBaseContext(baseContext)
    applyOverrideConfiguration(newContextResourceConfiguration)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_language -> {
        LocaleUtils.renderSelectLanguageDialog(this, languageList) { _, i ->
          refreshToSelectedLanguage(languageList[i], this)
        }
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private infix fun Activity.reload(activity: Activity) {
    val intent = Intent(activity, activity.javaClass)
    activity.startActivity(intent)
  }
}
