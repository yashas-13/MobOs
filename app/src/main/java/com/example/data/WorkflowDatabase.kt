package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workflows")
data class WorkflowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val triggerCommand: String,
    val stepsJson: String, // JSON array of steps
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "execution_histories")
data class ExecutionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workflowName: String,
    val commandText: String,
    val status: String, // "SUCCESS", "RUNNING", "FAILED"
    val timestamp: Long = System.currentTimeMillis(),
    val logs: String // String with newlines containing step-by-step terminal logs
)

@Dao
interface WorkflowDao {
    @Query("SELECT * FROM workflows ORDER BY createdAt DESC")
    fun getAllWorkflowsFlow(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows WHERE isActive = 1")
    suspend fun getActiveWorkflows(): List<WorkflowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkflow(workflow: WorkflowEntity): Long

    @Delete
    suspend fun deleteWorkflow(workflow: WorkflowEntity)

    @Query("DELETE FROM workflows")
    suspend fun clearWorkflows()

    @Query("SELECT * FROM execution_histories ORDER BY timestamp DESC")
    fun getAllHistoriesFlow(): Flow<List<ExecutionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ExecutionHistoryEntity): Long

    @Query("DELETE FROM execution_histories")
    suspend fun clearHistory()
}

@Database(entities = [WorkflowEntity::class, ExecutionHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workflowDao(): WorkflowDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agentic_os_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
