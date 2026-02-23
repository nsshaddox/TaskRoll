package com.nshaddox.randomtask.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SampleViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = getTasksUseCase()
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
