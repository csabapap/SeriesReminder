package hu.csabapap.seriesreminder.utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SRRxSchedulers: RxSchedulers {
    override fun io() = Schedulers.io()

    override fun ui() = AndroidSchedulers.mainThread()

    override fun compoutation() = Schedulers.computation()
}