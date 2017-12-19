package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.*

@Entity(tableName = "trending_shows",
        foreignKeys = [(
                ForeignKey(
                        entity = SRShow::class,
                        parentColumns = [("trakt_id")],
                        childColumns = [("show_id")],
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE))],
        indices = [(Index(value = ["show_id"], unique = true))])
data class SRTrendingShow (
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        @ColumnInfo(name = "show_id") val showId: Int,
        @ColumnInfo(name = "watchers") val watchers: Int = 0)