package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.*
import android.support.v7.graphics.Palette

@Entity(tableName = "trending_shows",
        foreignKeys = [(
                ForeignKey(
                        entity = SRShow::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("show_id"),
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE))])
data class SRTrendingShow (
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        @ColumnInfo(name = "show_id") val showId: Long,
        @ColumnInfo(name = "watchers") val watchers: Int = 0)