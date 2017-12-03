package hu.csabapap.seriesreminder.data.network.entities

open class BaseShow (var title: String, var ids: Ids, var overview: String,
                     _image: String = "", _thumb: String = "", val rating: Float, val votes: Int){

    var image = _image
    get() {
        return "https://thetvdb.com/banners/${field}"
    }

    var thumb = _thumb
    get() {
        return "https://thetvdb.com/banners/${field}"
    }
}

