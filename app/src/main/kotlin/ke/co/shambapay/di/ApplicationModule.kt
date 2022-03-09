package ke.co.shambapay.di

import ke.co.shambapay.ui.UiGlobalState
import org.koin.dsl.module

val applicationModule = module {

    single { UiGlobalState() }

}