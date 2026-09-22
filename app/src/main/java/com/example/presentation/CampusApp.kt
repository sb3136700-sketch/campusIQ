package com.example.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.presentation.components.AdminBottomNavBar
import com.example.presentation.components.CampusTopAppBar
import com.example.presentation.components.FacultyBottomNavBar
import com.example.presentation.components.NotificationsBottomSheet
import com.example.presentation.components.StudentBottomNavBar
import com.example.presentation.navigation.AdminTab
import com.example.presentation.navigation.FacultyTab
import com.example.presentation.navigation.StudentTab
import com.example.presentation.screens.admin.AdminDashboardScreen
import com.example.presentation.screens.admin.AdminDepartmentsScreen
import com.example.presentation.screens.admin.AdminManagementScreen
import com.example.presentation.screens.admin.AdminReportsScreen
import com.example.presentation.screens.auth.LoginScreen
import com.example.presentation.screens.faculty.FacultyAttendanceEntryScreen
import com.example.presentation.screens.faculty.FacultyClassAnalyticsScreen
import com.example.presentation.screens.faculty.FacultyDashboardScreen
import com.example.presentation.screens.faculty.FacultyMarksEntryScreen
import com.example.presentation.screens.student.StudentAiAdvisorScreen
import com.example.presentation.screens.student.StudentAssignmentsScreen
import com.example.presentation.screens.student.StudentAttendanceScreen
import com.example.presentation.screens.student.StudentDashboardScreen
import com.example.presentation.screens.student.StudentMarksScreen
import com.example.presentation.viewmodel.AiViewModel
import com.example.presentation.viewmodel.AuthViewModel
import com.example.presentation.viewmodel.CampusViewModel

