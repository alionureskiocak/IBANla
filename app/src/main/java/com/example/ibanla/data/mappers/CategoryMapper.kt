package com.example.ibanla.data.mappers

import com.example.ibanla.domain.model.Category
import com.example.ibanla.data.model.CategoryEntity

fun CategoryEntity.toDomain() : Category{
    return Category(
        id = id,
        name = categoryName
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        categoryName = name
    )
}