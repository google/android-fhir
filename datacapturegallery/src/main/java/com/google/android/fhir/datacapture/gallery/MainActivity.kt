package com.google.android.fhir.datacapture.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.datacapture.gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}