package com.example.ibanla.domain.repository

import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem
import kotlinx.coroutines.flow.Flow

interface IbanRepository {

    suspend fun insertIbanInfo(ibanEntity: IbanEntity)

    suspend fun deleteIbanInfo(ibanEntity: IbanEntity)

    fun getAllIbanInfos() : Flow<List<IbanItem>>

    fun getIbanInfosByCategory(categoryId : Int) : Flow<List<IbanItem>>
}