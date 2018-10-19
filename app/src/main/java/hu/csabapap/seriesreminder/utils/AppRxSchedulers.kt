package hu.csabapap.seriesreminder.utils

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers

data class AppRxSchedulers(
        val io : Scheduler = Schedulers.io(),
        val main: Scheduler = AndroidSchedulers.mainThread()
)