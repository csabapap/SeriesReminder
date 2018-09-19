package hu.csabapap.seriesreminder.utils

import io.reactivex.Scheduler

interface RxSchedulers {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun compoutation(): Scheduler
}