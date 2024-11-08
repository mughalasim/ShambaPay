package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.Query
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class GetSettingsUseCase(val globalState: UiGlobalState) : BaseUseCase<Unit, SettingsEntity, Failures>() {

    override suspend fun run(input: Unit): BaseResult<SettingsEntity, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val deferred = CompletableDeferred<BaseResult<SettingsEntity, Failures>>()
        FirebaseDatabase.getInstance().getReference(Query.getSettings(globalState.user!!.companyId)).get().
        addOnSuccessListener{
            try {
                val settingsEntity: SettingsEntity? = it.getValue(SettingsEntity::class.java)
                if (settingsEntity != null){
                    deferred.complete(BaseResult.Success(settingsEntity))
                } else {
                    deferred.complete(BaseResult.Failure(Failures.WithMessage("Settings not found, Please contact your administrator")))
                }
            } catch (e: Exception){
                FirebaseAuth.getInstance().signOut()
                deferred.complete(BaseResult.Failure(Failures.WithMessage(e.localizedMessage)))
            }
        }.addOnFailureListener {
            FirebaseAuth.getInstance().signOut()
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }
        return deferred.await()
    }
}