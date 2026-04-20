package com.example.taskforeffectivemobile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    protected var router: Router? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Router) {
            router = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    abstract fun setupNavigation()
}
