package ke.co.shambapay.di

import ke.co.shambapay.domain.*
import org.koin.dsl.module

val useCaseModule = module {

    single { UploadUseCase(get()) }

    single { GetUserUseCase() }

    single { GetEmployeesUseCase(get()) }

    single { SetEmployeeUseCase() }

    single { DeleteEmployeeUseCase() }

    single { GetWorkUseCase(get()) }

    single { GetSettingsUseCase(get()) }

    single { SetRateUseCase(get()) }

    single { DeleteRateUseCase(get()) }

    single { GetLoginUseCase() }

    single { SetUserUseCase() }

    single { SetEmployeeWorkUseCase(get()) }

    single { SetPasswordUseCase() }

    single { SetPasswordResetUseCase() }

    single { GetReportUseCase(get(), get()) }

    single { SetCompanyNameUseCase(get()) }

    single { GetCompaniesUseCase(get()) }

    single { SetCompanyUseCase() }

    single { RegisterUserUseCase() }

    single { GetUsersUseCase(get()) }

    single { DeleteUserAndCompanyUseCase() }

}
