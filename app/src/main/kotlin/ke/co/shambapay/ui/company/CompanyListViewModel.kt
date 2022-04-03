package ke.co.shambapay.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetCompaniesUseCase
import ke.co.shambapay.domain.base.BaseState

class CompanyListViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase
) : ViewModel() {

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    private val _data = MutableLiveData<List<CompanyEntity>>()
    val data: LiveData<List<CompanyEntity>> = _data


    fun getRecyclerData() {
        _state.postValue(BaseState.UpdateUI(true, "Fetching all companies, Please wait..."))

        getCompaniesUseCase.invoke(viewModelScope, Unit){
            it.result(onSuccess = { list ->
                if (list.isEmpty()){
                    _state.postValue(BaseState.UpdateUI(false, "No results found"))
                } else {
                    _state.postValue(BaseState.UpdateUI(false, ""))
                }
                _data.postValue(list)

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {
                        _state.postValue(BaseState.UpdateUI(false, failure.message))
                    }

                    else ->{
                        _state.postValue(BaseState.UpdateUI(false, "Unknown error when authenticating, please check back later"))
                    }
                }
            })
        }
    }

}