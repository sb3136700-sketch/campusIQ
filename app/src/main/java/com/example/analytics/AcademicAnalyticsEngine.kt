package com.example.analytics

import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.MarkRecordEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.AcademicRiskLevel
import com.example.data.model.AttendanceStatus
import com.example.data.model.ExamType
import kotlin.math.roundToInt

data class SubjectAttendanceStat(
    val subjectCode: String,
    val subjectName: String,
    val totalSessions: Int,
    val attendedSessions: Int,
    val absentSessions: Int,
    val percentage: Double,
    val isBelowThreshold: Boolean,
    val sessionsNeededFor75: Int
)

data class SubjectMarksStat(
    val subjectCode: String,
    val subjectName: String,
    val internalAveragePercent: Double,
    val assignmentScore: Double,
    val assignmentMax: Double,
    val labScore: Double?,
    val labMax: Double?,
    val overallPercentage: Double,
    val grade: String,
    val gradePoints: Double,
    val credits: Int
)

data class StudentAcademicSummary(
    val studentId: String,
    val overallAttendancePercent: Double,
    val totalAttended: Int,
    val totalSessions: Int,
    val subjectAttendance: List<SubjectAttendanceStat>,
    val subjectMarks: List<SubjectMarksStat>,
    val projectedSgpa: Double,
    val strongSubjects: List<String>,
    val improvementAreas: List<String>,
    val riskLevel: AcademicRiskLevel,
    val riskFactors: List<String>
)

data class ClassAnalyticsSummary(
    val subjectCode: String,
    val subjectName: String,
    val enrolledStudentsCount: Int,
    val classAverageMarksPercent: Double,
    val classAverageAttendancePercent: Double,
    val highestMarksPercent: Double,
    val lowestMarksPercent: Double,
    val studentsRequiringSupportCount: Int,
    val passRatePercentage: Double
)

data class DepartmentMetric(
    val departmentName: String,
    val totalStudents: Int,
    val averageAttendance: Double,
    val averageCgpa: Double,
    val facultyCount: Int
)

object AcademicAnalyticsEngine {

    const val MINIMUM_ATTENDANCE_THRESHOLD = 75.0 // University norm

    fun calculateSubjectAttendance(
        subjectCode: String,
        subjectName: String,
        records: List<AttendanceRecordEntity>
    ): SubjectAttendanceStat {
        val subjectRecords = records.filter { it.subjectCode == subjectCode }
        val total = subjectRecords.size
        val attended = subjectRecords.count { it.status == AttendanceStatus.PRESENT }
        val absent = subjectRecords.count { it.status == AttendanceStatus.ABSENT }

        val percentage = if (total > 0) (attended.toDouble() / total) * 100.0 else 100.0
        val roundedPercentage = (percentage * 10).roundToInt() / 10.0
        val isBelow = roundedPercentage < MINIMUM_ATTENDANCE_THRESHOLD

        // Calculate sessions needed to reach 75%
        // (attended + x) / (total + x) >= 0.75
        // attended + x >= 0.75 * total + 0.75 * x
        // 0.25 * x >= 0.75 * total - attended
        // x >= (0.75 * total - attended) / 0.25
        val needed = if (isBelow && total > 0) {
            val rawNeeded = (0.75 * total - attended) / 0.25
            maxOf(1, rawNeeded.roundToInt())
        } else {
            0
        }

        return SubjectAttendanceStat(
            subjectCode = subjectCode,
            subjectName = subjectName,
            totalSessions = total,
            attendedSessions = attended,
            absentSessions = absent,
            percentage = roundedPercentage,
            isBelowThreshold = isBelow,
            sessionsNeededFor75 = needed
        )
    }

