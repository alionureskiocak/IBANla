package com.example.ibanla.domain.repository

import androidx.room.Query
import com.example.ibanla.domain.model.Category
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem
import kotlinx.coroutines.flow.Flow

interface IbanRepository {

    suspend fun insertIbanInfo(ibanItem: IbanItem)

    suspend fun deleteIbanInfo(ibanItem: IbanItem)

    suspend fun updateIbanInfo(ibanItem: IbanItem)

    fun getAllIbanInfos() : Flow<List<IbanItem>>

    fun getIbanInfosByCategory(categoryId : Int) : Flow<List<IbanItem>>

    suspend fun insertCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    fun getCategories() : Flow<List<Category>>

   suspend fun getCategoryById(categoryId : Int) : Category

    suspend fun getCategoryByName(name : String) : Category?

    suspend fun initializeCategoryId()
}