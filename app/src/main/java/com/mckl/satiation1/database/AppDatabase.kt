package com.mckl.satiation1.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. The Entity (The Table)
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Only 1 profile exists
    val name: String,
    val pronouns: String,
    val startWeight: Int,
    val currentWeight: Int,
    val age: Int
)

// 2. The DAO (The SQL Commands)
@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateProfile(profile: UserProfile)
}

// 3. The Database Setup
@Database(entities = [UserProfile::class], version = 1, exportSchema = false)
abstract class SatiationDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: SatiationDatabase? = null

        fun getDatabase(context: Context): SatiationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SatiationDatabase::class.java,
                    "satiation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}