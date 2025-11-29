package com.example.ibanla.data.repository

import com.example.ibanla.data.local.IbanDao
import com.example.ibanla.data.mappers.toIbanItem
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
}