package hu.csabapap.seriesreminder.data.db

fun makePlaceholders(len: Int): String {
    if (len < 1) {
        throw RuntimeException("No placeholders")
    } else {
        val sb = StringBuilder(len * 2 - 1)
        sb.append("?")
        for (i in 1 until len) {
            sb.append(",?")
        }
        return sb.toString()
    }
}

