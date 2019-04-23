package hu.csabapap.seriesreminder.data.repositories.notifications

import hu.csabapap.seriesreminder.data.db.daos.NotificationsDao
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import javax.inject.Inject

class NotificationsRepository @Inject constructor(private val notificationsDao: NotificationsDao) {

    fun createNotification(notification: SrNotification) {
        notificationsDao.insert(notification)
    }

    suspend fun getNotification(showId: Int) = notificationsDao.getNotification(showId)
}