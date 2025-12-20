package com.example.ibanla.presentation.iban

import com.example.ibanla.domain.model.Category
import com.example.ibanla.domain.model.IbanItem

sealed interface IbanEvent{
    data class CopyIban(val iban : String) : IbanEvent
    data class TabSelected(val tab : IbanTab) : IbanEvent
    data class IbanSelected(
        val iban : IbanItem,
        val category: Category
    ) : IbanEvent
    data class CategorySelected(
        val category : Category
    ) : IbanEvent
    data class AddIban(val iban : IbanItem) : IbanEvent
    data class UpdateIban(val iban : IbanItem) : IbanEvent
    data class DeleteIban(val iban : IbanItem) : IbanEvent

    data class AddCategory(val category : Category) : IbanEvent
}