package com.leafy.features.mypage.presentation.tea.edit

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.subin.leafy.domain.model.TeaType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaAddEditScreen(
    onBackClick: () -> Unit,
    onRecordClick: (String) -> Unit,
    viewModel: TeaAddEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is TeaAddEditSideEffect.SaveSuccess,
                is TeaAddEditSideEffect.DeleteSuccess -> {
                    onBackClick()
                }
                is TeaAddEditSideEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            uiState.teaId == null -> "차 추가"
                            uiState.isEditMode -> "차 정보 수정"
                            else -> "차 상세 정보"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick {
                        if (uiState.isEditMode && uiState.teaId != null) {
                            viewModel.toggleEditMode()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = if (uiState.isEditMode && uiState.teaId != null) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기 또는 취소"
                        )
                    }
                },
                actions = {
                    if (uiState.teaId != null) {
                        if (!uiState.isEditMode) {
                            IconButton(onClick = singleClick { viewModel.toggleEditMode() }) {
                                Icon(Icons.Default.Edit, contentDescription = "수정")
                            }
                            IconButton(onClick = singleClick { viewModel.deleteTea() }) {
                                Icon(Icons.Default.Delete, contentDescription = "삭제", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                if (uiState.isEditMode) {
                    TeaImagePicker(
                        selectedUri = uiState.selectedImageUri,
                        currentUrl = uiState.currentImageUrl,
                        onImageSelected = viewModel::onImageSelected
                    )
                } else {
                    val displayUrl = uiState.selectedImageUri ?: uiState.currentImageUrl
                    if (displayUrl != null) {
                        AsyncImage(
                            model = displayUrl,
                            contentDescription = "차 이미지",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.brand,
                    onValueChange = viewModel::onBrandChange,
                    label = { Text("브랜드") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = !uiState.isEditMode,
                    enabled = uiState.isEditMode,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = Color.Transparent,
                        disabledLabelColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = !uiState.isEditMode,
                    enabled = uiState.isEditMode,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = Color.Transparent,
                        disabledLabelColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("종류", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isEditMode) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TeaType.entries.filter { it != TeaType.UNKNOWN }.forEach { type ->
                                FilterChip(
                                    selected = type == uiState.selectedType,
                                    onClick = singleClick { viewModel.onTypeSelected(type) },
                                    label = { Text(type.label) },
                                    leadingIcon = if (type == uiState.selectedType) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = uiState.selectedType.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (uiState.isEditMode || uiState.origin.isNotBlank()) {
                    OutlinedTextField(
                        value = uiState.origin,
                        onValueChange = viewModel::onOriginChange,
                        label = { Text("산지") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = !uiState.isEditMode,
                        enabled = uiState.isEditMode,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = Color.Transparent,
                            disabledLabelColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                if (uiState.isEditMode || uiState.stockQuantity.isNotBlank()) {
                    OutlinedTextField(
                        value = uiState.stockQuantity,
                        onValueChange = viewModel::onStockChange,
                        label = { Text("보유량") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = !uiState.isEditMode,
                        enabled = uiState.isEditMode,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = Color.Transparent,
                            disabledLabelColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                if (uiState.isEditMode || uiState.memo.isNotBlank()) {
                    OutlinedTextField(
                        value = uiState.memo,
                        onValueChange = viewModel::onMemoChange,
                        label = { Text("메모") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 10,
                        readOnly = !uiState.isEditMode,
                        enabled = uiState.isEditMode,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = Color.Transparent,
                            disabledLabelColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.isEditMode -> {
                        Button(
                            onClick = singleClick { viewModel.saveTea() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.isFormValid && !uiState.isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (uiState.teaId == null) "추가하기" else "저장하기", modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                    uiState.teaId != null -> {
                        Button(
                            onClick = singleClick { onRecordClick(uiState.teaId!!) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "이 차로 기록하기",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isLoading) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun TeaImagePicker(
    selectedUri: Uri?,
    currentUrl: String?,
    onImageSelected: (Uri?) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> onImageSelected(uri) }

    val displayModel = selectedUri ?: currentUrl

    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickableSingle {
                imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        contentAlignment = Alignment.Center
    ) {
        if (displayModel != null) {
            AsyncImage(
                model = displayModel,
                contentDescription = "차 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text("사진 추가", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}