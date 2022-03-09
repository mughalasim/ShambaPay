package ke.co.shambapay.ui

import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity

class UiGlobalState {

    var user: UserEntity? = null
    var settings: SettingsEntity? = null

    fun clear(){
        user = null
        settings = null
    }

}