@Composable
fun CampusApp(
    authViewModel: AuthViewModel,
    campusViewModel: CampusViewModel,
    aiViewModel: AiViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val authLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val authError by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val authInfo by authViewModel.infoMessage.collectAsStateWithLifecycle()

    val students by campusViewModel.allStudents.collectAsStateWithLifecycle()
    val subjects by campusViewModel.allSubjects.collectAsStateWithLifecycle()
    val faculty by campusViewModel.allFaculty.collectAsStateWithLifecycle()
    val assignments by campusViewModel.allAssignments.collectAsStateWithLifecycle()
    val attendance by campusViewModel.allAttendance.collectAsStateWithLifecycle()
    val marks by campusViewModel.allMarks.collectAsStateWithLifecycle()
    val successMessage by campusViewModel.successMessage.collectAsStateWithLifecycle()

    val studentSummary by campusViewModel.currentStudentSummary.collectAsStateWithLifecycle()
    val currentStudentId by campusViewModel.currentStudentId.collectAsStateWithLifecycle()

    val isGeneratingAi by aiViewModel.isGenerating.collectAsStateWithLifecycle()
    val aiInsight by aiViewModel.studentInsight.collectAsStateWithLifecycle()
    val studyPlan by aiViewModel.generatedStudyPlan.collectAsStateWithLifecycle()
    val facultyDiagnostic by aiViewModel.facultyDiagnostic.collectAsStateWithLifecycle()
    val chatMessages by aiViewModel.chatMessages.collectAsStateWithLifecycle()

    var studentTab by remember { mutableStateOf(StudentTab.DASHBOARD) }
    var facultyTab by remember { mutableStateOf(FacultyTab.DASHBOARD) }
    var adminTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    var showNotificationsSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            campusViewModel.clearSuccessMessage()
        }
    }

    if (currentUser == null) {
        LoginScreen(
            authViewModel = authViewModel,
            isLoading = authLoading,
            errorMessage = authError,
            infoMessage = authInfo,
            onLoginSuccess = {}
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CampusTopAppBar(
                    currentUser = currentUser,
                    onSwitchRole = { role -> authViewModel.quickLoginAs(role) },
                    onLogout = { authViewModel.logout() },
                    onNotificationClick = { showNotificationsSheet = true },
                    unreadNotificationCount = 2
                )
            },
            bottomBar = {
                when (currentUser?.role) {
                    UserRole.STUDENT -> StudentBottomNavBar(
                        selectedTab = studentTab,
                        onTabSelected = { studentTab = it }
                    )
                    UserRole.FACULTY -> FacultyBottomNavBar(
                        selectedTab = facultyTab,
                        onTabSelected = { facultyTab = it }
                    )
                    UserRole.ADMIN -> AdminBottomNavBar(
                        selectedTab = adminTab,
                        onTabSelected = { adminTab = it }
                    )
                    null -> {}
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentUser?.role) {
                    UserRole.STUDENT -> {
                        val currentStudent = students.firstOrNull { it.studentId == currentStudentId } ?: students.firstOrNull()

                        Crossfade(targetState = studentTab, label = "StudentTabTransition") { tab ->
                            when (tab) {
                                StudentTab.DASHBOARD -> StudentDashboardScreen(
                                    student = currentStudent,
                                    summary = studentSummary,
                                    assignments = assignments,
                                    onNavigateTab = { studentTab = it }
                                )
                                StudentTab.ATTENDANCE -> StudentAttendanceScreen(
                                    summary = studentSummary,
                                    allRecords = attendance,
                                    studentId = currentStudentId
                                )
                                StudentTab.MARKS -> StudentMarksScreen(
                                    summary = studentSummary,
                                    allMarks = marks,
                                    studentId = currentStudentId
                                )
                                StudentTab.ASSIGNMENTS -> StudentAssignmentsScreen(
                                    assignments = assignments,
                                    onToggleSubmission = { id, submitted ->
                                        campusViewModel.toggleAssignment(id, submitted)
                                    }
                                )
                                StudentTab.AI_ADVISOR -> StudentAiAdvisorScreen(
                                    student = currentStudent,
                                    summary = studentSummary,
                                    pendingAssignmentsCount = assignments.count { !it.isSubmitted },
                                    aiViewModel = aiViewModel,
                                    isGenerating = isGeneratingAi,
                                    insight = aiInsight,
                                    studyPlan = studyPlan,
                                    chatMessages = chatMessages
                                )
                            }
                        }
                    }

                    UserRole.FACULTY -> {
                        val currentFaculty = faculty.firstOrNull()

                        Crossfade(targetState = facultyTab, label = "FacultyTabTransition") { tab ->
                            when (tab) {
                                FacultyTab.DASHBOARD -> FacultyDashboardScreen(
                                    faculty = currentFaculty,
                                    students = students,
                                    subjects = subjects,
                                    onNavigateTab = { facultyTab = it }
                                )
                                FacultyTab.ATTENDANCE -> FacultyAttendanceEntryScreen(
                                    subjects = subjects,
                                    students = students,
                                    onSaveBatch = { code, date, topic, statuses ->
                                        campusViewModel.recordBatchAttendance(code, date, topic, statuses)
                                    }
                                )
                                FacultyTab.MARKS -> FacultyMarksEntryScreen(
                                    subjects = subjects,
                                    students = students,
                                    onSaveMark = { studentId, code, type, score, maxScore, remarks ->
                                        campusViewModel.enterMark(studentId, code, type, score, maxScore, remarks)
                                    }
                                )
                                FacultyTab.ANALYTICS -> FacultyClassAnalyticsScreen(
                                    subjects = subjects,
                                    students = students,
                                    getClassAnalytics = { code -> campusViewModel.getClassAnalytics(code) },
                                    aiViewModel = aiViewModel,
                                    isGeneratingAi = isGeneratingAi,
                                    facultyDiagnostic = facultyDiagnostic
                                )
                                FacultyTab.STUDENTS -> {
                                    // Display Students with their analytics
                                    StudentAttendanceScreen(
                                        summary = studentSummary,
                                        allRecords = attendance,
                                        studentId = currentStudentId
                                    )
                                }
                            }
                        }
                    }

                    UserRole.ADMIN -> {
                        Crossfade(targetState = adminTab, label = "AdminTabTransition") { tab ->
                            when (tab) {
                                AdminTab.DASHBOARD -> AdminDashboardScreen(
                                    students = students,
                                    faculty = faculty,
                                    departmentMetrics = campusViewModel.getDepartmentMetrics(),
                                    onBroadcastAnnouncement = { title, msg, role ->
                                        campusViewModel.broadcastAnnouncement(title, msg, role)
                                    }
                                )
                                AdminTab.DEPARTMENTS -> AdminDepartmentsScreen(
                                    departmentMetrics = campusViewModel.getDepartmentMetrics()
                                )
                                AdminTab.MANAGEMENT -> AdminManagementScreen(
                                    students = students,
                                    faculty = faculty,
                                    onAddStudent = { campusViewModel.addStudent(it) }
                                )
                                AdminTab.REPORTS -> AdminReportsScreen(
                                    students = students
                                )
                            }
                        }
                    }

                    null -> {}
                }
            }
        }

        if (showNotificationsSheet) {
            NotificationsBottomSheet(
                notifications = emptyList(), // Can connect to repository notification flow if needed
                onDismiss = { showNotificationsSheet = false },
                onMarkAsRead = {}
            )
        }
    }
}
