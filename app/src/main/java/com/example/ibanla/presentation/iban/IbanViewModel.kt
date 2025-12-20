package com.example.ibanla.presentation.iban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibanla.domain.model.Category
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.repository.IbanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val myIbansFlow = repository.getIbanInfosByCategory(1000)

    private val categoriesFlow = repository.getCategories()

    private val allIbansFlow = repository.getAllIbanInfos()

   private val repositoryState : StateFlow<IbanUiState> =
        combine(
            myIbansFlow,
            categoriesFlow,
            allIbansFlow
        ){ myIbans, categories, allIbans ->

            val categorized = categories
                .filter { it.id != 1000 }
                .associateWith { category ->
                    allIbans.filter { it.categoryId == category.id}
                }
                .filterValues { it.isNotEmpty() }

            IbanUiState(
                myIbans,categorized,categories
            )

        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), IbanUiState()
        )

    private val _uiState = MutableStateFlow(IbanUiState())

    val state : StateFlow<IbanUiState> =
        combine(
            repositoryState,
            _uiState
        ) { repo, ui ->
            repo.copy(
                currentIban = ui.currentIban,
                currentTab = ui.currentTab,
                currentCategory = ui.currentCategory,
                showTick = ui.showTick
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), IbanUiState()
        )



    private val _effect = MutableSharedFlow<IbanEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event : IbanEvent){
        when(event){
            is IbanEvent.CopyIban -> handleCopy(event.iban)
            is IbanEvent.TabSelected -> handleTabSelected(event.tab)
            is IbanEvent.IbanSelected -> handleIbanSelected(event.iban,event.category)
            is IbanEvent.CategorySelected -> handleCategorySelected(event.category)
            is IbanEvent.AddIban -> addIban(event.iban)
            is IbanEvent.UpdateIban -> updateIban(event.iban)
            is IbanEvent.DeleteIban -> deleteIban(event.iban)

            is IbanEvent.AddCategory -> addCategory(event.category)
        }
    }

    private var timerJob : Job? = null

    fun startCopiedTimer(){
        if (timerJob?.isActive == true) return

        var countDown = 3

        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(showTick = true) }
            while (isActive){
                delay(1000)
                countDown--
                if (countDown == 0){
                    timerJob?.cancel()
                    _uiState.update { it.copy(showTick = false) }
                }
            }
        }
    }




    private fun handleCopy(iban : String){
        viewModelScope.launch {
            _effect.emit(IbanEffect.ShowToast("Copied"))
            _effect.emit(IbanEffect.StartCopyTick)
            startCopiedTimer()
        }
    }

    private fun handleTabSelected(tab : IbanTab){
        _uiState.update { it.copy(currentTab = tab) }
    }

    private fun handleIbanSelected(
        iban : IbanItem,
        category : Category
    ){
        _uiState.update {
            it.copy(
                currentIban = iban,
                currentCategory = category
            )
        }
    }

    private fun handleCategorySelected(
        category: Category
    ){
        _uiState.update {
            it.copy(
                currentCategory = category
            )
        }
    }

    private fun addIban(ibanItem: IbanItem) {
        viewModelScope.launch {
            repository.insertIbanInfo(ibanItem)
               _uiState.update {
                   it.copy(
                       currentTab = if (ibanItem.categoryId == 1000) IbanTab.MY else IbanTab.OTHER
                   )
               }
        }
    }

    private fun deleteIban(ibanItem: IbanItem) {
        viewModelScope.launch {
            repository.deleteIbanInfo(ibanItem)
        }
    }

    private fun updateIban(ibanItem: IbanItem){
        viewModelScope.launch {
            repository.updateIbanInfo(ibanItem)
        }
    }

    private fun addCategory(categoryEntity: Category) {
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }

    private fun deleteCategory(categoryEntity: Category) {
        viewModelScope.launch {
            repository.insertCategory(categoryEntity)
        }
    }


    fun getCategoryById(categoryId: Int) {
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryId)
            _uiState.update {
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

}

data class IbanUiState(
    val myIbans : List<IbanItem> = emptyList(),
    val categorizedIbans : Map<Category, List<IbanItem>> = emptyMap(),
    val categories : List<Category> = emptyList(),
    val currentTab : IbanTab = IbanTab.MY,
    val currentIban: IbanItem = IbanItem(-1, "", "", "", -1),
    val currentCategory: Category = Category(1000, ""),
    val showTick : Boolean = false
)

enum class IbanTab{
    MY,
    OTHER
}