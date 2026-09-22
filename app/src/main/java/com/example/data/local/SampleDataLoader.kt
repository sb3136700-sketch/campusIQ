package com.example.data.local

import com.example.data.local.entity.AIInsightEntity
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.MarkRecordEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.ExamType
import com.example.data.model.NotificationType
import com.example.data.model.UserRole

object SampleDataLoader {

    suspend fun populateDatabase(db: CampusIqDatabase) {
        val userDao = db.userDao()
        val studentDao = db.studentDao()
        val facultyDao = db.facultyDao()
        val subjectDao = db.subjectDao()
        val attendanceDao = db.attendanceDao()
        val marksDao = db.marksDao()
        val assignmentDao = db.assignmentDao()
        val notificationDao = db.notificationDao()
        val aiInsightDao = db.aiInsightDao()

        // 1. Predefined Users for all 3 roles
        val users = listOf(
            UserEntity(
                id = "usr_student_01",
                name = "Rohan Sharma",
                email = "student@campusiq.edu",
                role = UserRole.STUDENT,
                department = "AI & ML",
                refId = "1MS21AI042"
            ),
            UserEntity(
                id = "usr_faculty_01",
                name = "Dr. Priya Nair",
                email = "faculty@campusiq.edu",
                role = UserRole.FACULTY,
                department = "AI & ML",
                refId = "FAC_AIML_01"
            ),
            UserEntity(
                id = "usr_admin_01",
                name = "Dr. S. K. Murthy",
                email = "admin@campusiq.edu",
                role = UserRole.ADMIN,
                department = "Academic Affairs",
                refId = "ADM_001"
            )
        )
        userDao.insertUsers(users)

        // 2. Sample Students
        val students = listOf(
            StudentEntity(
                studentId = "1MS21AI042",
                name = "Rohan Sharma",
                email = "student@campusiq.edu",
                department = "AI & ML",
                semester = 6,
                section = "A",
                batchYear = "2021-2025",
                phone = "+91 98450 12345",
                cgpa = 8.64
            ),
            StudentEntity(
                studentId = "1MS21AI015",
                name = "Ananya Rao",
                email = "ananya.rao@campusiq.edu",
                department = "AI & ML",
                semester = 6,
                section = "A",
                batchYear = "2021-2025",
                phone = "+91 98450 67890",
                cgpa = 9.32
            ),
            StudentEntity(
                studentId = "1MS21AI058",
                name = "Vikramaditya Verma",
                email = "vikram.v@campusiq.edu",
                department = "AI & ML",
                semester = 6,
                section = "A",
                batchYear = "2021-2025",
                phone = "+91 98450 54321",
                cgpa = 6.45 // Low attendance & marks example for support identification
            ),
            StudentEntity(
                studentId = "1MS21AI051",
                name = "Sneha Kulkarni",
                email = "sneha.k@campusiq.edu",
                department = "AI & ML",
                semester = 6,
                section = "A",
                batchYear = "2021-2025",
                phone = "+91 98450 98765",
                cgpa = 8.85
            ),
            StudentEntity(
                studentId = "1MS21AI033",
                name = "Mohammed Zeeshan",
                email = "zeeshan.m@campusiq.edu",
                department = "AI & ML",
                semester = 6,
                section = "B",
                batchYear = "2021-2025",
                phone = "+91 98450 11223",
                cgpa = 7.90
            )
        )
        studentDao.insertStudents(students)

        // 3. Faculty Members
        val facultyMembers = listOf(
            FacultyEntity(
                facultyId = "FAC_AIML_01",
                name = "Dr. Priya Nair",
                email = "faculty@campusiq.edu",
                department = "AI & ML",
                designation = "Professor & Head of Dept",
                cabin = "CS-301",
                phone = "+91 98110 44556"
            ),
            FacultyEntity(
                facultyId = "FAC_AIML_02",
                name = "Prof. Arvind Swamy",
                email = "arvind.swamy@campusiq.edu",
                department = "AI & ML",
                designation = "Associate Professor",
                cabin = "CS-204",
                phone = "+91 98110 77889"
            ),
            FacultyEntity(
                facultyId = "FAC_AIML_03",
                name = "Dr. Kavitha Rajan",
                email = "kavitha.rajan@campusiq.edu",
                department = "AI & ML",
                designation = "Assistant Professor",
                cabin = "CS-209",
                phone = "+91 98110 33221"
            )
        )
        facultyDao.insertFacultyList(facultyMembers)

        // 4. Subjects
        val subjects = listOf(
            SubjectEntity("21AI61", "Machine Learning Techniques", "AI & ML", 6, 4, "FAC_AIML_01"),
            SubjectEntity("21AI62", "Deep Learning & Neural Networks", "AI & ML", 6, 4, "FAC_AIML_02"),
            SubjectEntity("21AI63", "Natural Language Processing", "AI & ML", 6, 3, "FAC_AIML_03"),
            SubjectEntity("21AI64", "Cloud Computing & Big Data", "AI & ML", 6, 3, "FAC_AIML_02"),
            SubjectEntity("21AIL66", "AI & ML Laboratory", "AI & ML", 6, 2, "FAC_AIML_01")
        )
        subjectDao.insertSubjects(subjects)

        // 5. Attendance Records for Rohan Sharma (1MS21AI042)
        // 21AI61 (ML): 20 sessions, 18 present (90%)
        // 21AI62 (DL): 18 sessions, 14 present (77.7%)
        // 21AI63 (NLP): 16 sessions, 14 present (87.5%)
        // 21AI64 (Cloud): 15 sessions, 11 present (73.3% - slight warning)
        // 21AIL66 (Lab): 10 sessions, 9 present (90%)
        val attendanceList = mutableListOf<AttendanceRecordEntity>()

        // 21AI61
        for (i in 1..20) {
            attendanceList.add(
                AttendanceRecordEntity(
                    studentId = "1MS21AI042",
                    subjectCode = "21AI61",
                    date = "2026-03-${i.toString().padStart(2, '0')}",
                    status = if (i in listOf(4, 12)) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT,
                    topicCovered = "Unit ${((i - 1) / 4) + 1}: ML Model Theory and Validation"
                )
            )
        }

        // 21AI62
        for (i in 1..18) {
            attendanceList.add(
                AttendanceRecordEntity(
                    studentId = "1MS21AI042",
                    subjectCode = "21AI62",
                    date = "2026-03-${i.toString().padStart(2, '0')}",
                    status = if (i in listOf(2, 7, 11, 15)) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT,
                    topicCovered = "Backpropagation and Convolutional Layers"
                )
            )
        }

        // 21AI63
        for (i in 1..16) {
            attendanceList.add(
                AttendanceRecordEntity(
                    studentId = "1MS21AI042",
                    subjectCode = "21AI63",
                    date = "2026-03-${i.toString().padStart(2, '0')}",
                    status = if (i in listOf(3, 9)) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT,
                    topicCovered = "Tokenization, Transformers & Attention"
                )
            )
        }

        // 21AI64
        for (i in 1..15) {
            attendanceList.add(
                AttendanceRecordEntity(
                    studentId = "1MS21AI042",
                    subjectCode = "21AI64",
                    date = "2026-03-${i.toString().padStart(2, '0')}",
                    status = if (i in listOf(1, 5, 8, 14)) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT,
                    topicCovered = "Distributed MapReduce and Spark Architecture"
                )
            )
        }

        // 21AIL66
        for (i in 1..10) {
            attendanceList.add(
                AttendanceRecordEntity(
                    studentId = "1MS21AI042",
                    subjectCode = "21AIL66",
                    date = "2026-03-${(i * 2).toString().padStart(2, '0')}",
                    status = if (i == 6) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT,
                    topicCovered = "Lab Experiment #$i: Tensor Operations & PyTorch"
                )
            )
        }

        // Also add attendance for Vikramaditya (1MS21AI058) to showcase low attendance warning in Faculty dashboard
        for (i in 1..20) {
            attendanceList.add(
                AttendanceRecordEntity(
                    studentId = "1MS21AI058",
                    subjectCode = "21AI61",
                    date = "2026-03-${i.toString().padStart(2, '0')}",
                    status = if (i % 2 == 0) AttendanceStatus.ABSENT else AttendanceStatus.PRESENT, // 50% attendance
                    topicCovered = "ML Session $i"
                )
            )
        }

        attendanceDao.insertAttendanceList(attendanceList)

        // 6. Marks Records for Rohan Sharma (1MS21AI042)
        val marksList = listOf(
            // 21AI61: Machine Learning Techniques
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI61", examType = ExamType.INTERNAL_1, score = 27.5, maxScore = 30.0, remarks = "Strong grasp of algorithms"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI61", examType = ExamType.INTERNAL_2, score = 28.0, maxScore = 30.0, remarks = "Excellent test results"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI61", examType = ExamType.ASSIGNMENT, score = 19.0, maxScore = 20.0, remarks = "Superb implementation"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI61", examType = ExamType.LAB_PRACTICAL, score = 19.5, maxScore = 20.0, remarks = "Flawless code execution"),

            // 21AI62: Deep Learning
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI62", examType = ExamType.INTERNAL_1, score = 25.0, maxScore = 30.0, remarks = "Good conceptual knowledge"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI62", examType = ExamType.INTERNAL_2, score = 24.5, maxScore = 30.0, remarks = "Needs practice with LSTM math"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI62", examType = ExamType.ASSIGNMENT, score = 17.0, maxScore = 20.0, remarks = "Solid notebook submission"),

            // 21AI63: Natural Language Processing
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI63", examType = ExamType.INTERNAL_1, score = 28.5, maxScore = 30.0, remarks = "Highest in section"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI63", examType = ExamType.INTERNAL_2, score = 29.0, maxScore = 30.0, remarks = "Outstanding analytical ability"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI63", examType = ExamType.ASSIGNMENT, score = 19.5, maxScore = 20.0, remarks = "Innovative BERT fine-tuning"),

            // 21AI64: Cloud Computing & Big Data (Struggling/Improvement Area)
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI64", examType = ExamType.INTERNAL_1, score = 18.0, maxScore = 30.0, remarks = "Review distributed consensus protocols"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI64", examType = ExamType.INTERNAL_2, score = 19.5, maxScore = 30.0, remarks = "Slight improvement, focus on Hadoop"),
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AI64", examType = ExamType.ASSIGNMENT, score = 15.0, maxScore = 20.0, remarks = "Submitted on deadline"),

