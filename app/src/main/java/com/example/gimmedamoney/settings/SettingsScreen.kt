package com.example.gimmedamoney.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.BuildConfig
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.navigation.compose.rememberNavController
import com.example.gimmedamoney.UserViewModel
import com.example.gimmedamoney.ui.theme.DialogPopUp
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.ui.theme.Red
import com.example.gimmedamoney.ui.theme.TopNavBar

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class AppLanguage { SYSTEM, ENGLISH, DANISH }

// Holds settings state
class SettingsViewModel : ViewModel() {
    var theme by mutableStateOf(ThemeMode.SYSTEM); private set
    var language by mutableStateOf(AppLanguage.SYSTEM); private set
    var notificationsEnabled by mutableStateOf(true); private set
    var marketingEmails by mutableStateOf(false); private set
    var crashReports by mutableStateOf(true); private set
    var analytics by mutableStateOf(true); private set

    fun updateTheme(v: ThemeMode) {
        theme = v
    }
    fun updateLanguage(v: AppLanguage) { language = v }
    fun updateNotifications(v: Boolean) { notificationsEnabled = v }
    fun updateMarketing(v: Boolean) { marketingEmails = v }
    fun updateCrashReports(v: Boolean) { crashReports = v }
    fun updateAnalytics(v: Boolean) { analytics = v }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenPrivacyPolicy: () -> Unit = {},
    onOpenLicenses: () -> Unit = {},
    onExportData: () -> Unit = {},
    onClearCache: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onThemeChanged: (ThemeMode) -> Unit = {},
    onLanguageChanged: (AppLanguage) -> Unit = {},
    vm: SettingsViewModel = viewModel(),
    userVM: UserViewModel = viewModel(),
    onLogOut: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Settings",
                centerAligned = true,
            )
        },
        bottomBar = {
            SettingsBottomNavBar(
                onGoHome   = onOpenHome,
                onProfile  = onOpenProfile,
                onFavourites = { /* TODO */ }
            )
        }
    ) { inner ->
        val scroll = rememberScrollState()

        Column(
            Modifier
                .padding(inner)
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            Spacer(Modifier.height(8.dp))

            // Appearance
            SectionHeader("Appearance")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    ListItem(
                        headlineContent = { Text("Theme") },
                        supportingContent = { Text("Light/Dark/System") },
                        leadingContent = { Icon(Icons.Outlined.Palette, null) }
                    )
                    ThemeRow(
                        selected = vm.theme,
                        onSelect = { vm.updateTheme(it); onThemeChanged(it) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Language
            SectionHeader("Language")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    ListItem(
                        headlineContent = { Text("App language") },
                        supportingContent = { Text("May require restart") },
                        leadingContent = { Icon(Icons.Outlined.Language, null) }
                    )
                    LanguageDropdown(
                        current = vm.language,
                        onChange = { vm.updateLanguage(it); onLanguageChanged(it) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Notifications
            SectionHeader("Notifications")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(4.dp)) {
                    SwitchRow(
                        "Enable notifications",
                        checked = vm.notificationsEnabled,
                        onCheckedChange = vm::updateNotifications,
                        icon = { Icon(Icons.Outlined.Notifications, null) }
                    )
                    SwitchRow(
                        "Marketing emails",
                        supporting = "Product updates/offers",
                        checked = vm.marketingEmails,
                        onCheckedChange = vm::updateMarketing
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Privacy/Data
            SectionHeader("Privacy & data")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(4.dp)) {
                    SwitchRow(
                        "Share crash reports",
                        supporting = "Helps fix bugs",
                        checked = vm.crashReports,
                        onCheckedChange = vm::updateCrashReports,
                        icon = { Icon(Icons.Outlined.PrivacyTip, null) }
                    )
                    SwitchRow(
                        "Anonymous analytics",
                        supporting = "Usage metrics",
                        checked = vm.analytics,
                        onCheckedChange = vm::updateAnalytics
                    )
                    Divider()
                    ClickRow(
                        "Export my data",
                        supporting = "Download a copy",
                        onClick = onExportData,
                        icon = { Icon(Icons.Outlined.Description, null) }
                    )
                    ClickRow(
                        "Clear cache",
                        supporting = "Local temp files",
                        onClick = onClearCache
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // About/Legal
            SectionHeader("About")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(4.dp)) {
                    ClickRow("Privacy policy", onClick = onOpenPrivacyPolicy)
                    ClickRow("Open-source licenses", onClick = onOpenLicenses)
                    ListItem(
                        headlineContent = { Text("Version") },
                        supportingContent = { Text("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") },
                        leadingContent = { Icon(Icons.Outlined.Info, null) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Account", style = MaterialTheme.typography.titleMedium)

            var popupActive by remember{ mutableStateOf(false) }
            val userID = userVM.currentUser.value
            userID?.let { id ->
                if (popupActive){
                    DialogPopUp(
                        active = true,
                        title = "Confirm Deletion",
                        content = { Text("Are you sure?") },
                        onDismissRequest = { popupActive = false },
                        onConfirmation = { popupActive = false; userVM.deleteAccount(userID = id); onLogOut() },
                        confirmButtonColor = Red
                    )
                }

            }
            Button(
                onClick = { popupActive = true; onDeleteAccount },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(Icons.Outlined.Delete, null)
                Spacer(Modifier.width(8.dp))
                Text("Delete account")
            }
            Text(
                "This action is permanent.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SettingsBottomNavBar(
    onGoHome: () -> Unit,
    onProfile: () -> Unit = {},
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
            selected = false,
            onClick = onGoHome, // “Groups”
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
            selected = true, // you’re on Settings
            onClick = { /* no-op */ },
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


@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun SwitchRow(
    title: String,
    supporting: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: (@Composable () -> Unit)? = null
) {
    ListItem(
        leadingContent = icon,
        headlineContent = { Text(title) },
        supportingContent = supporting?.let { { Text(it) } },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun ClickRow(
    title: String,
    supporting: String? = null,
    onClick: () -> Unit,
    icon: (@Composable () -> Unit)? = null
) {
    ListItem(
        leadingContent = icon,
        headlineContent = { Text(title) },
        supportingContent = supporting?.let { { Text(it) } },
        trailingContent = { TextButton(onClick = onClick) { Text("Open") } }
    )
}

@Composable
private fun ThemeRow(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
    ) {
        ThemeMode.values().forEach { mode ->
            FilterChip(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                label = { Text(mode.name.lowercase().replaceFirstChar { it.titlecase() }) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(current: AppLanguage, onChange: (AppLanguage) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            value = current.name.lowercase().replaceFirstChar { it.titlecase() },
            onValueChange = {}, readOnly = true, label = { Text("Language") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppLanguage.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name.lowercase().replaceFirstChar { it.titlecase() }) },
                    onClick = { onChange(option); expanded = false }
                )
            }
        }
    }
}
