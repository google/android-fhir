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

package com.google.android.fhir.datacapture.gallery.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.android.fhir.datacapture.gallery.R
import java.util.Locale

object LanguageSwitcherUtils {

  const val SHARED_PREF_KEY_LANG = "shared_pref_key_lang"

  fun setAppLocale(context: Context, language: String?): Configuration? {
    val res: Resources = context.resources
    val configuration: Configuration = res.configuration
    try {
      val locale = Locale.forLanguageTag(language)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        context.createConfigurationContext(configuration)
      } else {
        configuration.locale = locale
        res.updateConfiguration(configuration, res.displayMetrics)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return configuration
  }

  fun getLanguageList(activity: Activity): List<Language> {
    return activity.resources.getStringArray(R.array.languages).toList().map {
      Language(it, Locale.forLanguageTag(it).displayName)
    }
  }

  fun refreshActivity(activity: Activity) {
    val intent = Intent(activity, activity.javaClass)
    activity.startActivity(intent)
  }

  fun renderSelectLanguageDialog(
    context: Activity,
    languageList: List<Language>,
    listener: DialogInterface.OnClickListener
  ) {

    val adapter: ArrayAdapter<Language> =
      ArrayAdapter(context, android.R.layout.simple_list_item_1, languageList)

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setTitle(context.getString(R.string.select_language))
    builder.setIcon(R.drawable.outline_language_24)
    builder.setAdapter(adapter, listener).create().show()
  }
  data class Language(val tag: String, val displayName: String) {
    override fun toString() = displayName
  }
}
