package com.example.ibanla.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "ibans",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.Companion.SET_NULL
        )
    ]
)
data class IbanEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val iban : String,
    val ownerName : String,
    val bankName : String,
    val categoryId : Int
)