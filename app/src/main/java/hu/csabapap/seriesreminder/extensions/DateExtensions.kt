package hu.csabapap.seriesreminder.extensions

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

fun OffsetDateTime.diffInDays(): Int {
    val zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
    val other = zonedDateTime.toOffsetDateTime()
    return this.dayOfMonth - other.dayOfMonth
}