    fun calculateSubjectMarks(
        subject: SubjectEntity,
        records: List<MarkRecordEntity>
    ): SubjectMarksStat {
        val marks = records.filter { it.subjectCode == subject.subjectCode }

        val internals = marks.filter { it.examType == ExamType.INTERNAL_1 || it.examType == ExamType.INTERNAL_2 }
        val internalPct = if (internals.isNotEmpty()) {
            val totalScore = internals.sumOf { it.score }
            val totalMax = internals.sumOf { it.maxScore }
            if (totalMax > 0) (totalScore / totalMax) * 100.0 else 0.0
        } else {
            0.0
        }

        val assignment = marks.firstOrNull { it.examType == ExamType.ASSIGNMENT }
        val assignmentScore = assignment?.score ?: 0.0
        val assignmentMax = assignment?.maxScore ?: 20.0

        val lab = marks.firstOrNull { it.examType == ExamType.LAB_PRACTICAL }
        val labScore = lab?.score
        val labMax = lab?.maxScore

        // Combined estimated percentage
        val overallPercentage = if (marks.isNotEmpty()) {
            val totalScore = marks.sumOf { it.score }
            val totalMax = marks.sumOf { it.maxScore }
            if (totalMax > 0) (totalScore / totalMax) * 100.0 else 0.0
        } else {
            0.0
        }

        val roundedOverall = (overallPercentage * 10).roundToInt() / 10.0
        val (grade, points) = calculateGradeAndPoints(roundedOverall)

        return SubjectMarksStat(
            subjectCode = subject.subjectCode,
            subjectName = subject.name,
            internalAveragePercent = (internalPct * 10).roundToInt() / 10.0,
            assignmentScore = assignmentScore,
            assignmentMax = assignmentMax,
            labScore = labScore,
            labMax = labMax,
            overallPercentage = roundedOverall,
            grade = grade,
            gradePoints = points,
            credits = subject.credits
        )
    }

    private fun calculateGradeAndPoints(percentage: Double): Pair<String, Double> {
        return when {
            percentage >= 90.0 -> "O" to 10.0
            percentage >= 80.0 -> "A+" to 9.0
            percentage >= 70.0 -> "A" to 8.0
            percentage >= 60.0 -> "B+" to 7.0
            percentage >= 50.0 -> "B" to 6.0
            percentage >= 40.0 -> "C" to 5.0
            percentage > 0.0 -> "P" to 4.0
            else -> "N/A" to 0.0
        }
    }

    fun buildStudentSummary(
        studentId: String,
        subjects: List<SubjectEntity>,
        attendanceRecords: List<AttendanceRecordEntity>,
        markRecords: List<MarkRecordEntity>
    ): StudentAcademicSummary {
        val studentAttendance = attendanceRecords.filter { it.studentId == studentId }
        val studentMarks = markRecords.filter { it.studentId == studentId }

        val subjectAttendanceList = subjects.map { subject ->
            calculateSubjectAttendance(subject.subjectCode, subject.name, studentAttendance)
        }

        val subjectMarksList = subjects.map { subject ->
            calculateSubjectMarks(subject, studentMarks)
        }

        val totalSessions = subjectAttendanceList.sumOf { it.totalSessions }
        val totalAttended = subjectAttendanceList.sumOf { it.attendedSessions }
        val overallAttendancePercent = if (totalSessions > 0) {
            ((totalAttended.toDouble() / totalSessions) * 1000).roundToInt() / 10.0
        } else {
            100.0
        }

        // Weighted SGPA calculation
        var totalWeightedPoints = 0.0
        var totalCredits = 0
        subjectMarksList.forEach { stat ->
            if (stat.gradePoints > 0) {
                totalWeightedPoints += stat.gradePoints * stat.credits
                totalCredits += stat.credits
            }
        }
        val projectedSgpa = if (totalCredits > 0) {
            ((totalWeightedPoints / totalCredits) * 100).roundToInt() / 100.0
        } else {
            0.0
        }

        // Strong vs weak subjects
        val sortedByScore = subjectMarksList.filter { it.overallPercentage > 0 }.sortedByDescending { it.overallPercentage }
        val strongSubjects = sortedByScore.filter { it.overallPercentage >= 80.0 }.map { "${it.subjectName} (${it.overallPercentage}%)" }
        val improvementAreas = sortedByScore.filter { it.overallPercentage < 70.0 }.map { "${it.subjectName} (${it.overallPercentage}%)" }

        // Risk factor analysis (Non-judgmental, constructive indicators)
        val riskFactors = mutableListOf<String>()
        var isHighRisk = false
        var isModerateRisk = false

        if (overallAttendancePercent < MINIMUM_ATTENDANCE_THRESHOLD) {
            riskFactors.add("Overall attendance ($overallAttendancePercent%) is below 75% regulatory requirement")
            isHighRisk = true
        }

        subjectAttendanceList.filter { it.isBelowThreshold }.forEach {
            riskFactors.add("Low attendance in ${it.subjectName} (${it.percentage}%). Needs ${it.sessionsNeededFor75} more sessions")
            isModerateRisk = true
        }

        subjectMarksList.filter { it.overallPercentage > 0 && it.overallPercentage < 50.0 }.forEach {
            riskFactors.add("Internal assessment score in ${it.subjectName} is below 50%")
            isHighRisk = true
        }

        val riskLevel = when {
            isHighRisk -> AcademicRiskLevel.HIGH_RISK
            isModerateRisk -> AcademicRiskLevel.MODERATE_RISK
            else -> AcademicRiskLevel.NORMAL
        }

        return StudentAcademicSummary(
            studentId = studentId,
            overallAttendancePercent = overallAttendancePercent,
            totalAttended = totalAttended,
            totalSessions = totalSessions,
            subjectAttendance = subjectAttendanceList,
            subjectMarks = subjectMarksList,
            projectedSgpa = projectedSgpa,
            strongSubjects = strongSubjects,
            improvementAreas = improvementAreas,
            riskLevel = riskLevel,
            riskFactors = riskFactors
        )
    }

