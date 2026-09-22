package com.example.presentation.screens.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.StudentEntity

@Composable
fun AdminManagementScreen(
    students: List<StudentEntity>,
    faculty: List<FacultyEntity>,
    onAddStudent: (StudentEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("STUDENTS") } // STUDENTS, FACULTY
    var showAddStudentDialog by remember { mutableStateOf(false) }

    // Add Student state
    var newName by remember { mutableStateOf("") }
    var newUsn by remember { mutableStateOf("") }
    var newDept by remember { mutableStateOf("AI & ML") }
    var newSem by remember { mutableStateOf("6") }
    var newSec by remember { mutableStateOf("A") }
    var newCgpa by remember { mutableStateOf("8.5") }

    Scaffold(
        floatingActionButton = {
            if (selectedCategory == "STUDENTS") {
                FloatingActionButton(
                    onClick = { showAddStudentDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("fab_add_student")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Enroll Student")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Institutional Directory & Enrollment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage university student enrollment rolls and faculty assignments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, ID, or department...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == "STUDENTS",
                        onClick = { selectedCategory = "STUDENTS" },
                        label = { Text("Students (${students.size})") }
                    )
                    FilterChip(
                        selected = selectedCategory == "FACULTY",
                        onClick = { selectedCategory = "FACULTY" },
                        label = { Text("Faculty (${faculty.size})") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (selectedCategory == "STUDENTS") {
                val filteredStudents = students.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.studentId.contains(searchQuery, ignoreCase = true) ||
                    it.department.contains(searchQuery, ignoreCase = true)
                }

                items(filteredStudents) { student ->
                    StudentDirectoryCard(student = student)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                val filteredFaculty = faculty.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.facultyId.contains(searchQuery, ignoreCase = true) ||
                    it.department.contains(searchQuery, ignoreCase = true)
                }

                items(filteredFaculty) { fac ->
                    FacultyDirectoryCard(faculty = fac)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("Enroll New Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Student Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newUsn,
                        onValueChange = { newUsn = it },
                        label = { Text("University USN (e.g. 1MS21AI055)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newSem,
                            onValueChange = { newSem = it },
                            label = { Text("Sem") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newSec,
                            onValueChange = { newSec = it },
                            label = { Text("Sec") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newCgpa,
                            onValueChange = { newCgpa = it },
                            label = { Text("CGPA") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newUsn.isNotBlank()) {
                            onAddStudent(
                                StudentEntity(
                                    studentId = newUsn.trim(),
                                    name = newName.trim(),
                                    email = "${newUsn.trim().lowercase()}@campusiq.edu",
                                    department = newDept.trim(),
                                    semester = newSem.toIntOrNull() ?: 6,
                                    section = newSec.trim(),
                                    batchYear = "2021-2025",
                                    phone = "+91 98765 43210",
                                    cgpa = newCgpa.toDoubleOrNull() ?: 8.0
                                )
                            )
                            newName = ""
                            newUsn = ""
                            showAddStudentDialog = false
                        }
                    }
                ) {
                    Text("Enroll")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudentDirectoryCard(student: StudentEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${student.studentId} • ${student.department} • Sem ${student.semester} (${student.section})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "CGPA ${student.cgpa}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FacultyDirectoryCard(faculty: FacultyEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = faculty.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${faculty.designation} • ${faculty.department} • ${faculty.facultyId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "Active",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
