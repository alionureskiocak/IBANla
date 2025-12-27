package com.example.ibanla.presentation.ibanquery

sealed interface QueryEvent {

    data class FindBank(val iban : String) : QueryEvent
}