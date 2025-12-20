package com.example.ibanla.domain.model

import com.example.ibanla.data.model.CategoryEntity

data class IbanWithCategory(
    val ibanItem: IbanItem,
    val categoryEntity: Category
)