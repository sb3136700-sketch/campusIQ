package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AIInsightDao
import com.example.data.local.dao.AssignmentDao
import com.example.data.local.dao.AttendanceDao
import com.example.data.local.dao.FacultyDao
import com.example.data.local.dao.MarksDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.StudentDao
import com.example.data.local.dao.SubjectDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AIInsightEntity
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.FacultyEntity
import com.example.data.local.entity.MarkRecordEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        FacultyEntity::class,
        SubjectEntity::class,
        AttendanceRecordEntity::class,
        MarkRecordEntity::class,
        AssignmentEntity::class,
        NotificationEntity::class,
        AIInsightEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(CampusTypeConverters::class)
abstract class CampusIqDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun facultyDao(): FacultyDao
    abstract fun subjectDao(): SubjectDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun marksDao(): MarksDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun aiInsightDao(): AIInsightDao

    companion object {
        @Volatile
        private var INSTANCE: CampusIqDatabase? = null

        fun getInstance(context: Context): CampusIqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusIqDatabase::class.java,
                    "campusiq_academic.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate realistic academic sample database on initial creation
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).populateInitialData()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialData() {
        SampleDataLoader.populateDatabase(this)
    }
}
