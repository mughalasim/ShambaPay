package ke.co.shambapay.di

import ke.co.shambapay.domain.*
import org.koin.dsl.module

val useCaseModule = module {

    single { UploadUseCase(get()) }

    single { GetUserUseCase() }

    single { GetEmployeesUseCase(get()) }

    single { GetWorkUseCase(get()) }

    single { GetSettingsUseCase(get()) }

    single { SetRateUseCase(get()) }

    single { DeleteRateUseCase(get()) }

    single { GetLoginUseCase() }

    single { SetUserUseCase() }

    single { SetEmployeeWorkUseCase(get()) }

    single { SetPasswordUseCase() }

    single { SetPasswordResetUseCase() }

}