            // 21AIL66: AI Lab
            MarkRecordEntity(studentId = "1MS21AI042", subjectCode = "21AIL66", examType = ExamType.LAB_PRACTICAL, score = 48.0, maxScore = 50.0, remarks = "Distinction in PyTorch & OpenCV")
        )
        marksDao.insertMarks(marksList)

        // Vikramaditya's marks (showing struggling indicators)
        val vikramMarks = listOf(
            MarkRecordEntity(studentId = "1MS21AI058", subjectCode = "21AI61", examType = ExamType.INTERNAL_1, score = 11.0, maxScore = 30.0, remarks = "Needs foundational recap"),
            MarkRecordEntity(studentId = "1MS21AI058", subjectCode = "21AI61", examType = ExamType.INTERNAL_2, score = 10.5, maxScore = 30.0, remarks = "Book office hours consultation")
        )
        marksDao.insertMarks(vikramMarks)

        // 7. Assignments
        val assignments = listOf(
            AssignmentEntity(
                subjectCode = "21AI61",
                title = "Ensemble Learning & Gradient Boosting",
                description = "Implement XGBoost and LightGBM from scratch on the California Housing dataset. Provide hyperparameter tuning analysis.",
                dueDate = "2026-04-05",
                maxMarks = 20,
                isSubmitted = true
            ),
            AssignmentEntity(
                subjectCode = "21AI62",
                title = "Vision Transformer (ViT) Implementation",
                description = "Build a patch-embedding projection and multi-head self-attention module using PyTorch for CIFAR-10 classification.",
                dueDate = "2026-04-12",
                maxMarks = 20,
                isSubmitted = false
            ),
            AssignmentEntity(
                subjectCode = "21AI63",
                title = "Transformer Attention Masking & Tokenizer",
                description = "Construct BPE (Byte Pair Encoding) tokenizer and compare attention matrices for sentiment classification.",
                dueDate = "2026-04-18",
                maxMarks = 20,
                isSubmitted = true
            ),
            AssignmentEntity(
                subjectCode = "21AI64",
                title = "Apache Spark Streaming Pipeline",
                description = "Build a local PySpark streaming pipeline to ingest Twitter-like sentiment json feeds and compute 5-minute rolling averages.",
                dueDate = "2026-04-25",
                maxMarks = 20,
                isSubmitted = false
            )
        )
        assignmentDao.insertAssignments(assignments)

        // 8. Notifications
        val notifications = listOf(
            NotificationEntity(
                recipientRole = UserRole.STUDENT,
                title = "Attendance Alert: Cloud Computing (21AI64)",
                message = "Your attendance in 21AI64 currently stands at 73.3%. University compliance requires minimum 75% for exam eligibility. Attend upcoming lectures.",
                type = NotificationType.ATTENDANCE_WARNING,
                isRead = false
            ),
            NotificationEntity(
                recipientRole = UserRole.STUDENT,
                title = "Internal Marks Published: Machine Learning (21AI61)",
                message = "Internal Assessment 2 results for 21AI61 have been published. Check your score and faculty feedback.",
                type = NotificationType.MARKS_RELEASED,
                isRead = true
            ),
            NotificationEntity(
                recipientRole = UserRole.STUDENT,
                title = "Campus Hackathon: AI for Sustainability 2026",
                message = "Annual Inter-College Hackathon registration is now open for BE CSE/AIML students. Grand prize ₹1,00,000.",
                type = NotificationType.ANNOUNCEMENT,
                isRead = false
            ),
            NotificationEntity(
                recipientRole = UserRole.FACULTY,
                title = "Mid-Term Academic Audit Meeting",
                message = "Department HOD meeting scheduled for Friday 3:00 PM in Conference Room A regarding academic progress reviews.",
                type = NotificationType.ANNOUNCEMENT,
                isRead = false
            ),
            NotificationEntity(
                recipientRole = UserRole.ADMIN,
                title = "Quarterly Accreditation Compliance Report Ready",
                message = "The institutional attendance and performance analytics data has been compiled for NAAC/NBA review.",
                type = NotificationType.ANNOUNCEMENT,
                isRead = false
            )
        )
        notificationDao.insertNotifications(notifications)

        // 9. Initial AI Academic Insight
        val initialInsight = AIInsightEntity(
            studentId = "1MS21AI042",
            title = "Academic Progress Summary & Trajectory Analysis",
            summary = "Outstanding performance in NLP (95.0%) and Machine Learning (92.5%). Primary growth opportunity identified in Cloud Computing & Big Data (62.5% marks, 73.3% attendance).",
            recommendations = "1. Allocate 45 minutes daily to distributed systems concepts in 21AI64.\n2. Maintain consistent lecture attendance in Cloud Computing to cross the 75% threshold.\n3. Leverage your high proficiency in Python & ML to master Apache Spark APIs."
        )
        aiInsightDao.insertInsight(initialInsight)
    }
}
