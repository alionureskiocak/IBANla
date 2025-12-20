package com.example.ibanla.presentation.iban

sealed interface IbanEvent{
    data class CopyIban(val iban : String) : IbanEvent
}