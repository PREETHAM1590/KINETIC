package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.ChartDataPoint
import com.kinetic.app.data.models.PersonalRecord
import com.kinetic.app.data.models.WeeklyReport
import com.kinetic.app.data.repository.ReportsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val isLoading: Boolean = true,
    val reports: List<WeeklyReport> = emptyList(),
    val personalRecords: List<PersonalRecord> = emptyList(),
    val chartData: List<ChartDataPoint> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                reportsRepository.getWeeklyReports(),
                reportsRepository.getPersonalRecords(),
                reportsRepository.getChartData()
            ) { reports, records, chart ->
                ReportsUiState(
                    isLoading = false,
                    reports = reports,
                    personalRecords = records,
                    chartData = chart
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
