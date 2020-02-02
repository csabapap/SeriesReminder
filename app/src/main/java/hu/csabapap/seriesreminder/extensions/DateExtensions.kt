package hu.csabapap.seriesreminder.extensions

import org.threeten.bp.Duration
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

fun OffsetDateTime.diffInDays(): Int {
    val currentDateTime = OffsetDateTime.now(ZoneOffset.UTC)
    val duration = Duration.between(currentDateTime, this)
    return duration.toDays().toInt()
}

fun OffsetDateTime.diffInHours(): Int {
    val currentDateTime = OffsetDateTime.now(ZoneOffset.UTC)
    val duration = Duration.between(currentDateTime, this)
    return duration.toHours().toInt()
}

//operator fun OffsetDateTime.compareTo(other: OffsetDateTime): Int {
//    return -1
//}