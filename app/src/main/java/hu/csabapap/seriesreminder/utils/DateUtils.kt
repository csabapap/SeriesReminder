package hu.csabapap.seriesreminder.utils

import hu.csabapap.seriesreminder.data.db.entities.AiringTime
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

val dayAndTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE H:mm")

fun getAirDateTimeInCurrentTimeZone(localDateTime: LocalDateTime,
                                    airingTime: AiringTime,
                                    localZoneId: ZoneId = ZoneId.systemDefault())
        : ZonedDateTime {
    val zoneId = ZoneId.of(airingTime.timezone)
    val localZonedDateTime = ZonedDateTime.of(localDateTime, localZoneId)
    var airingDateTime = localZonedDateTime.withZoneSameInstant(zoneId)
    while (airingDateTime.dayOfWeek != DayOfWeek.valueOf(airingTime.day.toUpperCase())) {
        airingDateTime = airingDateTime.plusDays(1)
    }
    val (hours, minutes) = toTime(airingTime.time)
    val airDateTime = ZonedDateTime.of(airingDateTime.year,
            airingDateTime.monthValue,
            airingDateTime.dayOfMonth,
            hours,
            minutes,
            0,
            0,
            airingDateTime.zone)

    return airDateTime.withZoneSameInstant(ZoneId.systemDefault())
}

fun getDayAndTimeString(dateTime: ZonedDateTime): String {
    return dayAndTimeFormatter.format(dateTime)
}

fun toTime(time:String): SrTime {
    val timeParts = time.split(":")
    val hours = timeParts[0].toInt()
    val minutes = timeParts[1].toInt()
    return SrTime(hours, minutes)
}

fun readableDate(milliseconds: Int): String {
    val minutes = milliseconds / 1000 / 60
    val hours = if (minutes >= 60) {
        minutes / 60
    } else {
        0
    }

    val days = if (hours >= 24) hours / 24 else 0

    if (days != 0) {
        return if (days == 1) {
            "1 day"
        } else {
            "$days days"
        }
    }

    if (hours != 0) {
        return if (hours == 1) {
            "1 hour"
        } else {
            "$hours hours"
        }
    }

    return "$minutes minutes"
}

data class SrTime(val hours: Int, val minutes: Int)