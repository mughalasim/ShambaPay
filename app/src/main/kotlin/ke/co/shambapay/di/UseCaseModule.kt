package ke.co.shambapay.di

import ke.co.shambapay.domain.*
import org.koin.dsl.module

val useCaseModule = module {

    single { PostCSVUseCase() }
    single { GetUserUseCase() }
    single { GetEmployeesUseCase() }
    single { GetWorkUseCase() }
    single { GetSettingsUseCase() }
    single { GetLoginUseCase(get()) }

}
