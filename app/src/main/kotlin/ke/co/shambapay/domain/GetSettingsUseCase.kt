package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetSettingsUseCase : BaseUseCase<UserEntity?, SettingsEntity, Failures>() {

    override suspend fun run(input: UserEntity?): BaseResult<SettingsEntity, Failures> {

        if (input == null) return BaseResult.Failure(Failures.NotAuthenticated)

        val deferred = CompletableDeferred<BaseResult<SettingsEntity, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getSettings(input.companyId)).get().
        addOnSuccessListener{
            try {
                val settingsEntity: SettingsEntity? = it.getValue(SettingsEntity::class.java)
                if (settingsEntity != null){
                    deferred.complete(BaseResult.Success(settingsEntity))
                } else {
                    deferred.complete(BaseResult.Failure(Failures.WithMessage("Settings not found, Please contact your administrator")))
                }
            } catch (e: Exception){
                deferred.complete(BaseResult.Failure(Failures.WithMessage(e.localizedMessage)))
            }
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }
        return deferred.await()
    }
}