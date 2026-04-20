package com.example.taskforeffectivemobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskforeffectivemobile.databinding.Fragment3Binding

class Fragment3: BaseFragment() {

    private lateinit var binding: Fragment3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Fragment3"
    }

    override fun setupNavigation() {
        binding.button.setOnClickListener {
            router?.navigateTo(Fragment1())
        }
        binding.button2.setOnClickListener {
            router?.navigateBack()
        }
    }
}
