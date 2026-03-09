package com.example.myapplication.data.local

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.AcneCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel : ViewModel() {

    private val _cases = MutableStateFlow<List<AcneCase>>(emptyList())
    val cases: StateFlow<List<AcneCase>> = _cases

    fun addCase(case: AcneCase) {
        _cases.value = _cases.value + case
    }

    fun getCaseById(id: String): AcneCase? {
        return _cases.value.find { it.id == id }
    }

    fun updateCase(updatedCase: AcneCase) {
        _cases.value = _cases.value.map {
            if (it.id == updatedCase.id) updatedCase else it
        }
    }
}
