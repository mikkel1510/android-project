package com.example.gimmedamoney.data.sync
enum class SyncType {
    GROUP,
    MESSAGE,
    EXPENSE
}

sealed class SyncEvent {
    object Idle : SyncEvent()

    data class Pending(
        val type: SyncType,
        val id: String
    ) : SyncEvent()

    data class Synced(
        val type: SyncType,
        val id: String
    ) : SyncEvent()

    data class Error(
        val type: SyncType,
        val id: String?,
        val message: String
    ) : SyncEvent()
}
