package com.finance.valuespend.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ExpenseEntity): Long

    @Update
    suspend fun update(entity: ExpenseEntity)

    @Delete
    suspend fun delete(entity: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun observeById(id: Long): Flow<ExpenseEntity?>

    @Query(
        """
        SELECT * FROM expenses
        WHERE (:category IS NULL OR category = :category)
          AND (:fromEpochDay IS NULL OR dateEpochDay >= :fromEpochDay)
          AND (:toEpochDay IS NULL OR dateEpochDay <= :toEpochDay)
          AND (:minSatisfaction IS NULL OR satisfaction >= :minSatisfaction)
          AND (:maxSatisfaction IS NULL OR satisfaction <= :maxSatisfaction)
        ORDER BY dateEpochDay DESC, createdAtEpochMs DESC
        """
    )
    fun observeFiltered(
        category: String?,
        fromEpochDay: Long?,
        toEpochDay: Long?,
        minSatisfaction: Int?,
        maxSatisfaction: Int?
    ): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses ORDER BY dateEpochDay DESC, createdAtEpochMs DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT * FROM expenses
        WHERE (:category IS NULL OR category = :category)
          AND (:fromEpochDay IS NULL OR dateEpochDay >= :fromEpochDay)
          AND (:toEpochDay IS NULL OR dateEpochDay <= :toEpochDay)
          AND (:minSatisfaction IS NULL OR satisfaction >= :minSatisfaction)
          AND (:maxSatisfaction IS NULL OR satisfaction <= :maxSatisfaction)
        ORDER BY dateEpochDay DESC, createdAtEpochMs DESC
        """
    )
    suspend fun getFiltered(
        category: String?,
        fromEpochDay: Long?,
        toEpochDay: Long?,
        minSatisfaction: Int?,
        maxSatisfaction: Int?
    ): List<ExpenseEntity>

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}

