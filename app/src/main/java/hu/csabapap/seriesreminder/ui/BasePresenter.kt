package hu.csabapap.seriesreminder.ui

interface BasePresenter<V> {

    fun attach(view: V)
    fun detach()
}