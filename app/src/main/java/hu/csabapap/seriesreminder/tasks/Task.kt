package hu.csabapap.seriesreminder.tasks

interface Task {
    suspend fun execute()
}