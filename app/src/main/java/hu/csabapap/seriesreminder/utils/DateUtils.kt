package hu.csabapap.seriesreminder.utils

import org.threeten.bp.*

fun getDateTimeForNextAir(currentDateTime: OffsetDateTime, day: String, time: String): OffsetDateTime {
    val nextDayOfWeek = DayOfWeek.valueOf(day.toUpperCase())
    var nextDateTime = OffsetDateTime.of(currentDateTime.toLocalDateTime(), ZoneOffset.UTC)
    println("next day of week: $nextDayOfWeek")
    while (nextDateTime.dayOfWeek != nextDayOfWeek) {
        nextDateTime = nextDateTime.plusDays(1)
    }
    val (hours, mins) = toTime(time)
    val newDateTime = ZonedDateTime.of(nextDateTime.toLocalDate(), LocalTime.of(hours, mins), ZoneOffset.UTC)

    return newDateTime.toOffsetDateTime()
}

fun toTime(time:String): SrTime {
    val timeParts = time.split(":")
    val hours = timeParts[0].toInt()
    val minutes = timeParts[1].toInt()
    return SrTime(hours, minutes)
}

data class SrTime(val hours: Int, val minutes: Int)