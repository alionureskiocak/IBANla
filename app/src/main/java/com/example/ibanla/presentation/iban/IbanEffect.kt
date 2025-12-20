package com.example.ibanla.presentation.iban

sealed interface IbanEffect{
    data class ShowToast(val message : String) : IbanEffect
    object StartCopyTick : IbanEffect
}