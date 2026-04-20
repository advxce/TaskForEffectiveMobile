package com.example.taskforeffectivemobile

import androidx.fragment.app.Fragment

interface Router {
    fun navigateTo(fragment: Fragment)
    fun navigateBack()
}
