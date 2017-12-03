package hu.csabapap.seriesreminder.ui.add_show

import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

class AddShowPresenter @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val schedulers: AppRxSchedulers)
    : AddShowContract.Presenter {

    var view: AddShowContract.View? = null

    override fun attach(view: AddShowContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
    }

    override fun loadShow(showId: Int) {
        showsRepository.getShow(showId)
                .flatMap { it ->
                    showsRepository.images(it.tvdbId, "fanart")
                            .flatMap { (data) ->
                                var popularImage: Image? = null
                                for (image in data) {
                                    if (popularImage == null) {
                                        popularImage = image
                                        continue
                                    }

                                    if (image.ratings.average > popularImage.ratings.average) {
                                        popularImage = image
                                    }
                                }

                                it.apply {
                                    updateProperty(this::coverUrl, popularImage?.fileName!!)
                                    updateProperty(this::coverThumbUrl, popularImage?.thumbnail!!)
                                }

                                Flowable.just(it)
                            }
                }
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({ view?.displayShow(it) }, { Timber.e(it.message) })
    }

    override fun loadCover(showId: Int) {
        showsRepository.images(showId, "fanart")

    }
}