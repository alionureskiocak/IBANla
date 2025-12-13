package com.example.ibanla.data.repository

import com.example.ibanla.data.local.IbanDao
import com.example.ibanla.data.mappers.toIbanItem
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.repository.IbanRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class IbanRepositoryImpl @Inject constructor(
    private val dao : IbanDao
) : IbanRepository{
    override suspend fun insertIbanInfo(ibanEntity: IbanEntity) {
        dao.insertIbanInfo(ibanEntity)
    }

    override suspend fun deleteIbanInfo(ibanEntity: IbanEntity) {
        dao.deleteIbanInfo(ibanEntity)
    }

    override fun getAllIbanInfos(): Flow<List<IbanItem>> {
        val entities = dao.getAllIbanInfos()
        val ibans = entities.map{
            it.map {
                it.toIbanItem()
            }
        }
        return(ibans)
    }


    override fun getIbanInfosByCategory(categoryId: Int): Flow<List<IbanItem>> {
        val entities = dao.getIbanInfosByCategory(categoryId)
        val ibans = entities.map {
            it.map {
                it.toIbanItem()
            }
        }
        return ibans
    }

    override suspend fun insertCategory(categoryEntity: CategoryEntity) {
        dao.insertCategory(categoryEntity)
    }



    override suspend fun deleteCategory(categoryEntity: CategoryEntity) {
        dao.deleteCategory(categoryEntity)
    }

    override fun getCategories(): Flow<List<CategoryEntity>> {
        return dao.getCategories()
    }

    override suspend fun getCategoryById(categoryId: Int): CategoryEntity {
        return dao.getCategoryById(categoryId)!!
    }

    override suspend fun getCategoryByName(name: String): CategoryEntity? {
        return dao.getCategoryByName(name)
    }

    override suspend fun initializeCategoryId() {
        if (dao.getCategoryById(1000) == null){
            dao.insertCategory(CategoryEntity(1000,"Benim IBAN'ım"))
        }
        if (dao.getCategoryById(999) == null){
            dao.insertCategory(CategoryEntity(999,"Arkadaşlarım"))
        }
    }
}