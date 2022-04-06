package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

object ViewProvider {
fun getView(parent: ViewGroup,@LayoutRes resId: Int): View {
 return  LayoutInflater.from(parent.context).inflate(resId, parent, false)
}
}
