package com.example.gimmedamoney.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    title: String,
    subtitle: String? = null,
    centerAligned: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
){
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )

    Surface(
        shadowElevation = 4.dp,
        color = Color.Black
    ) {
        if (centerAligned) {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = actions,
                navigationIcon = navigationIcon,
                colors = colors,
            )


        } else {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = actions,
                navigationIcon = navigationIcon,
                colors = colors
            )
        }
    }

}
@Composable
fun DialogPopUp(
    active: Boolean,
    title: String,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    confirmButtonColor: Color? = null
){
    if (!active) return
    AlertDialog(
        title = {
            Text(title)
        },
        text = { content() },
        textContentColor = MaterialTheme.colorScheme.onBackground,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            PrimaryButton(onClick = { onConfirmation()}, text = "Confirm", color = confirmButtonColor, modifier = Modifier.size(100.dp, 50.dp))
        },
        dismissButton = {
            PrimaryButton(onClick = { onDismissRequest() }, text = "Cancel", modifier = Modifier.size(100.dp, 50.dp))
        }
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    color: Color? = null
){
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = color ?: MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ){
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = "Button Icon"
            )
        }
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview(){
    GimmeDaMoneyTheme {
        PrimaryButton("Primary Button", {})
    }
}

@Preview(showBackground = true)
@Composable
fun TopNavBarPreview(){
    GimmeDaMoneyTheme {
        TopNavBar(
            "Top Bar",
            "Subtitle",
            false,
            {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new member",
                    )
                }
            },
            {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DialogPopupPreview(){
    GimmeDaMoneyTheme{
        DialogPopUp(
            active = true,
            title = "Popup",
            content = {
                Row{
                    Text("Hello ", fontWeight = FontWeight.Bold)
                    Text("there")
                }
            },
            onDismissRequest = { },
            onConfirmation = {},
            confirmButtonColor = Red
        )
    }
}