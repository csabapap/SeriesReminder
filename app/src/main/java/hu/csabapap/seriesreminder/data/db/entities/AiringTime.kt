package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.TypeConverter

data class AiringTime(val day: String = "", val time: String = "", val timezone: String = "") {

    @TypeConverter
    fun airingTimeToString(airingTime: AiringTime): String {
        if (day.isEmpty() && time.isEmpty() && timezone.isEmpty()) {
            return ""
        }
        return "${airingTime.day}|${airingTime.time}|${airingTime.timezone}"
    }

    @TypeConverter
    fun stringToAiringTime(fromDb: String): AiringTime {
        if (fromDb.isEmpty()) {
            return AiringTime()
        }
        val parts = fromDb.split("|")
        return AiringTime(parts[0], parts[1], parts[2])
    }
}