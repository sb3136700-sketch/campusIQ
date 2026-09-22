package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AttendanceStatus
import com.example.data.model.ExamType
import com.example.data.model.NotificationType
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val department: String,
    val refId: String // studentId or facultyId or adminId
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val studentId: String, // e.g. "1MS21AI042"
    val name: String,
    val email: String,
    val department: String,
    val semester: Int,
    val section: String,
    val batchYear: String,
    val phone: String,
    val cgpa: Double = 0.0
)

@Entity(tableName = "faculty")
data class FacultyEntity(
    @PrimaryKey val facultyId: String, // e.g. "FAC_AIML_01"
    val name: String,
    val email: String,
    val department: String,
    val designation: String,
    val cabin: String,
    val phone: String
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val subjectCode: String, // e.g. "21AI61"
    val name: String,
    val department: String,
    val semester: Int,
    val credits: Int,
    val facultyId: String
)

@Entity(tableName = "attendance")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val subjectCode: String,
    val date: String, // YYYY-MM-DD
    val status: AttendanceStatus,
    val topicCovered: String
)

@Entity(tableName = "marks")
data class MarkRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val subjectCode: String,
    val examType: ExamType,
    val score: Double,
    val maxScore: Double,
    val remarks: String = ""
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val maxMarks: Int,
    val isSubmitted: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientRole: UserRole,
    val recipientId: String = "", // empty for role-wide
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "ai_insights")
data class AIInsightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val title: String,
    val summary: String,
    val recommendations: String, // comma or newline separated
    val timestamp: Long = System.currentTimeMillis()
)
