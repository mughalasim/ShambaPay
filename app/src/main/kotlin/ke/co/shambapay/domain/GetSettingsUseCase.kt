package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.BuildConfig
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetSettingsUseCase : BaseUseCase<UserEntity, SettingsEntity, Failures>() {

    override suspend fun run(input: UserEntity): BaseResult<SettingsEntity, Failures> {

        val completableDeferred = CompletableDeferred<BaseResult<SettingsEntity, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getSettings(input.companyId)).get().
        addOnSuccessListener{
            try {
                BaseResult.Success(it.getValue(SettingsEntity::class.java))
            } catch (e: Exception){
                BaseResult.Failure(Failures.NotFound)
            }
        }.addOnFailureListener {
            BaseResult.Failure(Failures.NoNetwork)
        }
        return completableDeferred.await()
    }
}