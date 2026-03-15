package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchTasksUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var searchTasksUseCase: SearchTasksUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        searchTasksUseCase = SearchTasksUseCase(repository)
    }

    @Test
    fun `invoke with blank query returns empty list`() =
        runTest {
            repository.addTask(
                Task(title = "Buy groceries", createdAt = 1000L, updatedAt = 1000L),
            )

            val tasks = searchTasksUseCase("").first()

            assertTrue(tasks.isEmpty())
        }

    @Test
    fun `invoke with whitespace-only query returns empty list`() =
        runTest {
            repository.addTask(
                Task(title = "Buy groceries", createdAt = 1000L, updatedAt = 1000L),
            )

            val tasks = searchTasksUseCase("   ").first()

            assertTrue(tasks.isEmpty())
        }

    @Test
    fun `invoke with non-blank query returns matching tasks`() =
        runTest {
            repository.addTask(
                Task(title = "Buy groceries", createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(title = "Clean house", createdAt = 2000L, updatedAt = 2000L),
            )
            repository.addTask(
                Task(
                    title = "Walk dog",
                    description = "Buy treats first",
                    createdAt = 3000L,
                    updatedAt = 3000L,
                ),
            )

            val tasks = searchTasksUseCase("Buy").first()

            assertEquals(2, tasks.size)
            assertEquals("Buy groceries", tasks[0].title)
            assertEquals("Walk dog", tasks[1].title)
        }

    @Test
    fun `invoke trims query before searching`() =
        runTest {
            repository.addTask(
                Task(title = "Buy groceries", createdAt = 1000L, updatedAt = 1000L),
            )

            val tasks = searchTasksUseCase("  Buy  ").first()

            assertEquals(1, tasks.size)
            assertEquals("Buy groceries", tasks[0].title)
        }
}
