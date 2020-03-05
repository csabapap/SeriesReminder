package hu.csabapap.seriesreminder.utils

import hu.csabapap.seriesreminder.data.db.entities.AiringTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DateUtilsTest {

    @Test
    fun `create the zoned datetime for the given day and time`() {
        val airingTime = AiringTime("Friday", "21:00", "America/New_York")
        val localDateTime = LocalDateTime.of(2020, 3, 1, 12, 0)
        val calculatedDateTime = getAirDateTimeInCurrentTimeZone(localDateTime, airingTime, ZoneId.of("UTC"))
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
        val calculatedDateTime = getAirDateTimeInCurrentTimeZone(localDateTime, airingTime, localZoneId = ZoneId.of("UTC"))
        assertEquals(localDateTime.dayOfMonth, calculatedDateTime.dayOfMonth)
    }
}