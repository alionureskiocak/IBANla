package com.example.ibanla.presentation.iban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibanla.data.mappers.toIbanEntity
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.repository.IbanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    val categorizedIbans: StateFlow<Map<CategoryEntity, List<IbanItem>>> =
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
            val category = cats.find { it.id == iban.categoryId } ?: CategoryEntity(-1, "")
            iban to category
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun addIban(ibanItem: IbanItem) {
        viewModelScope.launch {
            val ibanEntity = ibanItem.toIbanEntity()
            repository.insertIbanInfo(ibanEntity)
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
            val ibanEntity = ibanItem.toIbanEntity()
            repository.deleteIbanInfo(ibanEntity)
        }
    }

    fun addCategory(categoryEntity: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }

    fun deleteCategory(categoryEntity: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }

    fun getAllCategories() {
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

    fun getAllIbans() {
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

    fun getAllIbansByCategory(categoryId: Int) {
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

    fun setCurrentCategory(name: String) {
        viewModelScope.launch {
            val category = repository.getCategoryByName(name)
            category?.let {
                _state.update {
                    it.copy(
                        currentCategory = category
                    )
                }
            }
        }


    }

    fun changeCurrentIban(iban: IbanItem) {
        _state.update {
            it.copy(
                currentIban = iban
            )
        }
    }

}

data class IbanState(
    val ibanList: List<IbanItem> = emptyList(),
    val categorizedIbanList: List<IbanItem> = emptyList(),
    val currentIban: IbanItem = IbanItem(-1, "", "", "", -1),
    val categoryList: List<CategoryEntity> = emptyList(),
    val currentCategory: CategoryEntity = CategoryEntity(1000, ""),
    val currentTab : IbanTab = IbanTab.MY
)

enum class IbanTab{
    MY,
    OTHER
}