package com.mckl.satiation1.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val pronouns: String,
    val heightCm: Double? = null,
    val startWeightKg: Double,
    val currentWeightKg: Double
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val geminiApiKey: String? = null,
    val preferredUnits: String = "metric",
    val preferredDateFormat: String = "day_month_year",
    val calorieTarget: Double = 2500.0,
    val proteinTargetGrams: Double = 120.0,
    val carbsTargetGrams: Double = 300.0,
    val fatsTargetGrams: Double = 70.0,
    val themePreference: String = "dark",
    val followSystemTheme: Boolean = true,
    val primaryAccentHex: String = "#BDE064",
    val secondaryAccentHex: String = "#FF7D5A",
    val mealReminderEnabled: Boolean = false,
    val mealReminderHour: Int = 12,
    val mealReminderMinute: Int = 30,
    val weightReminderEnabled: Boolean = false,
    val weightReminderHour: Int = 8,
    val weightReminderMinute: Int = 0,
    val macroReminderEnabled: Boolean = false,
    val macroReminderHour: Int = 20,
    val macroReminderMinute: Int = 0
)

@Entity(
    tableName = "meal_logs",
    indices = [Index("loggedAtEpochMillis")]
)
data class MealLog(
    @PrimaryKey(autoGenerate = true) val mealId: Long = 0,
    val loggedAtEpochMillis: Long,
    val sourceType: String,
    val totalCalories: Double,
    val totalProteinGrams: Double,
    val totalCarbsGrams: Double,
    val totalFatsGrams: Double,
    val notes: String? = null
)

@Entity(
    tableName = "meal_items",
    foreignKeys = [
        ForeignKey(
            entity = MealLog::class,
            parentColumns = ["mealId"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealId")]
)
data class MealItem(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val mealId: Long,
    val name: String,
    val category: String? = null,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val confidence: Double? = null
)

@Entity(
    tableName = "weight_logs",
    indices = [Index("loggedAtEpochMillis")]
)
data class WeightLog(
    @PrimaryKey(autoGenerate = true) val weightLogId: Long = 0,
    val loggedAtEpochMillis: Long,
    val weightKg: Double
)

@Entity(tableName = "preset_foods")
data class PresetFood(
    @PrimaryKey(autoGenerate = true) val presetFoodId: Long = 0,
    val name: String,
    val category: String? = null,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val notes: String? = null,
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)

data class MealWithItems(
    @Embedded val meal: MealLog,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId"
    )
    val items: List<MealItem>
)

data class DailyMacroTotals(
    val day: String,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double
)

data class DailyMealSummary(
    val day: String,
    val mealCount: Int,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double
)

data class FoodFrequencySummary(
    val name: String,
    val occurrences: Int,
    val totalCalories: Double
)

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile): Long

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Query("DELETE FROM user_profile")
    suspend fun deleteAllProfiles()

    @Query(
        """
        SELECT CASE
            WHEN heightCm IS NULL OR heightCm <= 0 OR currentWeightKg <= 0 THEN NULL
            ELSE currentWeightKg / ((heightCm / 100.0) * (heightCm / 100.0))
        END
        FROM user_profile
        WHERE id = 1
        """
    )
    fun getCurrentBmi(): Flow<Double?>
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettingsOnce(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettings): Long
}

