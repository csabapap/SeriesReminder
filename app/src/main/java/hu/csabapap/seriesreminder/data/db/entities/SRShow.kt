package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.*
import kotlin.reflect.KMutableProperty0

@Entity(tableName = "shows",
        indices = [(Index(value = ["trakt_id"], unique = true)), (Index(value = ["tvdb_id"], unique = true))])
data class SRShow(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long? = null,
        @ColumnInfo(name="trakt_id") var traktId: Int = 0,
        @ColumnInfo(name="tvdb_id") var tvdbId: Int = 0,
        @ColumnInfo(name="title") var title: String = "",
        @ColumnInfo(name="overview") var overview: String = "",
        @ColumnInfo(name="poster") var poster: String = "",
        @ColumnInfo(name="poster_thumb") var posterThumb: String = "",
        @ColumnInfo(name="cover") var _cover: String = "",
        @ColumnInfo(name="cover_thumb") var _coverThumb: String = "",
        @ColumnInfo(name="rating") var rating: Float = 0f,
        @ColumnInfo(name="votes") var votes: Int = 0) {
    @Ignore var coverUrl = _cover
        get() {
            return "https://thetvdb.com/banners/$field"
        }

    @Ignore var coverThumbUrl = _coverThumb
        get() {
            return "https://thetvdb.com/banners/$field"
        }

    fun <T> updateProperty(entryVar : KMutableProperty0<T>, updateVal: T) {
        when {
            updateVal != null -> entryVar.set(updateVal)
        }
    }
}