package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.TaskMetrics
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetTaskMetricsUseCase
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) {
        operator fun invoke(): Flow<TaskMetrics> {
            val today = LocalDate.now()
            val zone = ZoneId.systemDefault()
            val todayStartMs = today.atStartOfDay(zone).toInstant().toEpochMilli()
            val weekStartMs = today.with(DayOfWeek.MONDAY).atStartOfDay(zone).toInstant().toEpochMilli()
            val todayEpochDays = today.toEpochDay()

            return combine(
                repository.getTasksCompletedSince(todayStartMs),
                repository.getTasksCompletedSince(weekStartMs),
                repository.getAllTasks(),
                repository.getOverdueIncompleteTasks(todayEpochDays),
                repository.getIncompleteTaskCount(),
            ) { completedToday, completedThisWeek, allTasks, overdueTasks, incompleteCount ->
                val completedAll = allTasks.count { it.isCompleted }
                val total = allTasks.size
                val completionRate = if (total > 0) completedAll.toFloat() / total else 0f

                val streak = calculateStreak(allTasks.filter { it.isCompleted }, today, zone)

                TaskMetrics(
                    completedToday = completedToday.size,
                    completedThisWeek = completedThisWeek.size,
                    streak = streak,
                    totalRemaining = incompleteCount,
                    completionRate = completionRate,
                    overdueCount = overdueTasks.size,
                )
            }
        }

        internal fun calculateStreak(
            completedTasks: List<com.nshaddox.randomtask.domain.model.Task>,
            today: LocalDate,
            zone: ZoneId,
        ): Int {
            val completionDays =
                completedTasks
                    .map { Instant.ofEpochMilli(it.updatedAt).atZone(zone).toLocalDate() }
                    .distinct()
                    .sortedDescending()

            if (completionDays.isEmpty() || completionDays.first() != today) return 0

            var streak = 0
            var expectedDay = today
            for (day in completionDays) {
                if (day == expectedDay) {
                    streak++
                    expectedDay = expectedDay.minusDays(1)
                } else if (day.isBefore(expectedDay)) {
                    break
                }
            }
            return streak
        }
    }
