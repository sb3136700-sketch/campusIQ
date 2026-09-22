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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.StudentAcademicSummary
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.StudentEntity
import com.example.presentation.components.MetricProgressBar
import com.example.presentation.components.RiskBadge
import com.example.presentation.components.StatCard
import com.example.presentation.components.SubjectPerformanceBar
import com.example.presentation.navigation.StudentTab
import com.example.ui.theme.BrandAmberTertiary
import com.example.ui.theme.BrandTealSecondary
import com.example.ui.theme.SuccessGreen

@Composable
fun StudentDashboardScreen(
    student: StudentEntity?,
    summary: StudentAcademicSummary?,
    assignments: List<AssignmentEntity>,
    onNavigateTab: (StudentTab) -> Unit
) {
    val studentName = student?.name ?: "Rohan Sharma"
    val studentUsn = student?.studentId ?: "1MS21AI042"
    val semester = student?.semester ?: 6
    val dept = student?.department ?: "AI & ML"
    val cgpa = student?.cgpa ?: 8.64

    val pendingCount = assignments.count { !it.isSubmitted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = studentName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$studentUsn • $dept • Sem $semester (Sec A)",
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Cumulative GPA: $cgpa",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (summary != null) {
                        RiskBadge(level = summary.riskLevel)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stat Grid
        Text(
            text = "Academic Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val attendanceVal = summary?.overallAttendancePercent?.toString() ?: "82.5"
            StatCard(
                title = "Attendance",
                value = "$attendanceVal%",
                subtitle = "${summary?.totalAttended ?: 70}/${summary?.totalSessions ?: 85} Sessions",
                icon = Icons.Default.DateRange,
                accentColor = BrandTealSecondary,
                modifier = Modifier.weight(1f)
            )
            val sgpaVal = summary?.projectedSgpa?.toString() ?: "8.75"
            StatCard(
                title = "Proj. SGPA",
                value = sgpaVal,
                subtitle = "6th Sem Projection",
                icon = Icons.Default.TrendingUp,
                accentColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Pending Tasks",
                value = "$pendingCount",
                subtitle = "Active Submissions",
                icon = Icons.Default.Assignment,
                accentColor = BrandAmberTertiary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Subjects",
                value = "${summary?.subjectMarks?.size ?: 5}",
                subtitle = "16 Total Credits",
                icon = Icons.Default.Grade,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Overall Attendance Meter Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Attendance Compliance Tracking",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                MetricProgressBar(
                    percentage = summary?.overallAttendancePercent ?: 82.5,
                    label = "Combined Attendance (All Subjects)",
                    targetThreshold = 75.0
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Advisor Teaser Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CampusIQ AI Insights",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Strong performance in Machine Learning & NLP! Cloud Computing attendance requires attention to secure exam eligibility.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onNavigateTab(StudentTab.AI_ADVISOR) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Open AI Academic Advisor", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subject Performance Snippet
        Text(
            text = "Subject Performance Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        summary?.subjectMarks?.forEach { stat ->
            SubjectPerformanceBar(
                subjectName = stat.subjectName,
                subjectCode = stat.subjectCode,
                scorePercentage = stat.overallPercentage,
                grade = stat.grade,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
