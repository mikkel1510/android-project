package com.example.gimmedamoney.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gimmedamoney.viewmodel.GroupViewModel
import com.example.gimmedamoney.viewmodel.GroupViewModel.GroupActionState

@Composable
fun GroupSyncSnackbarHandler(
    groupVM: GroupViewModel,
    snackbarHostState: SnackbarHostState
) {
    val groupActionState by groupVM.groupActionState.collectAsState()

    LaunchedEffect(groupActionState) {
        when (groupActionState) {
            is GroupActionState.PendingSync -> {
                snackbarHostState.showSnackbar(
                    "Group not yet synced — changes saved locally."
                )
            }
            is GroupActionState.Synced -> {
                snackbarHostState.showSnackbar(
                    "Group created and synced successfully."
                )
                groupVM.resetGroupActionState()
            }
            is GroupActionState.Error -> {
                snackbarHostState.showSnackbar((groupActionState as GroupActionState.Error).message)
                groupVM.resetGroupActionState()
            }
            else -> Unit
        }
    }
}
