package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*
import org.threeten.bp.OffsetDateTime
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
        @ColumnInfo(name="cover") var cover: String = "",
        @ColumnInfo(name="cover_thumb") var coverThumb: String = "",
        @ColumnInfo(name="rating") var rating: Float = 0f,
        @ColumnInfo(name="votes") var votes: Int = 0,
        @ColumnInfo(name="genres") var genres: String = "",
        @ColumnInfo(name="runtime") var runtime: Int = 0,
        @ColumnInfo(name="aired_episodes") var airedEpisodes: Int = 0,
        @ColumnInfo(name="status") var status: String = "",
        @ColumnInfo(name="network") var network: String = "",
        @ColumnInfo(name="trailer") var trailer: String = "",
        @ColumnInfo(name="homepage") var homepage: String = "",
        @ColumnInfo(name="updated_at") var updatedAt: OffsetDateTime? = null,
        @ColumnInfo(name="airs") var airingTime: AiringTime = AiringTime()) {

    @Ignore
    var inCollection: Boolean = false

    fun <T> updateProperty(entryVar : KMutableProperty0<T>, updateVal: T) {
        when {
            updateVal != null -> entryVar.set(updateVal)
        }
    }
}