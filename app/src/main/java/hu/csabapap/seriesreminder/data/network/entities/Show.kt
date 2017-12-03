package hu.csabapap.seriesreminder.data.network.entities

class Show(title: String, ids: Ids, overview: String, runtime: Int, rating: Float, votes: Int,
           _image: String, _thumb: String, _cover : String?) :
        BaseShow(title, ids, overview, _image, _thumb, rating, votes){

    var cover = _cover
    get(){
        return "https://thetvdb.com/banners/$field"
    }
}