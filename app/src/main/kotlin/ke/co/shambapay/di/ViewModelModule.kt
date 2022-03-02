package ke.co.shambapay.di

import ke.co.shambapay.ui.employees.EmployeeListViewModel
import ke.co.shambapay.ui.login.LoginViewModel
import ke.co.shambapay.ui.login.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { EmployeeListViewModel(get(), get()) }

}
