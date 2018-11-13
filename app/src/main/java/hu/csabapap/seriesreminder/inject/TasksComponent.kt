package hu.csabapap.seriesreminder.inject

import hu.csabapap.seriesreminder.tasks.DownloadShowTask

interface TasksComponent {
    fun inject(task: DownloadShowTask)
}