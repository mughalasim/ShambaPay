package ke.co.shambapay.di

import ke.co.shambapay.ui.capture.CaptureViewModel
import ke.co.shambapay.ui.employees.EmployeeListViewModel
import ke.co.shambapay.ui.login.LoginViewModel
import ke.co.shambapay.ui.profile.ProfileViewModel
import ke.co.shambapay.ui.register.RegisterViewModel
import ke.co.shambapay.ui.reports.ReportViewModel
import ke.co.shambapay.ui.settings.SettingsUpdateViewModel
import ke.co.shambapay.ui.upload.UploadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }

    viewModel { RegisterViewModel(get()) }

    viewModel { EmployeeListViewModel(get()) }

    viewModel { UploadViewModel(get()) }

    viewModel { CaptureViewModel(get(), get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { ReportViewModel(get()) }

    viewModel { SettingsUpdateViewModel(get(), get()) }

}