@Dao
interface MealDao {
    @Insert
    suspend fun insertMealLog(mealLog: MealLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMealLog(mealLog: MealLog): Long

    @Insert
    suspend fun insertMealItems(items: List<MealItem>): List<Long>

    @Query("DELETE FROM meal_items WHERE mealId = :mealId")
    suspend fun deleteMealItemsForMeal(mealId: Long)

    @Query("DELETE FROM meal_logs WHERE mealId = :mealId")
    suspend fun deleteMealLog(mealId: Long)

    @Transaction
    @Query(
        """
        SELECT * FROM meal_logs
        WHERE loggedAtEpochMillis BETWEEN :startInclusive AND :endInclusive
        ORDER BY loggedAtEpochMillis DESC
        """
    )
    fun getMealsBetween(startInclusive: Long, endInclusive: Long): Flow<List<MealWithItems>>

    @Transaction
    @Query(
        """
        SELECT * FROM meal_logs
        ORDER BY loggedAtEpochMillis DESC
        """
    )
    fun getAllMeals(): Flow<List<MealWithItems>>

    @Transaction
    @Query(
        """
        SELECT * FROM meal_logs
        ORDER BY loggedAtEpochMillis DESC
        """
    )
    suspend fun getAllMealsOnce(): List<MealWithItems>

    @Query(
        """
        SELECT
            date(loggedAtEpochMillis / 1000, 'unixepoch', 'localtime') AS day,
            COALESCE(SUM(totalCalories), 0) AS calories,
            COALESCE(SUM(totalProteinGrams), 0) AS proteinGrams,
            COALESCE(SUM(totalCarbsGrams), 0) AS carbsGrams,
            COALESCE(SUM(totalFatsGrams), 0) AS fatsGrams
        FROM meal_logs
        WHERE loggedAtEpochMillis BETWEEN :startInclusive AND :endInclusive
        GROUP BY day
        ORDER BY day ASC
        """
    )
    fun getDailyMacroTotals(startInclusive: Long, endInclusive: Long): Flow<List<DailyMacroTotals>>

    @Query(
        """
        SELECT
            date(loggedAtEpochMillis / 1000, 'unixepoch', 'localtime') AS day,
            COUNT(*) AS mealCount,
            COALESCE(SUM(totalCalories), 0) AS calories,
            COALESCE(SUM(totalProteinGrams), 0) AS proteinGrams,
            COALESCE(SUM(totalCarbsGrams), 0) AS carbsGrams,
            COALESCE(SUM(totalFatsGrams), 0) AS fatsGrams
        FROM meal_logs
        WHERE loggedAtEpochMillis BETWEEN :startInclusive AND :endInclusive
        GROUP BY day
        ORDER BY day ASC
        """
    )
    fun getDailyMealSummaries(startInclusive: Long, endInclusive: Long): Flow<List<DailyMealSummary>>

    @Query("SELECT MIN(loggedAtEpochMillis) FROM meal_logs")
    fun getEarliestMealLoggedAt(): Flow<Long?>

    @Query("DELETE FROM meal_logs")
    suspend fun deleteAllMealLogs()

    @Query(
        """
        SELECT
            name AS name,
            COUNT(*) AS occurrences,
            COALESCE(SUM(calories), 0) AS totalCalories
        FROM meal_items
        GROUP BY name
        ORDER BY occurrences DESC, totalCalories DESC, name COLLATE NOCASE ASC
        LIMIT :limit
        """
    )
    fun getTopFoods(limit: Int): Flow<List<FoodFrequencySummary>>
}

@Dao
interface WeightLogDao {
    @Insert
    suspend fun insertWeightLog(weightLog: WeightLog): Long

    @Query("SELECT * FROM weight_logs ORDER BY loggedAtEpochMillis ASC")
    fun getWeightHistory(): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_logs ORDER BY loggedAtEpochMillis ASC")
    suspend fun getWeightHistoryOnce(): List<WeightLog>

    @Query("DELETE FROM weight_logs")
    suspend fun deleteAllWeightLogs()
}

@Dao
interface PresetFoodDao {
    @Query("SELECT * FROM preset_foods ORDER BY name COLLATE NOCASE ASC")
    fun getPresetFoods(): Flow<List<PresetFood>>

    @Query("SELECT * FROM preset_foods ORDER BY name COLLATE NOCASE ASC")
    suspend fun getPresetFoodsOnce(): List<PresetFood>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePresetFood(presetFood: PresetFood): Long

    @Query("DELETE FROM preset_foods WHERE presetFoodId = :presetFoodId")
    suspend fun deletePresetFoodById(presetFoodId: Long)

    @Query("DELETE FROM preset_foods")
    suspend fun deleteAllPresetFoods()
}

@Database(
    entities = [
        UserProfile::class,
        AppSettings::class,
        MealLog::class,
        MealItem::class,
        WeightLog::class,
        PresetFood::class
    ],
    version = 6,
    exportSchema = true
)
abstract class SatiationDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun mealDao(): MealDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun presetFoodDao(): PresetFoodDao

    companion object {
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE app_settings
                    ADD COLUMN preferredDateFormat TEXT NOT NULL DEFAULT 'day_month_year'
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE app_settings ADD COLUMN followSystemTheme INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN primaryAccentHex TEXT NOT NULL DEFAULT '#BDE064'")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN secondaryAccentHex TEXT NOT NULL DEFAULT '#FF7D5A'")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN mealReminderEnabled INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN mealReminderHour INTEGER NOT NULL DEFAULT 12")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN mealReminderMinute INTEGER NOT NULL DEFAULT 30")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN weightReminderEnabled INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN weightReminderHour INTEGER NOT NULL DEFAULT 8")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN weightReminderMinute INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN macroReminderEnabled INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN macroReminderHour INTEGER NOT NULL DEFAULT 20")
                database.execSQL("ALTER TABLE app_settings ADD COLUMN macroReminderMinute INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: SatiationDatabase? = null

        fun getDatabase(context: Context): SatiationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SatiationDatabase::class.java,
                    "satiation_database"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
