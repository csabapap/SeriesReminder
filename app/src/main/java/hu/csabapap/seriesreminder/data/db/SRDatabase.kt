package hu.csabapap.seriesreminder.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.entities.SRShow

@Database(entities = arrayOf(SRShow::class), version = 1)
abstract class SRDatabase : RoomDatabase(){
    abstract fun showDao() : SRShowDao
}