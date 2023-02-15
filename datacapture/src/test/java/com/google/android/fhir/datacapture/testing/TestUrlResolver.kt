package com.google.android.fhir.datacapture.testing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.fhir.datacapture.UrlResolver

class TestUrlResolver(private val correctUrl: String, private val decodedBase64Image: ByteArray) : UrlResolver {

    override suspend fun resolveBitmapUrl(url: String): Bitmap? {
        if (url == correctUrl) return BitmapFactory.decodeByteArray(decodedBase64Image, 0, decodedBase64Image.size)
        return null
    }
}