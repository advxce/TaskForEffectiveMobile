package com.example.taskforeffectivemobile

interface Router {

    fun navigateToNextScreen()
    fun navigateToPreviousScreen()

    fun navigationManager(screen: Screen)

}

enum class Screen{
    SCREEN_1,
    SCREEN_2,
    SCREEN_3
}