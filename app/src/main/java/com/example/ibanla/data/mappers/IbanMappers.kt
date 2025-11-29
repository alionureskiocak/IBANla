package com.example.ibanla.data.mappers

import com.example.ibanla.data.model.IbanEntity
import com.example.ibanla.domain.model.IbanItem

fun IbanEntity.toIbanItem() : IbanItem{
    return IbanItem(
        id = id,
        iban = iban,
        ownerName = ownerName,
        bankName = bankName,
        categoryId = categoryId
    )
}

fun IbanItem.toIbanEntity() : IbanEntity{
    return IbanEntity(
        id = id,
        iban = iban,
        ownerName = ownerName,
        bankName = bankName,
        categoryId = categoryId
    )
}