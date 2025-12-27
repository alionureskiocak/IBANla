package com.example.ibanla.presentation.ibanquery

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QueryViewModel @Inject constructor(

) : ViewModel(){


    fun onEvent(event : QueryEvent){
        when(event){
            is QueryEvent.FindBank ->{
                findBankByIban(event.iban)
            }
        }
    }

    fun findBankByIban(iban : String){

    }

}

