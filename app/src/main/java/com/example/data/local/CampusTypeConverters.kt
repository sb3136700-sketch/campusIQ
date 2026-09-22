package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AttendanceStatus
import com.example.data.model.ExamType
import com.example.data.model.NotificationType
import com.example.data.model.UserRole

class CampusTypeConverters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.STUDENT)

    @TypeConverter
    fun fromAttendanceStatus(value: AttendanceStatus): String = value.name

    @TypeConverter
    fun toAttendanceStatus(value: String): AttendanceStatus = runCatching { AttendanceStatus.valueOf(value) }.getOrDefault(AttendanceStatus.PRESENT)

    @TypeConverter
    fun fromExamType(value: ExamType): String = value.name

    @TypeConverter
    fun toExamType(value: String): ExamType = runCatching { ExamType.valueOf(value) }.getOrDefault(ExamType.INTERNAL_1)

    @TypeConverter
    fun fromNotificationType(value: NotificationType): String = value.name

    @TypeConverter
    fun toNotificationType(value: String): NotificationType = runCatching { NotificationType.valueOf(value) }.getOrDefault(NotificationType.ANNOUNCEMENT)
}
