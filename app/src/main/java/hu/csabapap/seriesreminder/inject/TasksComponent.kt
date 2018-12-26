package hu.csabapap.seriesreminder.inject

import hu.csabapap.seriesreminder.tasks.DownloadShowTask
import hu.csabapap.seriesreminder.tasks.FetchNextEpisodeTask

interface TasksComponent {
    fun inject(task: DownloadShowTask)
    fun inject(task: FetchNextEpisodeTask)
}