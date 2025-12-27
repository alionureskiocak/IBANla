package com.example.ibanla.presentation.ibanquery

import androidx.lifecycle.ViewModel
import androidx.room.Query
import com.example.ibanla.R
import com.example.ibanla.domain.model.BankInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QueryViewModel @Inject constructor(

) : ViewModel(){

    private val _state = MutableStateFlow(QueryState())
    val state : StateFlow<QueryState> = _state.asStateFlow()

    fun onEvent(event : QueryEvent){
        when(event){
            is QueryEvent.FindBank ->{
                findBankByIban(event.iban)
            }
        }
    }

    fun findBankByIban(iban : String){
        val cleaned = iban.uppercase().replace(" ", "")

        if (cleaned.length < 26) {
            _state.update {
                it.copy(
                    bankInfo = BankInfo("Geçersiz IBAN", R.drawable.unknown)
                )
            }
            return
        }

        val code = cleaned.substring(4, 9)

        _state.update {
            it.copy(
                bankInfo = when (code) {
                    "00010" -> BankInfo("Ziraat Bankası",R.drawable.ziraat)
                    "00064" -> BankInfo("İş Bankası",R.drawable.isbankasi)
                    "00111" -> BankInfo("QNB Finansbank",R.drawable.qnb)
                    "00046" -> BankInfo("Akbank",R.drawable.akbank)
                    "00067" -> BankInfo("Yapı Kredi",R.drawable.yapikredi)
                    "00012" -> BankInfo("Halkbank",R.drawable.halkbank)
                    "00015" -> BankInfo("Vakıfbank",R.drawable.vakifbank)
                    "00062" -> BankInfo("Garanti BBVA",R.drawable.garanti)
                    else -> BankInfo("Bilinmeyen Banka",R.drawable.unknown)
                }
            )
        }

    }

}

data class QueryState(
    val bankInfo: BankInfo = BankInfo("",-1)
)

