package com.example.ibanla.domain.model

import androidx.room.Entity


data class IbanItem(
    val id : Int,
    val iban : String,
    val ownerName : String,
    val bankName : String,
    val categoryId : Int
)