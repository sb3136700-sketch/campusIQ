package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.AcademicAnalyticsEngine
import com.example.analytics.ClassAnalyticsSummary
import com.example.analytics.DepartmentMetric
import com.example.analytics.StudentAcademicSummary
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.MarkRecordEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.ExamType
import com.example.data.model.NotificationType
import com.example.data.model.UserRole
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CampusViewModel(private val repository: CampusRepository) : ViewModel() {

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFaculty: StateFlow<List<FacultyEntity>> = repository.allFaculty
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<AttendanceRecordEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMarks: StateFlow<List<MarkRecordEntity>> = repository.allMarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentStudentId = MutableStateFlow("1MS21AI042")
    val currentStudentId: StateFlow<String> = _currentStudentId.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun setCurrentStudentId(studentId: String) {
        _currentStudentId.value = studentId
    }

    // Reactive computation of current student's academic summary
    val currentStudentSummary: StateFlow<StudentAcademicSummary?> = combine(
        _currentStudentId,
        allSubjects,
        allAttendance,
        allMarks
    ) { studentId, subjects, attendance, marks ->
        if (subjects.isEmpty()) null
        else AcademicAnalyticsEngine.buildStudentSummary(studentId, subjects, attendance, marks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getStudentSummaryById(studentId: String): StudentAcademicSummary? {
        val subjects = allSubjects.value
        if (subjects.isEmpty()) return null
        return AcademicAnalyticsEngine.buildStudentSummary(
            studentId = studentId,
            subjects = subjects,
            attendanceRecords = allAttendance.value,
            markRecords = allMarks.value
        )
    }

    fun getClassAnalytics(subjectCode: String): ClassAnalyticsSummary? {
        val subject = allSubjects.value.firstOrNull { it.subjectCode == subjectCode } ?: return null
        val studentsCount = allStudents.value.size
        return AcademicAnalyticsEngine.calculateClassAnalytics(
            subject = subject,
            studentsCount = studentsCount,
            allAttendance = allAttendance.value,
            allMarks = allMarks.value
        )
    }

    fun getDepartmentMetrics(): List<DepartmentMetric> {
        val students = allStudents.value
        val faculty = allFaculty.value
        val attendance = allAttendance.value
        val totalSessions = attendance.size
        val attendedSessions = attendance.count { it.status == AttendanceStatus.PRESENT }
        val avgAttendance = if (totalSessions > 0) (attendedSessions.toDouble() / totalSessions) * 100.0 else 85.0

        val aimlStudents = students.filter { it.department == "AI & ML" }
        val aimlCgpa = if (aimlStudents.isNotEmpty()) aimlStudents.map { it.cgpa }.average() else 8.5

        return listOf(
            DepartmentMetric("Artificial Intelligence & ML", aimlStudents.size, avgAttendance, (aimlCgpa * 10).toInt() / 10.0, faculty.size),
            DepartmentMetric("Computer Science & Eng", 120, 84.5, 8.4, 18),
            DepartmentMetric("Information Science & Eng", 95, 81.2, 8.1, 14),
            DepartmentMetric("Electronics & Comm Eng", 110, 83.0, 7.9, 16)
        )
    }

    fun recordAttendance(
        studentId: String,
        subjectCode: String,
        date: String,
        status: AttendanceStatus,
        topic: String
    ) {
        viewModelScope.launch {
            repository.recordAttendance(
                AttendanceRecordEntity(
                    studentId = studentId,
                    subjectCode = subjectCode,
                    date = date,
                    status = status,
                    topicCovered = topic
                )
            )
            _successMessage.value = "Attendance recorded successfully."
        }
    }

    fun recordBatchAttendance(
        subjectCode: String,
        date: String,
        topic: String,
        studentStatuses: Map<String, AttendanceStatus>
    ) {
        viewModelScope.launch {
            val records = studentStatuses.map { (studentId, status) ->
                AttendanceRecordEntity(
                    studentId = studentId,
                    subjectCode = subjectCode,
                    date = date,
                    status = status,
                    topicCovered = topic
                )
            }
            repository.recordAttendanceBatch(records)
            _successMessage.value = "Class attendance saved for ${records.size} students."
        }
    }

    fun enterMark(
        studentId: String,
        subjectCode: String,
        examType: ExamType,
        score: Double,
        maxScore: Double,
        remarks: String
    ) {
        viewModelScope.launch {
            repository.saveMark(
                MarkRecordEntity(
                    studentId = studentId,
                    subjectCode = subjectCode,
                    examType = examType,
                    score = score,
                    maxScore = maxScore,
                    remarks = remarks
                )
            )
            _successMessage.value = "Marks recorded successfully for $studentId."
        }
    }

    fun toggleAssignment(assignmentId: Long, isSubmitted: Boolean) {
        viewModelScope.launch {
            repository.toggleAssignmentStatus(assignmentId, isSubmitted)
            _successMessage.value = if (isSubmitted) "Assignment marked as submitted!" else "Assignment marked as pending."
        }
    }

    fun createAssignment(
        subjectCode: String,
        title: String,
        description: String,
        dueDate: String,
        maxMarks: Int
    ) {
        viewModelScope.launch {
            repository.addAssignment(
                AssignmentEntity(
                    subjectCode = subjectCode,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    maxMarks = maxMarks,
                    isSubmitted = false
                )
            )
            _successMessage.value = "Assignment '$title' created successfully."
        }
    }

    fun addStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.addStudent(student)
            _successMessage.value = "Student ${student.name} enrolled successfully."
        }
    }

    fun addSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.addSubject(subject)
            _successMessage.value = "Subject ${subject.name} created."
        }
    }

    fun broadcastAnnouncement(title: String, message: String, targetRole: UserRole) {
        viewModelScope.launch {
            repository.addNotification(
                NotificationEntity(
                    recipientRole = targetRole,
                    title = title,
                    message = message,
                    type = NotificationType.ANNOUNCEMENT
                )
            )
            _successMessage.value = "Announcement published to ${targetRole.name}."
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
