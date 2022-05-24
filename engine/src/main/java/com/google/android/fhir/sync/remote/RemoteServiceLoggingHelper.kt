package com.google.android.fhir.sync.remote

import android.os.Build
import android.os.Environment
import java.io.File

class RemoteServiceLoggingHelper {

  companion object{
    const val SYNC_FOLDER = "SyncLogs"

    fun commonDocumentDirPath(FolderName: String): File? {
      var dir: File? = null
      dir =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
              .toString() + "/" + FolderName
          )
        } else {
          File(
            Environment.getExternalStorageDirectory().absolutePath.toString() +
              "/Documents/" +
              FolderName
          )
        }

      // Make sure the path directory exists.
      if (!dir.exists()) {
        // Make it, if it doesn't exit
        val success = dir.mkdirs()
        if (!success) {
          dir = null
        }
      }
      return dir
    }
  }
}