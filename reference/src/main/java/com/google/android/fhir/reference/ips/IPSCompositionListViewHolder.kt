package com.google.android.fhir.reference.ips

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.R
import org.hl7.fhir.r4.model.Composition

class IPSCompositionListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val title: TextView = itemView.findViewById(R.id.list_title)
    val description: TextView = itemView.findViewById(R.id.list_description)
}
