package hu.csabapap.seriesreminder.data.network.entities


data class AirsJson(val day: String? = "", val time: String? = "", val timezone: String? = "")

data class Airs(val day: String = "", val time: String = "", val timezone: String = "")