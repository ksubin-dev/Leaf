package com.leafy.features.note.screen



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.sections.* // 모든 섹션 컴포넌트 Import
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteScreen() {

    val colors = MaterialTheme.colorScheme

    // Basic Tea Information 상태 관리 (Hoisted States)
    var teaName by remember { mutableStateOf("Enter Tea Name") }
    var brandName by remember { mutableStateOf("Enter brand name") }
    var teaType by remember { mutableStateOf("Black") }
    var leafStyle by remember { mutableStateOf("Loose Leaf") }
    var leafProcessing by remember { mutableStateOf("Whole Leaf") }
    var teaGrade by remember { mutableStateOf("OP") }


    LeafyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "New Brewing Note",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO: navController.navigateUp() */ }) {
                            Icon(
                                painter = painterResource(id = SharedR.drawable.ic_back),
                                contentDescription = "Back",
                                modifier = Modifier.height(20.dp),
                                tint = colors.onSurface
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { /* TODO: Save action */ }) {
                            Text(
                                text = "Save",
                                color = colors.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                // ---------------- 1. Photos 섹션 ----------------
                PhotosSection(
                    onClickDryLeaf = { /* TODO */ },
                    onClickTeaLiquor = { /* TODO */ },
                    onClickTeaware = { /* TODO */ },
                    onClickAdditional = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- 2. Basic Tea Information 섹션 ----------------
                BasicTeaInformationSection(
                    teaName = teaName,
                    onTeaNameChange = { teaName = it },
                    brandName = brandName,
                    onBrandNameChange = { brandName = it },
                    teaType = teaType,
                    onTeaTypeChange = { teaType = it },
                    leafStyle = leafStyle,
                    onLeafStyleChange = { leafStyle = it },
                    leafProcessing = leafProcessing,
                    onLeafProcessingChange = { leafProcessing = it },
                    teaGrade = teaGrade,
                    onTeaGradeChange = { teaGrade = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- 3. Tasting Context 섹션 ----------------
                TastingContextSection()


                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- 4. Brewing Conditions 섹션 ----------------
                BrewingConditionSection()

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- 5. Sensory Evaluation 섹션 ----------------
                SensoryEvaluationSection()

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- 6. Final Rating 섹션 ----------------
                FinalRatingSection()

                Spacer(modifier = Modifier.height(32.dp))

            }
        }
    }
}
