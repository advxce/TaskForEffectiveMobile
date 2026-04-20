package com.example.taskforeffectivemobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskforeffectivemobile.databinding.Fragment1Binding

class Fragment1: BaseFragment() {

    private lateinit var binding: Fragment1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Fragment1"
    }

    override fun setupNavigation() {
        binding.button.setOnClickListener {
            router?.navigateTo(Fragment2())
        }
        binding.button2.setOnClickListener {
            router?.navigateBack()
        }
    }
}
