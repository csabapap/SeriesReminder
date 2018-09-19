package hu.csabapap.seriesreminder.utils

import io.reactivex.schedulers.Schedulers

class TestAppRxSchedulers: RxSchedulers {
    override fun io() = Schedulers.trampoline()

    override fun ui() = Schedulers.trampoline()

    override fun compoutation() = Schedulers.trampoline()
}