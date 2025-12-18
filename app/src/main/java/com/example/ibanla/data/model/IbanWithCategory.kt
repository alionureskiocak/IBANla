package com.example.ibanla.data.model

import com.example.ibanla.domain.model.IbanItem

data class IbanWithCategory(
    val ibanItem: IbanItem,
    val categoryEntity: CategoryEntity
)