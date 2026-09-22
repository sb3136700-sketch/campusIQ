package com.example.presentation.navigation

object NavRoutes {
    const val AUTH = "auth"
    const val STUDENT_MAIN = "student_main"
    const val FACULTY_MAIN = "faculty_main"
    const val ADMIN_MAIN = "admin_main"
    const val PROFILE = "profile"
}

enum class StudentTab(val title: String) {
    DASHBOARD("Overview"),
    ATTENDANCE("Attendance"),
    MARKS("Marks"),
    ASSIGNMENTS("Tasks"),
    AI_ADVISOR("AI Advisor")
}

enum class FacultyTab(val title: String) {
    DASHBOARD("Overview"),
    ATTENDANCE("Attendance"),
    MARKS("Marks"),
    ANALYTICS("Analytics"),
    STUDENTS("Students")
}

enum class AdminTab(val title: String) {
    DASHBOARD("Overview"),
    DEPARTMENTS("Departments"),
    MANAGEMENT("Directory"),
    REPORTS("Reports")
}
