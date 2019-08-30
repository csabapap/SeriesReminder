package hu.csabapap.seriesreminder.utils

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetDateTime.of
import org.threeten.bp.ZoneOffset

class DateUtilsTest {

    lateinit var  currentDateTime: OffsetDateTime

    @Before
    fun setUp() {
        currentDateTime = of(2018, 9, 26, 14,11,0,0, ZoneOffset.UTC)
    }

    @Test
    fun `create the datetime for the given day and time`() {
        val calculatedDateTime = getDateTimeForNextAir(currentDateTime, "Friday", "21:00")
        val expectedDateTime = of(2018, 9, 28, 21,0,0,0, ZoneOffset.UTC)
        assertEquals(expectedDateTime, calculatedDateTime)
    }
}