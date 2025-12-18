package com.example.ibanla.data.repository

import com.example.ibanla.data.local.IbanDao
import com.example.ibanla.data.mappers.toCategoryEntity
import com.example.ibanla.data.mappers.toDomain
import com.example.ibanla.data.mappers.toIbanEntity
import com.example.ibanla.data.mappers.toIbanItem
import com.example.ibanla.domain.model.Category
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.repository.IbanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IbanRepositoryImpl @Inject constructor(
    private val dao : IbanDao
) : IbanRepository{
    override suspend fun insertIbanInfo(ibanItem: IbanItem) {
        dao.insertIbanInfo(ibanItem.toIbanEntity())
    }

    override suspend fun deleteIbanInfo(ibanItem: IbanItem) {
        dao.deleteIbanInfo(ibanItem.toIbanEntity())
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

    override suspend fun insertCategory(categoryEntity: Category) {
        dao.insertCategory(categoryEntity.toCategoryEntity())
    }



    override suspend fun deleteCategory(categoryEntity: Category) {
        dao.deleteCategory(categoryEntity.toCategoryEntity())
    }

    override fun getCategories(): Flow<List<Category>> {
        return dao.getCategories().map {
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Category {
        return dao.getCategoryById(categoryId)!!.toDomain()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return dao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun initializeCategoryId() {
        if (dao.getCategoryById(1000) == null){
            dao.insertCategory(Category(1000,"Benim IBAN'ım").toCategoryEntity())
        }
        if (dao.getCategoryById(1001) == null){
            dao.insertCategory(Category(1001,"Arkadaşlarım").toCategoryEntity())
        }
        if (dao.getCategoryById(1002) == null){
            dao.insertCategory(Category(1002,"Ailem").toCategoryEntity())
        }
        if (dao.getCategoryById(1003) == null){
            dao.insertCategory(Category(1003,"İş").toCategoryEntity())
        }
    }

    override suspend fun updateIbanInfo(ibanItem: IbanItem) {
        dao.updateIbanInfo(ibanItem.toIbanEntity())
    }
}

