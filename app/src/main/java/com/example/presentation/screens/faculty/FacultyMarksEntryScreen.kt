package com.example.presentation.screens.faculty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.ExamType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyMarksEntryScreen(
    subjects: List<SubjectEntity>,
    students: List<StudentEntity>,
    onSaveMark: (String, String, ExamType, Double, Double, String) -> Unit
) {
    var selectedSubjectCode by remember { mutableStateOf(subjects.firstOrNull()?.subjectCode ?: "CS601") }
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.studentId ?: "1MS21AI042") }
    var selectedExamType by remember { mutableStateOf(ExamType.INTERNAL_2) }

    var scoreText by remember { mutableStateOf("26.5") }
    var maxScoreText by remember { mutableStateOf("30.0") }
    var remarksText by remember { mutableStateOf("Sound understanding of attention mechanisms and Transformer layers.") }

    var subjectMenuExpanded by remember { mutableStateOf(false) }
    var studentMenuExpanded by remember { mutableStateOf(false) }
    var examMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Continuous Internal Evaluation (CIE) Entry",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Record marks for Internal Tests, Assignments, or Lab Practicals. Updates student grades immediately.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Course Dropdown
                ExposedDropdownMenuBox(
                    expanded = subjectMenuExpanded,
                    onExpandedChange = { subjectMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubjectCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = subjectMenuExpanded,
                        onDismissRequest = { subjectMenuExpanded = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text("${subject.subjectCode} - ${subject.name}") },
                                onClick = {
                                    selectedSubjectCode = subject.subjectCode
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Student Dropdown
                val currentStudentObj = students.firstOrNull { it.studentId == selectedStudentId }
                ExposedDropdownMenuBox(
                    expanded = studentMenuExpanded,
                    onExpandedChange = { studentMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${currentStudentObj?.name ?: selectedStudentId} (${selectedStudentId})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Student") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = studentMenuExpanded,
                        onDismissRequest = { studentMenuExpanded = false }
                    ) {
                        students.forEach { student ->
                            DropdownMenuItem(
                                text = { Text("${student.name} - ${student.studentId}") },
                                onClick = {
                                    selectedStudentId = student.studentId
                                    studentMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Exam Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = examMenuExpanded,
                    onExpandedChange = { examMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = when (selectedExamType) {
                            ExamType.INTERNAL_1 -> "Internal Assessment Test 1"
                            ExamType.INTERNAL_2 -> "Internal Assessment Test 2"
                            ExamType.ASSIGNMENT -> "Course Assignment / Mini Project"
                            ExamType.LAB_PRACTICAL -> "Laboratory Practical Exam"
                            else -> selectedExamType.name
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assessment Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = examMenuExpanded,
                        onDismissRequest = { examMenuExpanded = false }
                    ) {
                        listOf(
                            ExamType.INTERNAL_1 to "Internal Assessment Test 1 (Max 30)",
                            ExamType.INTERNAL_2 to "Internal Assessment Test 2 (Max 30)",
                            ExamType.ASSIGNMENT to "Course Assignment (Max 20)",
                            ExamType.LAB_PRACTICAL to "Laboratory Practical (Max 50)"
                        ).forEach { (type, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedExamType = type
                                    maxScoreText = when (type) {
                                        ExamType.INTERNAL_1, ExamType.INTERNAL_2 -> "30.0"
                                        ExamType.ASSIGNMENT -> "20.0"
                                        ExamType.LAB_PRACTICAL -> "50.0"
                                        else -> "100.0"
                                    }
                                    examMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = scoreText,
                        onValueChange = { scoreText = it },
                        label = { Text("Marks Scored") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_marks_score")
                    )

                    OutlinedTextField(
                        value = maxScoreText,
                        onValueChange = { maxScoreText = it },
                        label = { Text("Max Marks") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = remarksText,
                    onValueChange = { remarksText = it },
                    label = { Text("Constructive Feedback / Remarks") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val score = scoreText.toDoubleOrNull() ?: 0.0
                        val maxScore = maxScoreText.toDoubleOrNull() ?: 30.0
                        onSaveMark(
                            selectedStudentId,
                            selectedSubjectCode,
                            selectedExamType,
                            score,
                            maxScore,
                            remarksText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_mark"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Publish Assessment Record", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
