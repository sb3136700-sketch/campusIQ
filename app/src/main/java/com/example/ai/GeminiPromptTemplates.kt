package com.example.ai

object GeminiPromptTemplates {

    const val SYSTEM_INSTRUCTION_CAMPUS_IQ = """
You are CampusIQ AI, an expert academic advisor and educational analytics assistant for a premier college of engineering (AI & Machine Learning department).
Your role:
1. Provide constructive, positive-psychology academic guidance.
2. Never make absolute negative predictions such as "this student will fail". Focus on actionable recovery, time management, conceptual clarity, and resource recommendations.
3. Keep responses structured, concise, and mobile-friendly with bullet points.
4. If college rules or data are missing, state what is required rather than making assumptions.
"""

    fun buildStudentInsightPrompt(
        studentName: String,
        semester: Int,
        overallAttendance: Double,
        projectedSgpa: Double,
        strongSubjects: List<String>,
        improvementAreas: List<String>,
        pendingAssignmentsCount: Int
    ): String {
        return """
Student Academic Profile Analysis Request:
- Name: $studentName
- Current Semester: 6th Sem BE (AI & ML)
- Current Overall Attendance: $overallAttendance% (Minimum required: 75%)
- Projected SGPA: $projectedSgpa / 10.0
- Top Performing Subjects: ${strongSubjects.ifEmpty { listOf("Consistent across subjects") }.joinToString()}
- Improvement Opportunity Subjects: ${improvementAreas.ifEmpty { listOf("None currently flagged") }.joinToString()}
- Pending Assignments: $pendingAssignmentsCount

Please generate:
1. Brief Diagnostic Summary (2 sentences highlighting achievements and core growth area).
2. 3 Actionable, High-Impact Study Strategies for the improvement subjects.
3. Attendance & Schedule Recommendation to ensure university compliance.
Keep the response crisp, professional, and motivating.
"""
    }

    fun buildStudyPlanPrompt(
        studentName: String,
        subjectName: String,
        currentScorePercent: Double
    ): String {
        return """
Create a focused 5-day conceptual recovery study plan for student $studentName in the subject '$subjectName'.
Current performance level: $currentScorePercent%.
Format as:
Day 1: [Topic & 45-min active recall task]
Day 2: [Core algorithm / mathematical foundation & exercise]
Day 3: [Practical coding / laboratory simulation]
Day 4: [Previous year question paper analysis]
Day 5: [Self-assessment & doubt clarification with faculty]
Include one key motivational tip.
"""
    }

    fun buildFacultyDiagnosticPrompt(
        subjectName: String,
        enrolledCount: Int,
        classAverage: Double,
        attendanceAverage: Double,
        supportNeededCount: Int,
        passRate: Double
    ): String {
        return """
Generate an Executive Faculty Teaching & Learning Review for:
- Subject: $subjectName
- Total Enrolled: $enrolledCount
- Class Internal Marks Average: $classAverage%
- Class Average Attendance: $attendanceAverage%
- Students Identified for Proactive Support (<50% score): $supportNeededCount
- Projected Pass Rate: $passRate%

Provide:
1. Executive Health Summary of the class.
2. Key pedagogical recommendations to help the $supportNeededCount students needing support without disrupting high achievers.
3. One recommended active-learning intervention (e.g., peer programming, problem-solving clinics).
"""
    }

    fun buildAssistantPrompt(userQuery: String): String {
        return """
A user at CampusIQ asks: "$userQuery"

Answer using standard engineering college academic guidelines (VTU/AICTE model):
- 75% minimum attendance required per subject to be eligible for semester-end examinations.
- Internal Continuous Assessment (CIE) consists of 2 tests (30 marks each), assignments (20 marks), and lab practicals where applicable.
- Pass criteria requires minimum 40% in internal evaluation and 40% in semester-end exams.
- Provide a clear, helpful, factual response. If you do not have specific institutional details, advise consulting the Department Academic Coordinator or HOD.
"""
    }

    // Deterministic offline fallback insights so app always provides value
    fun getDeterministicStudentFallback(
        studentName: String,
        overallAttendance: Double,
        strongSubjects: List<String>,
        improvementAreas: List<String>
    ): String {
        val attendanceAdvice = if (overallAttendance < 75.0) {
            "Prioritize attending the next 4-6 upcoming lectures in flagged subjects to raise attendance above the mandatory 75% threshold."
        } else {
            "Attendance is in compliance ($overallAttendance%). Maintain this consistency through semester end."
        }

        val strongNote = if (strongSubjects.isNotEmpty()) {
            "Strong command demonstrated in: ${strongSubjects.joinToString()}. Consider sharing notes or mentoring peers."
        } else {
            "Steady performance maintained across curriculum subjects."
        }

        val improvementNote = if (improvementAreas.isNotEmpty()) {
            "Focused review advised for: ${improvementAreas.joinToString()}. Schedule 45-minute problem-solving sessions daily."
        } else {
            "No high-risk subject areas detected. Focus on preparing for end-semester examinations."
        }

        return """
Academic Summary for $studentName:
• $strongNote
• $improvementNote
• Compliance Note: $attendanceAdvice
• Recommended Action: Complete upcoming assignment deadlines and review faculty feedback on recent internal assessments.
""".trimIndent()
    }
}
