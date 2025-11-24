package com.example.gimmedamoney

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.Green
import com.example.gimmedamoney.ui.theme.Red
import com.example.gimmedamoney.ui.theme.TopNavBar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGroupPress: (String) -> Unit,
    onCreateGroup: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProfile: () -> Unit,
    groupVM: GroupViewModel = viewModel(),
    userVM: UserViewModel = viewModel()
) {

    val userID by userVM.currentUser.collectAsState()
    val groups by groupVM.groups.collectAsState()

    LaunchedEffect(userID) {
        userID?.let { id ->
            groupVM.getUserGroups(id)
        }
    }

    Scaffold(
        topBar = {
            TopNavBar(
                title = "GimmeDaMoney",
                centerAligned = true,
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                onSettings   = onOpenSettings,
                onProfile    = onOpenProfile,
                onGroups     = {},
                onFavourites = { /* TODO */ }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            if (groups.isEmpty()) {
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
                    items(groups, key = { it.id }) { g ->
                        Text(g.name)
                        GroupCard(
                            group = GroupSummary(g.id, g.name),
                            onClick = { onGroupPress(g.id) }
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
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            Modifier
                .height(120.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(Modifier.padding(12.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
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
                color = Red
            )
            Spacer(Modifier.height(4.dp))
            BalancePill(
                label = "You are owed",
                value = group.youAreOwedDkk,
                color = Green
            )
        }
    }
}

@Composable
private fun BalancePill(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
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
            tint = MaterialTheme.colorScheme.secondary
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
            color = MaterialTheme.colorScheme.onBackground
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
private fun BottomNavBar(
    onSettings: () -> Unit,
    onProfile: () -> Unit = {},
    onGroups: () -> Unit = {},
    onFavourites: () -> Unit = {}
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = false,
            onClick = onProfile,
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Profile", color = MaterialTheme.colorScheme.onSurface) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = onGroups,
            icon = { Icon(Icons.Outlined.Group, null) },
            label = { Text("Groups", color = MaterialTheme.colorScheme.onSurface) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onFavourites,
            icon = { Icon(Icons.Outlined.FavoriteBorder, null) },
            label = { Text("Favourites", color = MaterialTheme.colorScheme.onSurface) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onSettings,
            icon = { Icon(Icons.Outlined.Settings, null) },
            label = { Text("Settings", color = MaterialTheme.colorScheme.onSurface) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

private fun dkk(v: Double) = String.format("%.2f DKK", v)