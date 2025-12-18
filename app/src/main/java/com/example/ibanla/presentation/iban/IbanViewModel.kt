package com.example.ibanla.presentation.iban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibanla.domain.model.Category
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.repository.IbanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IbanViewModel @Inject constructor(
    private val repository: IbanRepository
) : ViewModel() {

    init {
        initializeCategoryId()
    }

    fun initializeCategoryId() {
        viewModelScope.launch {
            repository.initializeCategoryId()
        }
    }

    private val _state = MutableStateFlow(IbanState())
    val state: StateFlow<IbanState> = _state.asStateFlow()

    private val _showTick = MutableStateFlow(false)
    val showTick : StateFlow<Boolean> = _showTick.asStateFlow()

    private var timerJob : Job? = null

    fun startCopiedTimer(){
        if (timerJob?.isActive == true) return

        var countDown = 3

        timerJob = viewModelScope.launch {
            _showTick.value = true
            while (isActive){
                delay(1000)
                countDown--
                if (countDown == 0){
                    timerJob?.cancel()
                    _showTick.value = false
                }
            }
        }
    }

    val categorizedIbans: StateFlow<Map<Category, List<IbanItem>>> =
        combine(repository.getAllIbanInfos(), repository.getCategories()) { ibans, categories ->
            categories
                .filter { it.id != 1000 }
                .associateWith { category ->
                ibans.filter {
                    it.categoryId == category.id
                }
            }
                .filterValues {
                    it.isNotEmpty()
                }

        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val myIbans: StateFlow<List<IbanItem>> = repository.getIbanInfosByCategory(1000)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val categories = repository.getCategories().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    val myIbansWithCategory = combine(myIbans, categories) { ibans, cats ->
        ibans.map { iban ->
            val category = cats.find { it.id == iban.categoryId } ?: Category(-1, "")
            iban to category
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun addIban(ibanItem: IbanItem) {
        viewModelScope.launch {
            repository.insertIbanInfo(ibanItem)
               _state.update {
                   it.copy(
                       currentTab = if (ibanItem.categoryId == 1000) IbanTab.MY else IbanTab.OTHER
                   )
               }
        }
    }

    fun onTabSelected(tab : IbanTab){
        _state.update {
            it.copy(
                currentTab = tab
            )
        }
    }

    fun deleteIban(ibanItem: IbanItem) {
        viewModelScope.launch {
            repository.deleteIbanInfo(ibanItem)
        }
    }

    fun updateIban(ibanItem: IbanItem){
        viewModelScope.launch {
            repository.updateIbanInfo(ibanItem)
        }
    }

    fun addCategory(categoryEntity: Category) {
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }

    fun deleteCategory(categoryEntity: Category) {
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }


    fun getCategoryById(categoryId: Int) {
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryId)
            _state.update {
                it.copy(
                    currentCategory = category
                )
            }
        }
    }

    fun getCategoryByName(name: String) {
        viewModelScope.launch {
            repository.getCategoryByName(name)
        }
    }



    fun setCurrentCategory(categoryEntity: Category) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentCategory = categoryEntity
                )
            }
        }
    }

    fun setCurrentIban(ibanItem: IbanItem) {
        _state.update {
            it.copy(
                currentIban = ibanItem
            )
        }
    }

}

data class IbanState(
    val ibanList: List<IbanItem> = emptyList(),
    val categorizedIbanList: List<IbanItem> = emptyList(),
    val currentIban: IbanItem = IbanItem(-1, "", "", "", -1),
    val categoryList: List<Category> = emptyList(),
    val currentCategory: Category = Category(1000, ""),
    val currentTab : IbanTab = IbanTab.MY
)

enum class IbanTab{
    MY,
    OTHER
}