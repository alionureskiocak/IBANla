package com.example.ibanla.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem
import kotlinx.coroutines.flow.Flow

@Dao
interface IbanDao {

    @Insert
    suspend fun insertIbanInfo(ibanEntity: IbanEntity)

    @Delete
    suspend fun deleteIbanInfo(ibanEntity: IbanEntity)

    @Query("SELECT * FROM ibans")
    fun getAllIbanInfos() : Flow<List<IbanEntity>>

    @Query("SELECT * FROM ibans WHERE id = :categoryId ")
    fun getIbanInfosByCategory(categoryId : Int) : Flow<List<IbanEntity>>


}