package hu.csabapap.seriesreminder.domain

import androidx.work.WorkManager
import com.nhaarman.mockitokotlin2.mock
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class CreateNotificationAlarmUseCaseTest {

    private val notificationsRepository: NotificationsRepository = mock()
    private val showsRepository: ShowsRepository = mock()
    private val episodesRepository: EpisodesRepository = mock()
    private val workManager: WorkManager = mock()

    private val createNotificationAlarmUseCase = CreateNotificationAlarmUseCase(
            notificationsRepository,
            showsRepository,
            episodesRepository,
            workManager
    )

    @Test
    fun `delay should be zero when the two dates are equal`() {
        val airDateTime = ZonedDateTime.of(2020, 7, 30, 20, 20, 20, 0, ZoneId.of("UTC"))
        val currentDateTime = ZonedDateTime.of(2020, 7, 30, 20, 20, 20, 0, ZoneId.of("UTC"))
        val result = createNotificationAlarmUseCase.getInitialDelay(airDateTime, currentDateTime, 0)
        assertEquals(0, result)
    }

    @Test
    fun `delay should be zero when air time less then current date time`() {
        val currentDateTime = ZonedDateTime.of(2020, 7, 30, 20, 20, 20, 0, ZoneId.of("UTC"))
        val airDateTime = currentDateTime.minusHours(1)
        val result = createNotificationAlarmUseCase.getInitialDelay(airDateTime, currentDateTime, 0)
        assertEquals(0, result)
    }

    @Test
    fun `delay should be less than dates difference when hour delay is negative`() {
        val currentDateTime = ZonedDateTime.of(2020, 7, 30, 20, 20, 20, 0, ZoneId.of("UTC"))
        val airDateTime = currentDateTime.plusHours(3)
        val result = createNotificationAlarmUseCase.getInitialDelay(airDateTime, currentDateTime, -1 * 60 * 60 * 1000)
        assertEquals(2 * 60 * 60 * 1000, result)
    }

    @Test
    fun `delay should be greater than dates difference when hour delay is positive`() {
        val currentDateTime = ZonedDateTime.of(2020, 7, 30, 20, 20, 20, 0, ZoneId.of("UTC"))
        val airDateTime = currentDateTime.plusHours(3)
        val result = createNotificationAlarmUseCase.getInitialDelay(airDateTime, currentDateTime, 1 * 60 * 60 * 1000)
        assertEquals(4 * 60 * 60 * 1000, result)
    }
}