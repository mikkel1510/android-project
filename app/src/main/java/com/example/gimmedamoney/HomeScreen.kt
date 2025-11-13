package com.example.gimmedamoney

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.GroupViewModel.Group
import com.example.gimmedamoney.GroupViewModel
import com.example.gimmedamoney.UserViewModel.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMembersPress: () -> Unit,
    onCreateGroup: () -> Unit,
    vm: GroupViewModel = viewModel()
) {

    //Dummy group
    val dummyGroup = Group("1","dummy", members = listOf())
    if (vm.groups.isEmpty()){
        vm.addGroup(dummyGroup)
    }
    val summary = GroupSummary(id = "1", name = "dummysummary")
    if (vm.groupSummaries.isEmpty()){
        vm.addGroupSummary(summary)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GimmeDaMoney") },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { BottomNavBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {

            if (vm.groups.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyState(onCreate = onCreateGroup, modifier = Modifier.fillMaxWidth())

                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 96.dp),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vm.groupSummaries, key = { it.id }) { g ->
                        GroupCard(
                            group = g,
                            onClick = onMembersPress
                        )
                    }
                }
            }

            BigCenterFab(
                size = 88.dp,
                onClick = onCreateGroup,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
            )
        }
    }
}

@Composable
private fun GroupCard(
    group: GroupSummary,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            Modifier
                .height(120.dp)
                .fillMaxWidth()
                .background(Color(0xFFEDEEF2))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu_gallery),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(Modifier.padding(12.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ReceiptLong,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("Total", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Text(
                    dkk(group.totalDkk),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(6.dp))

            BalancePill(
                label = "You owe",
                value = group.youOweDkk,
                color = Color(0xFFD14D4D)
            )
            Spacer(Modifier.height(4.dp))
            BalancePill(
                label = "You are owed",
                value = group.youAreOwedDkk,
                color = Color(0xFF2EAF5D)
            )
        }
    }
}

@Composable
private fun BalancePill(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "$label  ${dkk(value)}",
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BigCenterFab(size: Dp, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .size(size)
            .shadow(8.dp, CircleShape),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(Icons.Outlined.Add, contentDescription = "Create group")
    }
}

@Composable
private fun EmptyState(
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "No groups yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Tap the button below to create your first group.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onCreate) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Create group")
        }
    }
}

@Composable
private fun BottomNavBar() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = false, onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Profile") }
        )
        NavigationBarItem(
            selected = true, onClick = { /* already here */ },
            icon = { Icon(Icons.Outlined.Group, null) },
            label = { Text("Groups") }
        )
        NavigationBarItem(
            selected = false, onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.FavoriteBorder, null) },
            label = { Text("Favourites") }
        )
        NavigationBarItem(
            selected = false, onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.Settings, null) },
            label = { Text("Settings") }
        )
    }
}

private fun dkk(v: Double) = String.format("%.2f DKK", v)