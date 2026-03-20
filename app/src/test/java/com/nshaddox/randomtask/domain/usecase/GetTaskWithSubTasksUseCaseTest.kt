package com.nshaddox.randomtask.domain.usecase

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTaskWithSubTasksUseCaseTest {
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var subTaskRepository: FakeSubTaskRepository
    private lateinit var getTaskWithSubTasksUseCase: GetTaskWithSubTasksUseCase

    @Before
    fun setup() {
        taskRepository = FakeTaskRepository()
        subTaskRepository = FakeSubTaskRepository()
        getTaskWithSubTasksUseCase = GetTaskWithSubTasksUseCase(taskRepository, subTaskRepository)
    }

    private fun createTask(title: String = "Test Task") =
        Task(
            title = title,
            createdAt = 1000L,
            updatedAt = 2000L,
        )

    private fun createSubTask(
        parentTaskId: Long,
        title: String = "SubTask",
        isCompleted: Boolean = false,
    ) = SubTask(
        parentTaskId = parentTaskId,
        title = title,
        isCompleted = isCompleted,
        createdAt = 1000L,
        updatedAt = 2000L,
    )

    @Test
    fun `invoke returns task with its subtasks`() =
        runTest {
            val taskId = taskRepository.addTask(createTask()).getOrThrow()
            subTaskRepository.addSubTask(createSubTask(parentTaskId = taskId, title = "Sub 1"))
            subTaskRepository.addSubTask(createSubTask(parentTaskId = taskId, title = "Sub 2"))

            getTaskWithSubTasksUseCase(taskId).test {
                val result = awaitItem()
                assertNotNull(result)
                assertEquals("Test Task", result!!.task.title)
                assertEquals(2, result.subTasks.size)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `invoke returns null when task not found`() =
        runTest {
            getTaskWithSubTasksUseCase(999L).test {
                val result = awaitItem()
                assertNull(result)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `invoke returns task with empty subtask list when no subtasks exist`() =
        runTest {
            val taskId = taskRepository.addTask(createTask()).getOrThrow()

            getTaskWithSubTasksUseCase(taskId).test {
                val result = awaitItem()
                assertNotNull(result)
                assertTrue(result!!.subTasks.isEmpty())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `invoke emits updated value when subtask is added`() =
        runTest {
            val taskId = taskRepository.addTask(createTask()).getOrThrow()

            getTaskWithSubTasksUseCase(taskId).test {
                val initial = awaitItem()
                assertNotNull(initial)
                assertEquals(0, initial!!.subTasks.size)

                subTaskRepository.addSubTask(createSubTask(parentTaskId = taskId))

                val updated = awaitItem()
                assertNotNull(updated)
                assertEquals(1, updated!!.subTasks.size)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `invoke only returns subtasks for the specified task`() =
        runTest {
            val taskId1 = taskRepository.addTask(createTask("Task 1")).getOrThrow()
            val taskId2 = taskRepository.addTask(createTask("Task 2")).getOrThrow()
            subTaskRepository.addSubTask(createSubTask(parentTaskId = taskId1, title = "Sub for 1"))
            subTaskRepository.addSubTask(createSubTask(parentTaskId = taskId2, title = "Sub for 2"))

            getTaskWithSubTasksUseCase(taskId1).test {
                val result = awaitItem()
                assertNotNull(result)
                assertEquals(1, result!!.subTasks.size)
                assertEquals("Sub for 1", result.subTasks.first().title)
                cancelAndConsumeRemainingEvents()
            }
        }
}
