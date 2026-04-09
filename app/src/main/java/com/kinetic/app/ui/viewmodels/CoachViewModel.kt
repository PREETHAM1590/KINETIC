package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.CoachInsight
import com.kinetic.app.data.store.UserActivityStore
import com.kinetic.app.domain.CoachEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val store: UserActivityStore,
    private val engine: CoachEngine
) : ViewModel() {

    val topInsight: StateFlow<CoachInsight?> = store.state
        .map { engine.generateInsights(it).firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
