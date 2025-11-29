package com.example.ibanla.presentation.iban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibanla.data.mappers.toIbanEntity
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.repository.IbanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IbanViewModel @Inject constructor(
    private val repository: IbanRepository
) : ViewModel(){

    private val _state = MutableStateFlow(IbanState())
    val state : StateFlow<IbanState> = _state.asStateFlow()

    fun insertIban(ibanItem: IbanItem){
        viewModelScope.launch {
            val ibanEntity = ibanItem.toIbanEntity()
            repository.insertIbanInfo(ibanEntity)
        }
    }

    fun deleteIban(ibanItem: IbanItem){
        viewModelScope.launch {
            val ibanEntity = ibanItem.toIbanEntity()
            repository.deleteIbanInfo(ibanEntity)
        }
    }

    fun insertCategory(categoryEntity: CategoryEntity){
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }

    fun deleteCategory(categoryEntity: CategoryEntity){
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }

    fun getAllCategories(){
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _state.update {
                    it.copy(
                        categoryList = categories
                    )
                }
            }
        }
    }

    fun getCategoryById(categoryId : Int){
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryId)
            _state.update {
                it.copy(
                    currentCategory = category
                )
            }
        }
    }

    fun getAllIbans(){
        viewModelScope.launch {
            repository.getAllIbanInfos().collect { ibanList ->
                _state.update {
                    it.copy(
                        ibanList = ibanList
                    )
                }
            }
        }
    }

    fun getAllIbansByCategory(categoryId : Int){
        viewModelScope.launch {
            repository.getIbanInfosByCategory(categoryId).collect { ibanList ->
                _state.update {
                    it.copy(
                        categorizedIbanList = ibanList
                    )
                }
            }
        }
    }

    fun changeCurrentIban(iban : IbanItem){
        _state.update {
            it.copy(
                currentIban = iban
            )
        }
    }

}

data class IbanState(
    val ibanList : List<IbanItem> = emptyList(),
    val categorizedIbanList : List<IbanItem> = emptyList(),
    val currentIban : IbanItem = IbanItem(-1,"","","",-1),
    val categoryList : List<CategoryEntity> = emptyList(),
    val currentCategory : CategoryEntity = CategoryEntity(-1,"")
)