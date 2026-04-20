package com.example.taskforeffectivemobile.simularLocation

import kotlinx.coroutines.flow.Flow

interface Location {

    fun getLocation() : Flow<Result<Point>>

    suspend fun startBackgroundUpdates()

    suspend fun stopBackgroundUpdates()

}