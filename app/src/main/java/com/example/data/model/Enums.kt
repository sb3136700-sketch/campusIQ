package com.example.data.model

enum class UserRole {
    STUDENT,
    FACULTY,
    ADMIN
}

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    EXCUSED
}

enum class ExamType {
    INTERNAL_1,
    INTERNAL_2,
    ASSIGNMENT,
    LAB_PRACTICAL,
    SEMESTER_END
}

enum class NotificationType {
    ATTENDANCE_WARNING,
    MARKS_RELEASED,
    ANNOUNCEMENT,
    AI_INSIGHT,
    ACADEMIC_ALERT
}

enum class AcademicRiskLevel {
    NORMAL,
    MODERATE_RISK,
    HIGH_RISK
}
