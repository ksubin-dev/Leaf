package com.leafy.features.timer.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.timer.ui.TimerSideEffect
import com.leafy.features.timer.ui.components.DetailedPresetCard
import com.leafy.features.timer.ui.components.FilterChipItem
import com.leafy.features.timer.ui.components.PresetEditDialog
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.TimerPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetListScreen(
    presets: List<TimerPreset>,
    onBackClick: () -> Unit,
    onPresetSelect: (TimerPreset) -> Unit,
    onAddPreset: (TimerPreset) -> Unit,
    onUpdatePreset: (TimerPreset) -> Unit,
    onDeletePreset: (String) -> Unit,
    effectFlow: Flow<TimerSideEffect>,
    onLockedClick: () -> Unit = {},
    onSettingsClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<TeaType?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var targetPreset by remember { mutableStateOf<TimerPreset?>(null) }

    val filteredPresets = remember(presets, selectedCategory) {
        if (selectedCategory == null) presets
        else presets.filter { it.teaType == selectedCategory }
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is TimerSideEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Brewing Presets", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = singleClick { onSettingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "타이머 설정",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = singleClick {
                            targetPreset = null
                            showEditDialog = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "레시피 추가",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChipItem(
                        text = "All",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                val teaTypes = TeaType.entries.filter { it != TeaType.UNKNOWN }
                items(teaTypes) { type ->
                    FilterChipItem(
                        text = type.label,
                        isSelected = selectedCategory == type,
                        onClick = { selectedCategory = type }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredPresets.isEmpty()) {
                EmptyPresetState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredPresets, key = { it.id }) { preset ->
                        DetailedPresetCard(
                            preset = preset,
                            onClick = { onPresetSelect(preset) },
                            onEditClick = {
                                targetPreset = preset
                                showEditDialog = true
                            },
                            onLockedClick = onLockedClick
                        )
                    }
                }
            }
        }

        if (showEditDialog) {
            PresetEditDialog(
                initialPreset = targetPreset,
                onDismissRequest = { showEditDialog = false },
                onSaveClick = {
                    if (targetPreset == null) onAddPreset(it) else onUpdatePreset(it)
                    showEditDialog = false
                },
                onDeleteClick = {
                    onDeletePreset(it)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun EmptyPresetState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("해당하는 레시피가 없습니다.", color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PresetListScreenPreview() {
    val mockPresets = remember {
        listOf(
            TimerPreset(
                id = "1",
                name = "우전 녹차 (표준)",
                teaType = TeaType.GREEN,
                isDefault = true,
                recipe = BrewingRecipe(
                    waterTemp = 75,
                    leafAmount = 3f,
                    waterAmount = 150,
                    brewTimeSeconds = 120,
                    infusionCount = 1,
                    teaware = TeawareType.KYUSU
                )
            ),
            TimerPreset(
                id = "2",
                name = "진한 오후의 홍차",
                teaType = TeaType.BLACK,
                isDefault = false,
                recipe = BrewingRecipe(
                    waterTemp = 95,
                    leafAmount = 2.5f,
                    waterAmount = 200,
                    brewTimeSeconds = 180,
                    infusionCount = 1,
                    teaware = TeawareType.MUG
                )
            )
        )
    }

    LeafyTheme {
        PresetListScreen(
            presets = mockPresets,
            onBackClick = { },
            onPresetSelect = { },
            onAddPreset = { },
            onUpdatePreset = { },
            onDeletePreset = { },
            effectFlow = emptyFlow(),
            onLockedClick = {},
            onSettingsClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PresetEditDialogPreview() {
    LeafyTheme {
        PresetEditDialog(
            initialPreset = null,
            onDismissRequest = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}