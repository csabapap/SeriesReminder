package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*

@Entity(tableName = "trending_shows",
        foreignKeys = [(
                ForeignKey(
                        entity = SRShow::class,
                        parentColumns = [("trakt_id")],
                        childColumns = [("show_id")],
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE))],
        indices = [(Index(value = ["show_id"], unique = true))])
data class SRTrendingItem(
        @PrimaryKey(autoGenerate = true) override val id: Long? = null,
        @ColumnInfo(name = "show_id") override val showId: Int,
        @ColumnInfo(name = "watchers") val watchers: Int = 0,
        @ColumnInfo(name = "page") val page: Int = 0
) : Item