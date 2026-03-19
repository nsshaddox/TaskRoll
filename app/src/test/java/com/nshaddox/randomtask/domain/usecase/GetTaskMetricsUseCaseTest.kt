package com.nshaddox.randomtask.domain.usecase

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class GetTaskMetricsUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var useCase: GetTaskMetricsUseCase

    private val today = LocalDate.now()
    private val todayStartMs = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val todayEpochDays = today.toEpochDay()

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        useCase = GetTaskMetricsUseCase(repository)
    }

    @Test
    fun `empty repository returns zero metrics`() =
        runTest {
            useCase().test {
                val metrics = awaitItem()
                assertEquals(0, metrics.completedToday)
                assertEquals(0, metrics.completedThisWeek)
                assertEquals(0, metrics.streak)
                assertEquals(0, metrics.totalRemaining)
                assertEquals(0f, metrics.completionRate)
                assertEquals(0, metrics.overdueCount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `completedToday counts tasks completed today`() =
        runTest {
            // Task completed today
            repository.addTask(
                Task(
                    title = "Done today",
                    isCompleted = true,
                    createdAt = todayStartMs,
                    updatedAt = todayStartMs + 1000,
                ),
            )
            // Task completed yesterday (should not count)
            repository.addTask(
                Task(
                    title = "Done yesterday",
                    isCompleted = true,
                    createdAt = todayStartMs - 100_000_000,
                    updatedAt = todayStartMs - 100_000,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(1, metrics.completedToday)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `completedThisWeek counts tasks completed since Monday`() =
        runTest {
            val mondayStart =
                today.with(java.time.DayOfWeek.MONDAY)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

            // Task completed on Monday
            repository.addTask(
                Task(
                    title = "Done Monday",
                    isCompleted = true,
                    createdAt = mondayStart,
                    updatedAt = mondayStart + 1000,
                ),
            )
            // Task completed today
            repository.addTask(
                Task(
                    title = "Done today",
                    isCompleted = true,
                    createdAt = todayStartMs,
                    updatedAt = todayStartMs + 1000,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(2, metrics.completedThisWeek)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `streak returns 0 when no tasks completed today`() =
        runTest {
            // Task completed yesterday only
            repository.addTask(
                Task(
                    title = "Done yesterday",
                    isCompleted = true,
                    createdAt = todayStartMs - 200_000_000,
                    updatedAt = todayStartMs - 100_000,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(0, metrics.streak)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `streak returns 1 when only today has completions`() =
        runTest {
            repository.addTask(
                Task(
                    title = "Done today",
                    isCompleted = true,
                    createdAt = todayStartMs,
                    updatedAt = todayStartMs + 1000,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(1, metrics.streak)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `streak counts consecutive days ending today`() =
        runTest {
            val oneDayMs = 24 * 60 * 60 * 1000L

            // Today
            repository.addTask(
                Task(
                    title = "Done today",
                    isCompleted = true,
                    createdAt = todayStartMs,
                    updatedAt = todayStartMs + 1000,
                ),
            )
            // Yesterday
            repository.addTask(
                Task(
                    title = "Done yesterday",
                    isCompleted = true,
                    createdAt = todayStartMs - oneDayMs,
                    updatedAt = todayStartMs - oneDayMs + 1000,
                ),
            )
            // Day before yesterday
            repository.addTask(
                Task(
                    title = "Done 2 days ago",
                    isCompleted = true,
                    createdAt = todayStartMs - 2 * oneDayMs,
                    updatedAt = todayStartMs - 2 * oneDayMs + 1000,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(3, metrics.streak)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `streak breaks on gap day`() =
        runTest {
            val oneDayMs = 24 * 60 * 60 * 1000L

            // Today
            repository.addTask(
                Task(
                    title = "Done today",
                    isCompleted = true,
                    createdAt = todayStartMs,
                    updatedAt = todayStartMs + 1000,
                ),
            )
            // 2 days ago (gap on yesterday)
            repository.addTask(
                Task(
                    title = "Done 2 days ago",
                    isCompleted = true,
                    createdAt = todayStartMs - 2 * oneDayMs,
                    updatedAt = todayStartMs - 2 * oneDayMs + 1000,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(1, metrics.streak)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `totalRemaining counts incomplete tasks`() =
        runTest {
            repository.addTask(
                Task(title = "Incomplete 1", createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(title = "Incomplete 2", createdAt = 2000L, updatedAt = 2000L),
            )
            repository.addTask(
                Task(
                    title = "Completed",
                    isCompleted = true,
                    createdAt = 3000L,
                    updatedAt = 3000L,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(2, metrics.totalRemaining)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `completionRate is ratio of completed to total`() =
        runTest {
            // 1 completed out of 4 total = 0.25
            repository.addTask(
                Task(
                    title = "Completed",
                    isCompleted = true,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                ),
            )
            repository.addTask(
                Task(title = "Incomplete 1", createdAt = 2000L, updatedAt = 2000L),
            )
            repository.addTask(
                Task(title = "Incomplete 2", createdAt = 3000L, updatedAt = 3000L),
            )
            repository.addTask(
                Task(title = "Incomplete 3", createdAt = 4000L, updatedAt = 4000L),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(0.25f, metrics.completionRate, 0.001f)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `completionRate is 0 when no tasks exist`() =
        runTest {
            useCase().test {
                val metrics = awaitItem()
                assertEquals(0f, metrics.completionRate)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `overdueCount counts incomplete tasks past due date`() =
        runTest {
            // Overdue task (due yesterday, incomplete)
            repository.addTask(
                Task(
                    title = "Overdue",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    dueDate = todayEpochDays - 1,
                ),
            )
            // Not overdue (due tomorrow)
            repository.addTask(
                Task(
                    title = "Not overdue",
                    createdAt = 2000L,
                    updatedAt = 2000L,
                    dueDate = todayEpochDays + 1,
                ),
            )
            // Overdue but completed (should not count)
            repository.addTask(
                Task(
                    title = "Overdue but done",
                    isCompleted = true,
                    createdAt = 3000L,
                    updatedAt = 3000L,
                    dueDate = todayEpochDays - 2,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(1, metrics.overdueCount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `all metrics combined`() =
        runTest {
            val oneDayMs = 24 * 60 * 60 * 1000L

            // Completed today
            repository.addTask(
                Task(
                    title = "Done today",
                    isCompleted = true,
                    createdAt = todayStartMs,
                    updatedAt = todayStartMs + 1000,
                ),
            )
            // Completed yesterday
            repository.addTask(
                Task(
                    title = "Done yesterday",
                    isCompleted = true,
                    createdAt = todayStartMs - oneDayMs,
                    updatedAt = todayStartMs - oneDayMs + 1000,
                ),
            )
            // Incomplete, overdue
            repository.addTask(
                Task(
                    title = "Overdue task",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    dueDate = todayEpochDays - 3,
                ),
            )
            // Incomplete, not overdue
            repository.addTask(
                Task(
                    title = "Pending task",
                    createdAt = 2000L,
                    updatedAt = 2000L,
                ),
            )

            useCase().test {
                val metrics = awaitItem()
                assertEquals(1, metrics.completedToday)
                assertEquals(2, metrics.completedThisWeek)
                assertEquals(2, metrics.streak)
                assertEquals(2, metrics.totalRemaining)
                assertEquals(0.5f, metrics.completionRate, 0.001f)
                assertEquals(1, metrics.overdueCount)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
