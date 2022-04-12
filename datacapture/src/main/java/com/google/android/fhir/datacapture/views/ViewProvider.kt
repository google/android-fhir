package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-04-2022.
 */
object ViewProvider {


    fun getView(parent: ViewGroup, @LayoutRes resId: Int) : View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }

}