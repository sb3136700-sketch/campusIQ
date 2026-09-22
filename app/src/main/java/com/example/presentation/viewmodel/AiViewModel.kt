package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.ClassAnalyticsSummary
import com.example.analytics.StudentAcademicSummary
import com.example.data.local.entity.StudentEntity
import com.example.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "User" or "CampusIQ AI"
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AiViewModel(private val aiRepository: AiRepository) : ViewModel() {

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _studentInsight = MutableStateFlow<String?>(null)
    val studentInsight: StateFlow<String?> = _studentInsight.asStateFlow()

    private val _generatedStudyPlan = MutableStateFlow<String?>(null)
    val generatedStudyPlan: StateFlow<String?> = _generatedStudyPlan.asStateFlow()

    private val _facultyDiagnostic = MutableStateFlow<String?>(null)
    val facultyDiagnostic: StateFlow<String?> = _facultyDiagnostic.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "CampusIQ AI",
                message = "Hello! I am your CampusIQ AI Academic Advisor. Ask me anything about attendance compliance, academic performance improvement, examination patterns, or study strategies.",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun generateStudentAcademicInsight(
        student: StudentEntity,
        summary: StudentAcademicSummary,
        pendingAssignments: Int
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            val insight = aiRepository.generateStudentAcademicInsight(student, summary, pendingAssignments)
            _studentInsight.value = insight
            _isGenerating.value = false
        }
    }

    fun generateStudyPlan(studentName: String, subjectName: String, currentScore: Double) {
        viewModelScope.launch {
            _isGenerating.value = true
            val plan = aiRepository.generateStudyPlan(studentName, subjectName, currentScore)
            _generatedStudyPlan.value = plan
            _isGenerating.value = false
        }
    }

    fun generateFacultyDiagnostic(summary: ClassAnalyticsSummary) {
        viewModelScope.launch {
            _isGenerating.value = true
            val diagnostic = aiRepository.generateFacultyClassDiagnostic(summary)
            _facultyDiagnostic.value = diagnostic
            _isGenerating.value = false
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            sender = "User",
            message = userText.trim(),
            isUser = true
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isGenerating.value = true
            val reply = aiRepository.askAssistant(userText)
            val aiMsg = ChatMessage(
                sender = "CampusIQ AI",
                message = reply,
                isUser = false
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _isGenerating.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "CampusIQ AI",
                message = "Chat history cleared. How can I assist with your academic goals today?",
                isUser = false
            )
        )
    }
}
