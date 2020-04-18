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
import hu.csabapap.seriesreminder.data.db.daos.*
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import hu.csabapap.seriesreminder.utils.RxSchedulers
import hu.csabapap.seriesreminder.utils.SRRxSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
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
    fun provideRxSchedulers() : AppRxSchedulers {
        return AppRxSchedulers()
    }

    @Singleton
    @Provides
    fun provideSrRxSchedulers() : RxSchedulers {
        return SRRxSchedulers()
    }

    @Singleton
    @Provides
    fun provideAppDispatchers(rxSchedulers: RxSchedulers) : AppCoroutineDispatchers {
        return AppCoroutineDispatchers(
                io = rxSchedulers.io().asCoroutineDispatcher(),
                computation = rxSchedulers.compoutation().asCoroutineDispatcher(),
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