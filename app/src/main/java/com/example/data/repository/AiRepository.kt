package com.example.data.repository

import com.example.ai.GeminiContent
import com.example.ai.GeminiGenerationConfig
import com.example.ai.GeminiNetworkClient
import com.example.ai.GeminiPart
import com.example.ai.GeminiPromptTemplates
import com.example.ai.GeminiRequest
import com.example.ai.GeminiResponseParser
import com.example.analytics.ClassAnalyticsSummary
import com.example.analytics.StudentAcademicSummary
import com.example.data.local.entity.StudentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiRepository {

    suspend fun generateStudentAcademicInsight(
        student: StudentEntity,
        summary: StudentAcademicSummary,
        pendingAssignments: Int
    ): String = withContext(Dispatchers.IO) {
        val fallback = GeminiPromptTemplates.getDeterministicStudentFallback(
            studentName = student.name,
            overallAttendance = summary.overallAttendancePercent,
            strongSubjects = summary.strongSubjects,
            improvementAreas = summary.improvementAreas
        )

        if (!GeminiNetworkClient.isApiKeyConfigured()) {
            return@withContext fallback
        }

        try {
            val promptText = GeminiPromptTemplates.buildStudentInsightPrompt(
                studentName = student.name,
                semester = student.semester,
                overallAttendance = summary.overallAttendancePercent,
                projectedSgpa = summary.projectedSgpa,
                strongSubjects = summary.strongSubjects,
                improvementAreas = summary.improvementAreas,
                pendingAssignmentsCount = pendingAssignments
            )

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = promptText))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.6f, maxOutputTokens = 1000),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = GeminiPromptTemplates.SYSTEM_INSTRUCTION_CAMPUS_IQ))
                )
            )

            val response = GeminiNetworkClient.apiService.generateContent(
                apiKey = GeminiNetworkClient.getApiKey(),
                request = request
            )

            GeminiResponseParser.extractTextFromResponse(response, fallback)
        } catch (e: Exception) {
            // Fail gracefully to deterministic academic feedback
            fallback
        }
    }

    suspend fun generateStudyPlan(
        studentName: String,
        subjectName: String,
        currentScore: Double
    ): String = withContext(Dispatchers.IO) {
        val fallback = """
5-Day Study Plan for $subjectName:
• Day 1: Foundational Review — Read lecture slides and summarize key definitions (45 mins).
• Day 2: Algorithmic Mechanics — Work out 3 sample numerical problems or architecture flowcharts.
• Day 3: Practical Application — Run code examples in Jupyter Notebook / PyTorch.
• Day 4: Past Exam Papers — Solve 2 questions from previous semester CIE assessments.
• Day 5: Self-Quiz & Review — Clarify remaining doubts during faculty office hours.
Motivational Tip: Consistent 45-minute daily intervals yield better retention than last-minute cramming!
""".trimIndent()

        if (!GeminiNetworkClient.isApiKeyConfigured()) {
            return@withContext fallback
        }

        try {
            val prompt = GeminiPromptTemplates.buildStudyPlanPrompt(studentName, subjectName, currentScore)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.6f, maxOutputTokens = 900),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = GeminiPromptTemplates.SYSTEM_INSTRUCTION_CAMPUS_IQ))
                )
            )
            val response = GeminiNetworkClient.apiService.generateContent(
                apiKey = GeminiNetworkClient.getApiKey(),
                request = request
            )
            GeminiResponseParser.extractTextFromResponse(response, fallback)
        } catch (e: Exception) {
            fallback
        }
    }

    suspend fun generateFacultyClassDiagnostic(
        summary: ClassAnalyticsSummary
    ): String = withContext(Dispatchers.IO) {
        val fallback = """
Faculty Class Diagnostic for ${summary.subjectName}:
• Performance Overview: Class marks average stands at ${summary.classAverageMarksPercent}%, with an average attendance rate of ${summary.classAverageAttendancePercent}%.
• Support Priority: ${summary.studentsRequiringSupportCount} student(s) currently scoring below 50% in continuous evaluations.
• Recommended Action: Schedule a 30-minute concept clarification clinic covering core Unit 2 topics before the next CIE test.
• Engagement Strategy: Introduce active peer-pair coding sessions during upcoming lab hours to reinforce theoretical principles.
""".trimIndent()

        if (!GeminiNetworkClient.isApiKeyConfigured()) {
            return@withContext fallback
        }

        try {
            val prompt = GeminiPromptTemplates.buildFacultyDiagnosticPrompt(
                subjectName = summary.subjectName,
                enrolledCount = summary.enrolledStudentsCount,
                classAverage = summary.classAverageMarksPercent,
                attendanceAverage = summary.classAverageAttendancePercent,
                supportNeededCount = summary.studentsRequiringSupportCount,
                passRate = summary.passRatePercentage
            )
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 900),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = GeminiPromptTemplates.SYSTEM_INSTRUCTION_CAMPUS_IQ))
                )
            )
            val response = GeminiNetworkClient.apiService.generateContent(
                apiKey = GeminiNetworkClient.getApiKey(),
                request = request
            )
            GeminiResponseParser.extractTextFromResponse(response, fallback)
        } catch (e: Exception) {
            fallback
        }
    }

    suspend fun askAssistant(userQuery: String): String = withContext(Dispatchers.IO) {
        val lower = userQuery.lowercase()
        val fallback = when {
            "attendance" in lower || "75" in lower -> {
                "University regulations require a minimum of 75% attendance in each course to be eligible to appear for Semester-End Examinations (SEE). If attendance falls between 60% and 75% due to medical reasons, condonation may be considered upon submittal of authorized medical certificates."
            }
            "mark" in lower || "cie" in lower || "internal" in lower -> {
                "Continuous Internal Evaluation (CIE) carries 50 marks comprising 2 tests (30 marks averaged), assignments/mini-projects (10 marks), and active tutorial/lab performance (10 marks). Minimum 40% (20 marks) is required to qualify for the final exam."
            }
            "study plan" in lower || "improve" in lower -> {
                "To optimize academic performance: 1) Allocate dedicated 45-minute daily focus sessions for core subjects, 2) Review algorithmic concepts in Jupyter notebooks, 3) Utilize the CampusIQ study plan generator for tailored daily recovery roadmaps."
            }
            else -> {
                "CampusIQ AI Academic Advisor: For questions regarding curriculum syllabi, examination schedules, or attendance condonation, please reference your student portal or consult the Department Academic Coordinator."
            }
        }

        if (!GeminiNetworkClient.isApiKeyConfigured()) {
            return@withContext fallback
        }

        try {
            val prompt = GeminiPromptTemplates.buildAssistantPrompt(userQuery)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.7f, maxOutputTokens = 800),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = GeminiPromptTemplates.SYSTEM_INSTRUCTION_CAMPUS_IQ))
                )
            )
            val response = GeminiNetworkClient.apiService.generateContent(
                apiKey = GeminiNetworkClient.getApiKey(),
                request = request
            )
            GeminiResponseParser.extractTextFromResponse(response, fallback)
        } catch (e: Exception) {
            fallback
        }
    }
}
