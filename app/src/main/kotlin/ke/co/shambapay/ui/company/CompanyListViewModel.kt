package ke.co.shambapay.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.domain.GetCompaniesUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel

class CompanyListViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase
) : BaseViewModel() {

    private val _companies = MutableLiveData<List<CompanyEntity>>()
    val companies: LiveData<List<CompanyEntity>> = _companies

    fun getAllCompanies() {
        _state.postValue(BaseState.UpdateUI(true, "Fetching all companies, Please wait..."))

        getCompaniesUseCase.invoke(viewModelScope, Unit){
            it.result(onSuccess = { list ->
                if (list.isEmpty()){
                    _state.postValue(BaseState.UpdateUI(false, "No results found"))
                } else {
                    _state.postValue(BaseState.UpdateUI(false, ""))
                }
                _companies.postValue(list)

            }, onFailure = { failure ->
                handleFailure(failure)
            })
        }
    }

}