package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.presentation.navigation.AdminTab
import com.example.presentation.navigation.FacultyTab
import com.example.presentation.navigation.StudentTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusTopAppBar(
    currentUser: UserEntity?,
    onSwitchRole: (UserRole) -> Unit,
    onLogout: () -> Unit,
    onNotificationClick: () -> Unit,
    unreadNotificationCount: Int = 2
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "CampusIQ",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "Campus",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "IQ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            // Role chip badge
            if (currentUser != null) {
                val (roleLabel, roleColor) = when (currentUser.role) {
                    UserRole.STUDENT -> "Student" to MaterialTheme.colorScheme.primary
                    UserRole.FACULTY -> "Faculty" to MaterialTheme.colorScheme.secondary
                    UserRole.ADMIN -> "Admin" to MaterialTheme.colorScheme.tertiary
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = roleColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = roleLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = roleColor
                    )
                }
            }

            // Notification button
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.testTag("notification_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge { Text("$unreadNotificationCount") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }

            // Quick Role Switcher Menu
            IconButton(onClick = { menuExpanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Role Switcher & Options")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                Text(
                    text = "Switch Role (Demo)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DropdownMenuItem(
                    text = { Text("Student (Rohan Sharma)") },
                    onClick = {
                        menuExpanded = false
                        onSwitchRole(UserRole.STUDENT)
                    },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Faculty (Dr. Priya Nair)") },
                    onClick = {
                        menuExpanded = false
                        onSwitchRole(UserRole.FACULTY)
                    },
                    leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Admin (Dean Academics)") },
                    onClick = {
                        menuExpanded = false
                        onSwitchRole(UserRole.ADMIN)
                    },
                    leadingIcon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                )
                androidx.compose.material3.HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        menuExpanded = false
                        onLogout()
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun StudentBottomNavBar(
    selectedTab: StudentTab,
    onTabSelected: (StudentTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == StudentTab.DASHBOARD,
            onClick = { onTabSelected(StudentTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text(StudentTab.DASHBOARD.title, fontSize = 11.sp) },
            modifier = Modifier.testTag("tab_student_dashboard")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.ATTENDANCE,
            onClick = { onTabSelected(StudentTab.ATTENDANCE) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Attendance") },
            label = { Text(StudentTab.ATTENDANCE.title, fontSize = 11.sp) },
            modifier = Modifier.testTag("tab_student_attendance")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.MARKS,
            onClick = { onTabSelected(StudentTab.MARKS) },
            icon = { Icon(Icons.Default.Grade, contentDescription = "Marks") },
            label = { Text(StudentTab.MARKS.title, fontSize = 11.sp) },
            modifier = Modifier.testTag("tab_student_marks")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.ASSIGNMENTS,
            onClick = { onTabSelected(StudentTab.ASSIGNMENTS) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = "Tasks") },
            label = { Text(StudentTab.ASSIGNMENTS.title, fontSize = 11.sp) },
            modifier = Modifier.testTag("tab_student_assignments")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.AI_ADVISOR,
            onClick = { onTabSelected(StudentTab.AI_ADVISOR) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Advisor") },
            label = { Text(StudentTab.AI_ADVISOR.title, fontSize = 11.sp) },
            modifier = Modifier.testTag("tab_student_ai")
        )
    }
}

@Composable
fun FacultyBottomNavBar(
    selectedTab: FacultyTab,
    onTabSelected: (FacultyTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == FacultyTab.DASHBOARD,
            onClick = { onTabSelected(FacultyTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
            label = { Text(FacultyTab.DASHBOARD.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == FacultyTab.ATTENDANCE,
            onClick = { onTabSelected(FacultyTab.ATTENDANCE) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Attendance Entry") },
            label = { Text(FacultyTab.ATTENDANCE.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == FacultyTab.MARKS,
            onClick = { onTabSelected(FacultyTab.MARKS) },
            icon = { Icon(Icons.Default.Grade, contentDescription = "Marks Entry") },
            label = { Text(FacultyTab.MARKS.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == FacultyTab.ANALYTICS,
            onClick = { onTabSelected(FacultyTab.ANALYTICS) },
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Class Analytics") },
            label = { Text(FacultyTab.ANALYTICS.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == FacultyTab.STUDENTS,
            onClick = { onTabSelected(FacultyTab.STUDENTS) },
            icon = { Icon(Icons.Default.People, contentDescription = "Students") },
            label = { Text(FacultyTab.STUDENTS.title, fontSize = 11.sp) }
        )
    }
}

@Composable
fun AdminBottomNavBar(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == AdminTab.DASHBOARD,
            onClick = { onTabSelected(AdminTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
            label = { Text(AdminTab.DASHBOARD.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.DEPARTMENTS,
            onClick = { onTabSelected(AdminTab.DEPARTMENTS) },
            icon = { Icon(Icons.Default.PieChart, contentDescription = "Departments") },
            label = { Text(AdminTab.DEPARTMENTS.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.MANAGEMENT,
            onClick = { onTabSelected(AdminTab.MANAGEMENT) },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Directory") },
            label = { Text(AdminTab.MANAGEMENT.title, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.REPORTS,
            onClick = { onTabSelected(AdminTab.REPORTS) },
            icon = { Icon(Icons.Default.Summarize, contentDescription = "Reports") },
            label = { Text(AdminTab.REPORTS.title, fontSize = 11.sp) }
        )
    }
}
