package hu.csabapap.seriesreminder.utils

import kotlinx.coroutines.CoroutineDispatcher

data class AppCoroutineDispatchers(
        val io: CoroutineDispatcher,
        val computation: CoroutineDispatcher,
        val main: CoroutineDispatcher
)