package com.example.gimmedamoney.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gimmedamoney.data.sync.SyncEvent
import com.example.gimmedamoney.data.sync.SyncType
import com.example.gimmedamoney.data.sync.SyncViewModel

@Composable
fun SyncSnackbarHandler(
    syncVM: SyncViewModel,
    snackbarHostState: SnackbarHostState
) {
    val syncEvent by syncVM.syncEvent.collectAsState()

    LaunchedEffect(syncEvent) {
        when (val event = syncEvent) {
            is SyncEvent.Pending -> {
                when (event.type) {
                    SyncType.MESSAGE -> {
                    }
                    SyncType.GROUP -> {
                        snackbarHostState.showSnackbar(
                            "Group not yet synced — changes saved locally."
                        )
                    }
                    SyncType.EXPENSE -> {
                        snackbarHostState.showSnackbar(
                            "Request not yet synced — changes saved locally."
                        )
                    }
                }
            }

            is SyncEvent.Synced -> {
                when (event.type) {
                    SyncType.MESSAGE -> {
                    }
                    SyncType.GROUP -> {
                        snackbarHostState.showSnackbar(
                            "Group created and synced successfully."
                        )
                    }
                    SyncType.EXPENSE -> {
                        snackbarHostState.showSnackbar(
                            "Request synced successfully."
                        )
                    }
                }
            }

            is SyncEvent.Error -> {
                snackbarHostState.showSnackbar(event.message)
            }

            SyncEvent.Idle -> Unit
        }
    }
}

