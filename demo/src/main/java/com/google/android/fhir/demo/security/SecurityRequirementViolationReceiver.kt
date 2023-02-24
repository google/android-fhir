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

package com.google.android.fhir.demo.security

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.fhir.demo.R
import com.google.android.fhir.security.LockScreenRequirementViolation
import com.google.android.fhir.security.SecurityRequirementViolation.EXTRA_LOCK_SCREEN_REQUIREMENT_VIOLATION
import timber.log.Timber

/** A sample receiver demonstrates the handling of password requirement violation. */
class SecurityRequirementViolationReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val lockScreenRequirementViolation =
      intent.getParcelableExtra<LockScreenRequirementViolation>(
        EXTRA_LOCK_SCREEN_REQUIREMENT_VIOLATION
      )
    Timber.w("Lock screen violation extra: $lockScreenRequirementViolation")
    if (lockScreenRequirementViolation == null) return
    showPasswordViolationNotification(context, lockScreenRequirementViolation.requiredComplexity)
  }

  private fun showPasswordViolationNotification(context: Context, requiredComplexity: Int) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    // We can't ask notification runtime permission from the background. Let's ask users for the
    // notification running permission when the demo app activity is started.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        !notificationManager.areNotificationsEnabled()
    ) {
      Timber.w("Can't post notification")
      return
    }

    createNotificationChannel(notificationManager)

    val notificationBuilder =
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        Notification.Builder(context)
      } else {
        Notification.Builder(context, SECURITY_REQUIREMENT_VIOLATION_NOTIFICATION_CHANNEL_ID)
      }

    notificationManager.notify(
      NOTIFICATION_ID,
      notificationBuilder
        .setSmallIcon(R.drawable.ic_small_fire_engine)
        .setContentTitle("Your lock screen doesn't meet the requirement")
        .setContentText("Click to set up.")
        .setContentIntent(
          PendingIntent.getActivity(
            context,
            /* requestCode= */ 0,
            Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
              .putExtra(DevicePolicyManager.EXTRA_PASSWORD_COMPLEXITY, requiredComplexity),
            /* flags= */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
          )
        )
        .setAutoCancel(true)
        .build()
    )
  }

  private fun createNotificationChannel(notificationManager: NotificationManager) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    notificationManager.createNotificationChannel(securityNotificationChannel)
  }

  private companion object {
    const val NOTIFICATION_ID = 324265
    const val SECURITY_REQUIREMENT_VIOLATION_NOTIFICATION_CHANNEL_ID = "fhir_security"
    @RequiresApi(Build.VERSION_CODES.O)
    val securityNotificationChannel =
      NotificationChannel(
        SECURITY_REQUIREMENT_VIOLATION_NOTIFICATION_CHANNEL_ID,
        /* name= */ "Security requirement violation notification",
        NotificationManager.IMPORTANCE_DEFAULT
      )
  }
}
