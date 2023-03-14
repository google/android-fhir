/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.demo.util

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

fun setProgressDialog(context: Context, message: String): AlertDialog {
  val llPadding = 30
  val ll = LinearLayout(context)
  ll.orientation = LinearLayout.HORIZONTAL
  ll.setPadding(llPadding, llPadding, llPadding, llPadding)
  ll.gravity = Gravity.CENTER
  var llParam =
    LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    )
  llParam.gravity = Gravity.CENTER
  ll.layoutParams = llParam

  val progressBar = ProgressBar(context)
  progressBar.isIndeterminate = true
  progressBar.setPadding(0, 0, llPadding, 0)
  progressBar.layoutParams = llParam

  llParam =
    LinearLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
  llParam.gravity = Gravity.CENTER
  val tvText = TextView(context)
  tvText.text = message
  tvText.setTextColor(Color.parseColor("#000000"))
  tvText.textSize = 20.toFloat()
  tvText.layoutParams = llParam

  ll.addView(progressBar)
  ll.addView(tvText)

  val builder = AlertDialog.Builder(context)
  builder.setCancelable(true)
  builder.setView(ll)

  val dialog = builder.create()
  val window = dialog.window
  if (window != null) {
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(dialog.window?.attributes)
    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.attributes = layoutParams
  }
  return dialog
}
