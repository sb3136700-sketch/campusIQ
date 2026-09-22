package com.example.data.repository

import com.example.data.local.CampusIqDatabase
import com.example.data.local.entity.AIInsightEntity
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.MarkRecordEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CampusRepository(private val db: CampusIqDatabase) {

    val allStudents: Flow<List<StudentEntity>> = db.studentDao().getAllStudents()
    val allSubjects: Flow<List<SubjectEntity>> = db.subjectDao().getAllSubjects()
    val allFaculty: Flow<List<FacultyEntity>> = db.facultyDao().getAllFaculty()
    val allAssignments: Flow<List<AssignmentEntity>> = db.assignmentDao().getAllAssignments()
    val allAttendance: Flow<List<AttendanceRecordEntity>> = db.attendanceDao().getAllAttendance()
    val allMarks: Flow<List<MarkRecordEntity>> = db.marksDao().getAllMarks()

    fun getStudentAttendance(studentId: String): Flow<List<AttendanceRecordEntity>> {
        return db.attendanceDao().getAttendanceForStudent(studentId)
    }

    fun getStudentMarks(studentId: String): Flow<List<MarkRecordEntity>> {
        return db.marksDao().getMarksForStudent(studentId)
    }

    fun getStudentInsights(studentId: String): Flow<List<AIInsightEntity>> {
        return db.aiInsightDao().getInsightsForStudent(studentId)
    }

    fun getNotificationsForRole(role: UserRole): Flow<List<NotificationEntity>> {
        return db.notificationDao().getNotificationsForRole(role)
    }

    suspend fun getStudentById(studentId: String): StudentEntity? = withContext(Dispatchers.IO) {
        db.studentDao().getStudentById(studentId)
    }

    suspend fun recordAttendance(record: AttendanceRecordEntity) = withContext(Dispatchers.IO) {
        db.attendanceDao().insertAttendance(record)
    }

    suspend fun recordAttendanceBatch(records: List<AttendanceRecordEntity>) = withContext(Dispatchers.IO) {
        db.attendanceDao().insertAttendanceList(records)
    }

    suspend fun saveMark(mark: MarkRecordEntity) = withContext(Dispatchers.IO) {
        db.marksDao().insertMark(mark)
    }

    suspend fun toggleAssignmentStatus(id: Long, isSubmitted: Boolean) = withContext(Dispatchers.IO) {
        db.assignmentDao().updateSubmissionStatus(id, isSubmitted)
    }

    suspend fun addAssignment(assignment: AssignmentEntity) = withContext(Dispatchers.IO) {
        db.assignmentDao().insertAssignment(assignment)
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        db.notificationDao().markAsRead(id)
    }

    suspend fun addNotification(notification: NotificationEntity) = withContext(Dispatchers.IO) {
        db.notificationDao().insertNotification(notification)
    }

    suspend fun saveInsight(insight: AIInsightEntity) = withContext(Dispatchers.IO) {
        db.aiInsightDao().insertInsight(insight)
    }

    suspend fun addStudent(student: StudentEntity) = withContext(Dispatchers.IO) {
        db.studentDao().insertStudent(student)
    }

    suspend fun addSubject(subject: SubjectEntity) = withContext(Dispatchers.IO) {
        db.subjectDao().insertSubject(subject)
    }
}
