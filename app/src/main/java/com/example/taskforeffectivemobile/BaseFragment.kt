package com.example.taskforeffectivemobile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment: Fragment() {

    private var router: Router? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        router = context as Router
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationButton()
    }

    private fun setupNavigationButton(){
        getNextButton().setOnClickListener {
            router?.navigateToNextScreen()
        }

        getPreviousButton().setOnClickListener {
            router?.navigateToPreviousScreen()
        }
    }

    abstract fun getNextButton(): Button
    abstract fun getPreviousButton(): Button
}

