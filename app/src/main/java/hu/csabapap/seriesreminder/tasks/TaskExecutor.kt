package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskExecutor @Inject constructor(private val dispatchers: AppCoroutineDispatchers) {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    val queue = ArrayDeque<Task>()

    var taskIsRunning = false

    fun executeTasks(callback: () -> Unit) {
        if (taskIsRunning) return
        taskIsRunning = true
        scope.launch(dispatchers.io) {
            while (queue.isEmpty().not()) {
                val task = queue.pollFirst()
                task.execute()
            }
            taskIsRunning = false
            withContext(dispatchers.main) {
                callback()
            }
        }
    }
}