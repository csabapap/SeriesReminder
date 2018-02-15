package hu.csabapap.seriesreminder.data.network.entities

class Show(title: String = "",
           ids: Ids,
           overview: String,
           val runtime: Int,
           rating: Float,
           votes: Int,
           val genres: Array<String>,
           var aired_episodes: Int,
           var status: String,
           var network: String,
           var trailer: String,
           var homepage: String,
           var updated_at: String,
           var airs: Airs?,
           _image: String,
           _thumb: String,
           _cover : String?) :
        BaseShow(title, ids, overview, _image, _thumb, rating, votes)