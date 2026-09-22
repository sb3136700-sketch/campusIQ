package com.example.presentation.screens.student

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.StudentAcademicSummary
import com.example.analytics.SubjectAttendanceStat
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.model.AttendanceStatus
import com.example.presentation.components.MetricProgressBar
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen

@Composable
fun StudentAttendanceScreen(
    summary: StudentAcademicSummary?,
    allRecords: List<AttendanceRecordEntity>,
    studentId: String
) {
    val studentRecords = allRecords.filter { it.studentId == studentId }
    var selectedSubjectCode by remember { mutableStateOf("ALL") }

    val filteredRecords = if (selectedSubjectCode == "ALL") {
        studentRecords
    } else {
        studentRecords.filter { it.subjectCode == selectedSubjectCode }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Overall Compliance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Attendance Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Statutory requirement: Minimum 75% attendance in every course for SEE eligibility.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MetricProgressBar(
                        percentage = summary?.overallAttendancePercent ?: 82.5,
                        label = "Overall Institutional Attendance",
                        targetThreshold = 75.0
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Sessions: ${summary?.totalSessions ?: 0}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "Attended: ${summary?.totalAttended ?: 0}",
                            style = MaterialTheme.typography.labelMedium,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Absent: ${(summary?.totalSessions ?: 0) - (summary?.totalAttended ?: 0)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Subject-wise Breakdown Cards
        item {
            Text(
                text = "Course-wise Attendance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(summary?.subjectAttendance ?: emptyList()) { stat ->
            SubjectAttendanceCard(stat = stat)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Session Logs Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Detailed Class Session Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSubjectCode == "ALL",
                    onClick = { selectedSubjectCode = "ALL" },
                    label = { Text("All") }
                )
                summary?.subjectAttendance?.forEach { stat ->
                    FilterChip(
                        selected = selectedSubjectCode == stat.subjectCode,
                        onClick = { selectedSubjectCode = stat.subjectCode },
                        label = { Text(stat.subjectCode) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(filteredRecords) { record ->
            SessionRecordRow(record = record)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SubjectAttendanceCard(stat: SubjectAttendanceStat) {
    val isWarning = stat.isBelowThreshold
    val cardBorderColor = if (isWarning) DangerRed.copy(alpha = 0.5f) else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (isWarning) androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stat.subjectName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${stat.subjectCode} • ${stat.attendedSessions}/${stat.totalSessions} Lectures",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isWarning) DangerRed.copy(alpha = 0.12f) else SuccessGreen.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stat.percentage}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isWarning) DangerRed else SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            MetricProgressBar(
                percentage = stat.percentage,
                label = "Attendance percentage",
                targetThreshold = 75.0
            )

            if (isWarning) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DangerRed.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Low Attendance Warning! Attend the next ${stat.sessionsNeededFor75} lecture(s) consecutively to restore 75% compliance.",
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionRecordRow(record: AttendanceRecordEntity) {
    val isPresent = record.status == AttendanceStatus.PRESENT
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isPresent) SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPresent) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isPresent) "Present" else "Absent",
                        tint = if (isPresent) SuccessGreen else DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = record.topicCovered.ifEmpty { "Lecture Session" },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${record.subjectCode} • ${record.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isPresent) SuccessGreen.copy(alpha = 0.1f) else DangerRed.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (isPresent) "PRESENT" else "ABSENT",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isPresent) SuccessGreen else DangerRed,
                    fontSize = 10.sp
                )
            }
        }
    }
}
