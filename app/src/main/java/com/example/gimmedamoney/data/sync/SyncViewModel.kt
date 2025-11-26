package com.example.gimmedamoney.data.sync

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncViewModel : ViewModel() {

    private val _syncEvent = MutableStateFlow<SyncEvent>(SyncEvent.Idle)
    val syncEvent = _syncEvent.asStateFlow()

    fun reportPending(type: SyncType, id: String) {
        _syncEvent.value = SyncEvent.Pending(type, id)
    }

    fun reportSynced(type: SyncType, id: String) {
        _syncEvent.value = SyncEvent.Synced(type, id)
    }

    fun reportError(type: SyncType, id: String?, message: String) {
        _syncEvent.value = SyncEvent.Error(type, id, message)
    }

    fun reset() {
        _syncEvent.value = SyncEvent.Idle
    }
}
