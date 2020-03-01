package hu.csabapap.seriesreminder.utils

import hu.csabapap.seriesreminder.data.db.entities.AiringTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetDateTime.of
import org.threeten.bp.ZoneOffset

class DateUtilsTest {

    private lateinit var  currentDateTime: OffsetDateTime

    @Before
    fun setUp() {
        currentDateTime = of(2018, 9, 26, 14,11,0,0, ZoneOffset.UTC)
    }

    @Test
    fun `create the zoned datetime for the given day and time`() {
        val airingTime = AiringTime("Friday", "21:00", "America/New_York")
        val localDateTime = LocalDateTime.of(2020, 3, 1, 12, 0)
        val calculatedDateTime = getAirDateTimeInCurrentTimeZone(localDateTime, airingTime)
        assertEquals(DayOfWeek.SATURDAY, calculatedDateTime.dayOfWeek)
        assertEquals(3, calculatedDateTime.hour)
    }

    @Test
    fun `create the zoned datetime for for same day airtime`() {
        val airingTime = AiringTime("Friday", "21:00", "America/New_York")
        var localDateTime = LocalDateTime.of(2020, 3, 1, 2, 0)
        while (localDateTime.dayOfWeek != DayOfWeek.SATURDAY) {
            localDateTime = localDateTime.plusDays(1)
        }
        val calculatedDateTime = getAirDateTimeInCurrentTimeZone(localDateTime, airingTime)
        assertEquals(localDateTime.dayOfMonth, calculatedDateTime.dayOfMonth)
    }
}