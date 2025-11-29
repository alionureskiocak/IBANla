package com.example.ibanla.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.data.model.IbanEntity

@Database(entities = [IbanEntity::class, CategoryEntity::class], version = 1)
abstract class IbanDatabase : RoomDatabase() {

    abstract fun ibanDao() : IbanDao
}