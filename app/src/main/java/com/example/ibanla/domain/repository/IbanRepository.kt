package com.example.ibanla.domain.repository

import androidx.room.Query
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem
import kotlinx.coroutines.flow.Flow

interface IbanRepository {

    suspend fun insertIbanInfo(ibanEntity: IbanEntity)

    suspend fun deleteIbanInfo(ibanEntity: IbanEntity)

    suspend fun updateIbanInfo(ibanItem: IbanItem)

    fun getAllIbanInfos() : Flow<List<IbanItem>>

    fun getIbanInfosByCategory(categoryId : Int) : Flow<List<IbanItem>>

    suspend fun insertCategory(categoryEntity: CategoryEntity)

    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    fun getCategories() : Flow<List<CategoryEntity>>

   suspend fun getCategoryById(categoryId : Int) : CategoryEntity

    suspend fun getCategoryByName(name : String) : CategoryEntity?

    suspend fun initializeCategoryId()
}