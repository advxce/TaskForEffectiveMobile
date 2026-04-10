package com.example.taskforeffectivemobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.taskforeffectivemobile.databinding.Fragment1Binding
import com.example.taskforeffectivemobile.databinding.Fragment2Binding

class Fragment2: BaseFragment() {

    private lateinit var binding: Fragment2Binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment2Binding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Fragment2"
    }
    override fun getNextButton(): Button {
        return binding.button
    }

    override fun getPreviousButton(): Button {
        return binding.button2
    }
}