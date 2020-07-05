package hu.csabapap.seriesreminder.inject.modules

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
class AppModule{

    @Singleton
    @Provides
    fun provideContext(application: SRApplication): Context {
        return application.applicationContext
    }

    @Provides
    fun provideAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Singleton
    @Provides
    fun provideAppDispatchers() : AppCoroutineDispatchers {
        return AppCoroutineDispatchers(
                io = Dispatchers.IO,
                computation = Dispatchers.IO,
                main = Dispatchers.Main
        )
    }

    @Provides
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun providePreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    fun providesCollectionRepository(collectionsDao: CollectionsDao) : CollectionRepository {
        return CollectionRepository(collectionsDao)
    }
}