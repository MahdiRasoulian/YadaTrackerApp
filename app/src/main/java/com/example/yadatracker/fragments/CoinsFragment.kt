package com.example.yadatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yadatracker.R // Adjust this to your actual R file import

class CoinsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // For now, this fragment can just display a simple text or your existing main content
        // You can later move elements from your ScrollView into a dedicated fragment layout
        return inflater.inflate(R.layout.fragment_coins, container, false)
    }
}