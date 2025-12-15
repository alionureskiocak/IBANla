package com.example.ibanla.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem
import kotlinx.coroutines.flow.Flow
import java.nio.charset.CodingErrorAction.IGNORE

@Dao
interface IbanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIbanInfo(ibanEntity: IbanEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteIbanInfo(ibanEntity: IbanEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM ibans")
    fun getAllIbanInfos() : Flow<List<IbanEntity>>

    @Query("SELECT * FROM ibans WHERE categoryId = :categoryId ")
    fun getIbanInfosByCategory(categoryId : Int) : Flow<List<IbanEntity>>

    @Query("SELECT * FROM categories")
    fun getCategories() : Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId : Int) : CategoryEntity?

    @Query("SELECT * FROM categories WHERE categoryName = :name")
    suspend fun getCategoryByName(name : String) : CategoryEntity?


}