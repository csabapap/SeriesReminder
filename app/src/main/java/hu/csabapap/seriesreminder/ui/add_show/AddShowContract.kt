package hu.csabapap.seriesreminder.ui.add_show

import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.BasePresenter

interface AddShowContract {
    interface View{
        fun displayShow(show: SRShow)
    }

    interface Presenter : BasePresenter<AddShowContract.View>{
        fun loadShow(showId: Int)
        fun loadCover(showId: Int)
    }
}