package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.BuildConfig
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseInput
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetUserUseCase : BaseUseCase<BaseInput, UserEntity, Failures>() {

    override suspend fun run(input: BaseInput): BaseResult<UserEntity, Failures> {

        val user = FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val completableDeferred = CompletableDeferred<BaseResult<UserEntity, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.geUser(user.uid)).get().
        addOnSuccessListener{
            try {
                BaseResult.Success(it.getValue(UserEntity::class.java))
            } catch (e: Exception){
                BaseResult.Failure(Failures.NotFound)
            }
        }.addOnFailureListener {
            BaseResult.Failure(Failures.NoNetwork)
        }
        return completableDeferred.await()
    }
}