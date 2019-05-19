package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "related_shows",
        foreignKeys = [ForeignKey(
                entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["relates_to"]
        ), ForeignKey(
                entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["related_id"]
        )])
data class RelatedShow(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        @ColumnInfo(name = "related_id") val relatedId: Int,
        @ColumnInfo(name = "relates_to") val relatesTo: Int)
