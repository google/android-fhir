package com.google.fhirengine.example

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.fhirengine.example.data.SamplePatients

class PatientItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val idView: TextView = itemView.findViewById(
        R.id.id_patient_number
    )
    private val nameView: TextView = itemView.findViewById(
        R.id.name
    )
    private val genderView: TextView = itemView.findViewById(
        R.id.gender
    )
    private val dobView: TextView = itemView.findViewById(
        R.id.dob
    )

    fun bindTo(patientItem: SamplePatients.PatientItem, onClickListener: View.OnClickListener) {
        this.idView.text = patientItem.id
        this.nameView.text = patientItem.name
        this.genderView.text = patientItem.gender
        this.dobView.text = patientItem.dob

        with(this.itemView) {
            tag = patientItem
            setOnClickListener(onClickListener)
        }
    }
}