package ke.co.shambapay.di

import ke.co.shambapay.ui.capture.CaptureViewModel
import ke.co.shambapay.ui.company.CompanyListViewModel
import ke.co.shambapay.ui.employees.EmployeeListViewModel
import ke.co.shambapay.ui.employees.EmployeeUpdateViewModel
import ke.co.shambapay.ui.login.LoginViewModel
import ke.co.shambapay.ui.profile.ProfileViewModel
import ke.co.shambapay.ui.reports.ReportViewModel
import ke.co.shambapay.ui.settings.SettingsUpdateViewModel
import ke.co.shambapay.ui.settings.SettingsViewModel
import ke.co.shambapay.ui.sms.BulkSMSViewModel
import ke.co.shambapay.ui.upload.UploadViewModel
import ke.co.shambapay.ui.user.UserListViewModel
import ke.co.shambapay.ui.user.UserUpdateViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }

    viewModel { UserUpdateViewModel(get(), get(), get(), get()) }

    viewModel { EmployeeListViewModel(get()) }

    viewModel { EmployeeUpdateViewModel(get(), get()) }

    viewModel { UploadViewModel(get()) }

    viewModel { CaptureViewModel(get(), get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { ReportViewModel(get()) }

    viewModel { SettingsUpdateViewModel(get(), get()) }

    viewModel { SettingsViewModel(get()) }

    viewModel { CompanyListViewModel(get()) }

    viewModel { UserListViewModel(get(), get()) }

    viewModel { BulkSMSViewModel(get()) }

}