    fun calculateClassAnalytics(
        subject: SubjectEntity,
        studentsCount: Int,
        allAttendance: List<AttendanceRecordEntity>,
        allMarks: List<MarkRecordEntity>
    ): ClassAnalyticsSummary {
        val subjectMarks = allMarks.filter { it.subjectCode == subject.subjectCode }
        val subjectAttendance = allAttendance.filter { it.subjectCode == subject.subjectCode }

        val studentMarksGrouped = subjectMarks.groupBy { it.studentId }
        val studentAverages = studentMarksGrouped.map { (_, marks) ->
            val totalScore = marks.sumOf { it.score }
            val totalMax = marks.sumOf { it.maxScore }
            if (totalMax > 0) (totalScore / totalMax) * 100.0 else 0.0
        }

        val classAvgMarks = if (studentAverages.isNotEmpty()) {
            ((studentAverages.average()) * 10).roundToInt() / 10.0
        } else {
            0.0
        }

        val highestMarks = if (studentAverages.isNotEmpty()) {
            ((studentAverages.maxOrNull() ?: 0.0) * 10).roundToInt() / 10.0
        } else {
            0.0
        }

        val lowestMarks = if (studentAverages.isNotEmpty()) {
            ((studentAverages.minOrNull() ?: 0.0) * 10).roundToInt() / 10.0
        } else {
            0.0
        }

        val totalAttCount = subjectAttendance.count { it.status == AttendanceStatus.PRESENT }
        val totalSessions = subjectAttendance.size
        val classAvgAttendance = if (totalSessions > 0) {
            (((totalAttCount.toDouble() / totalSessions) * 100.0) * 10).roundToInt() / 10.0
        } else {
            100.0
        }

        val supportCount = studentAverages.count { it < 50.0 }
        val passCount = studentAverages.count { it >= 40.0 }
        val passRate = if (studentAverages.isNotEmpty()) {
            (((passCount.toDouble() / studentAverages.size) * 100.0) * 10).roundToInt() / 10.0
        } else {
            100.0
        }

        return ClassAnalyticsSummary(
            subjectCode = subject.subjectCode,
            subjectName = subject.name,
            enrolledStudentsCount = studentsCount,
            classAverageMarksPercent = classAvgMarks,
            classAverageAttendancePercent = classAvgAttendance,
            highestMarksPercent = highestMarks,
            lowestMarksPercent = lowestMarks,
            studentsRequiringSupportCount = supportCount,
            passRatePercentage = passRate
        )
    }
}
