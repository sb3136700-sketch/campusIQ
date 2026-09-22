package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AIInsightEntity
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.MarkRecordEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: String): StudentEntity?

    @Query("SELECT * FROM students WHERE studentId = :studentId LIMIT 1")
    fun getStudentFlowById(studentId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE department = :department ORDER BY name ASC")
    fun getStudentsByDepartment(department: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)
}

@Dao
interface FacultyDao {
    @Query("SELECT * FROM faculty ORDER BY name ASC")
    fun getAllFaculty(): Flow<List<FacultyEntity>>

    @Query("SELECT * FROM faculty WHERE facultyId = :facultyId LIMIT 1")
    suspend fun getFacultyById(facultyId: String): FacultyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaculty(faculty: FacultyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacultyList(facultyList: List<FacultyEntity>)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY semester ASC, subjectCode ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE facultyId = :facultyId")
    fun getSubjectsByFaculty(facultyId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE semester = :semester")
    fun getSubjectsBySemester(semester: Int): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE subjectCode = :subjectCode LIMIT 1")
    suspend fun getSubjectByCode(subjectCode: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE subjectCode = :subjectCode ORDER BY date DESC")
    fun getAttendanceForSubject(subjectCode: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND subjectCode = :subjectCode ORDER BY date DESC")
    fun getAttendanceForStudentAndSubject(studentId: String, subjectCode: String): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecordEntity>)
}

@Dao
interface MarksDao {
    @Query("SELECT * FROM marks")
    fun getAllMarks(): Flow<List<MarkRecordEntity>>

    @Query("SELECT * FROM marks WHERE studentId = :studentId")
    fun getMarksForStudent(studentId: String): Flow<List<MarkRecordEntity>>

    @Query("SELECT * FROM marks WHERE subjectCode = :subjectCode")
    fun getMarksForSubject(subjectCode: String): Flow<List<MarkRecordEntity>>

    @Query("SELECT * FROM marks WHERE studentId = :studentId AND subjectCode = :subjectCode")
    fun getMarksForStudentAndSubject(studentId: String, subjectCode: String): Flow<List<MarkRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMark(mark: MarkRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarks(marks: List<MarkRecordEntity>)
}

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE subjectCode = :subjectCode ORDER BY dueDate ASC")
    fun getAssignmentsForSubject(subjectCode: String): Flow<List<AssignmentEntity>>

    @Query("UPDATE assignments SET isSubmitted = :isSubmitted WHERE id = :id")
    suspend fun updateSubmissionStatus(id: Long, isSubmitted: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientRole = :role OR recipientRole = 'STUDENT' ORDER BY timestamp DESC")
    fun getNotificationsForRole(role: UserRole): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
}

@Dao
interface AIInsightDao {
    @Query("SELECT * FROM ai_insights WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getInsightsForStudent(studentId: String): Flow<List<AIInsightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: AIInsightEntity)
}
