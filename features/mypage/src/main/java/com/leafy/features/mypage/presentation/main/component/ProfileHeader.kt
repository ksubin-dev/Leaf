package com.leafy.features.mypage.presentation.main.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyEditableProfileImage
import com.leafy.shared.ui.component.LeafyProfileImage
import com.subin.leafy.domain.model.User

@Composable
fun ProfileHeader(
    user: User,
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    editNickname: String,
    editBio: String,
    isNicknameValid: Boolean,
    editProfileImageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            if (isEditing) {
                val displayImage = editProfileImageUri ?: user.profileImageUrl

                LeafyEditableProfileImage(
                    imageUrl = displayImage,
                    size = 80.dp,
                    onImageClick = singleClick {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            } else {
                LeafyProfileImage(
                    imageUrl = user.profileImageUrl,
                    size = 80.dp,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconTint = MaterialTheme.colorScheme.outline,
                    contentDescription = "프로필 이미지"
                )
            }


            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = onNicknameChange,
                        label = { Text("닉네임", style = MaterialTheme.typography.bodySmall) },
                        isError = !isNicknameValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    if (!isNicknameValid) {
                        Text(
                            text = "사용할 수 없는 닉네임입니다.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = onBioChange,
                        label = { Text("한줄 소개", style = MaterialTheme.typography.bodySmall) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = user.nickname,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = user.bio?.takeIf { it.isNotBlank() } ?: "찻잎과의 여정을 시작합니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier.clickableSingle { onFollowerClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${user.socialStats.followerCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " 팔로워",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        modifier = Modifier.clickable { onFollowingClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${user.socialStats.followingCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " 팔로잉",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (isEditing) {
                OutlinedButton(
                    onClick = singleClick { onCancelClick() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("취소")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = singleClick { onEditClick() },
                    shape = RoundedCornerShape(8.dp),
                    enabled = isNicknameValid && editNickname.isNotBlank()
                ) {
                    Text("저장")
                }
            } else {
                OutlinedButton(
                    onClick = singleClick { onEditClick() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text("프로필 수정")
                }
            }
        }
    }
}