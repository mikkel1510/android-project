package com.example.gimmedamoney.ui.group

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.gimmedamoney.ui.common.GroupSyncSnackbarHandler
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.ui.theme.TopNavBar
import com.example.gimmedamoney.viewmodel.GroupViewModel
import com.example.gimmedamoney.viewmodel.GroupViewModel.GroupActionState
import com.example.gimmedamoney.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBackPress: () -> Unit,
    onGroupCreatePress: (String) -> Unit,
    vm: GroupViewModel,
    userVM: UserViewModel,
    snackbarHostState: SnackbarHostState
) {
    GroupSyncSnackbarHandler(vm, snackbarHostState)
    val userID by userVM.currentUser.collectAsState()

    var groupName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val groupActionState by vm.groupActionState.collectAsState()

    Scaffold(
        topBar = {
            TopNavBar(
                title = "Create Group",
                navigationIcon = {
                    IconButton(onClick = { onBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            PrimaryButton(
                text = if (selectedImageUri == null) "Select Picture" else "Change Picture",
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            )
            val isCreating = groupActionState is GroupActionState.Creating
            PrimaryButton(
                text = if (isCreating) "Creating..." else "Create Group",
                onClick = {
                    userID?.let { id ->
                        vm.createGroup(
                            groupName,
                            selectedImageUri.toString(),
                            id
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }


    }
    LaunchedEffect(groupActionState) {
        when (groupActionState) {
            is GroupActionState.PendingSync -> {
                onGroupCreatePress((groupActionState as GroupActionState.PendingSync).groupID)
            }
            else -> Unit
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview(){
    GimmeDaMoneyTheme {
        val userVM: UserViewModel = viewModel()
        val groupVM: GroupViewModel = viewModel()
        val snackbarHost = remember { SnackbarHostState() }
        CreateGroupScreen(
            onBackPress = {},
            onGroupCreatePress = {},
            userVM = userVM,
            vm = groupVM,
            snackbarHostState = snackbarHost
        )
    }
}