package com.example.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.DepartmentMetric
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.model.UserRole
import com.example.presentation.components.StatCard
import com.example.ui.theme.BrandAmberTertiary
import com.example.ui.theme.BrandTealSecondary
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminDashboardScreen(
    students: List<StudentEntity>,
    faculty: List<FacultyEntity>,
    departmentMetrics: List<DepartmentMetric>,
    onBroadcastAnnouncement: (String, String, UserRole) -> Unit
) {
    var announcementTitle by remember { mutableStateOf("") }
    var announcementMsg by remember { mutableStateOf("") }

    val totalStudents = departmentMetrics.sumOf { it.totalStudents }
    val totalFaculty = departmentMetrics.sumOf { it.facultyCount }
    val avgAttendance = (departmentMetrics.map { it.averageAttendance }.average() * 10).toInt() / 10.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Admin Executive Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Institutional Governance,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Dr. S. K. Murthy",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Dean of Academic Affairs • Campus Administration",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "College-Wide Academic Indicators",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Enrolled",
                value = "$totalStudents",
                subtitle = "Across 4 Engineering Depts",
                icon = Icons.Default.Groups,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Faculty Body",
                value = "$totalFaculty",
                subtitle = "Professors & Lecturers",
                icon = Icons.Default.Person,
                accentColor = BrandTealSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Campus Attendance",
                value = "$avgAttendance%",
                subtitle = "Statutory Target: 75%",
                icon = Icons.Default.DateRange,
                accentColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Institutional GPA",
                value = "8.23",
                subtitle = "Average Student CGPA",
                icon = Icons.Default.TrendingUp,
                accentColor = BrandAmberTertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Broadcast Announcement Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Campus Announcement",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = announcementTitle,
                    onValueChange = { announcementTitle = it },
                    label = { Text("Announcement Headline") },
                    placeholder = { Text("e.g. Schedule for 2nd CIE Assessments") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = announcementMsg,
                    onValueChange = { announcementMsg = it },
                    label = { Text("Announcement Content") },
                    placeholder = { Text("Detailed notice for faculty and students...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (announcementTitle.isNotBlank() && announcementMsg.isNotBlank()) {
                            onBroadcastAnnouncement(announcementTitle, announcementMsg, UserRole.STUDENT)
                            announcementTitle = ""
                            announcementMsg = ""
                        }
                    },
                    enabled = announcementTitle.isNotBlank() && announcementMsg.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_broadcast_announcement"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast to Students & Faculty")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
