package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseInput
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.QueryBuilder
import kotlinx.coroutines.CompletableDeferred

class GetUserUseCase : BaseUseCase<BaseInput, UserEntity, Failures>() {

    override suspend fun run(input: BaseInput): BaseResult<UserEntity, Failures> {

        val user = FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val deferred = CompletableDeferred<BaseResult<UserEntity, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getUser(user.uid)).get().
        addOnSuccessListener{
            try {
                val userEntity: UserEntity? = it.getValue(UserEntity::class.java)
                if (userEntity != null){
                    deferred.complete(BaseResult.Success(userEntity))
                } else {
                    deferred.complete(BaseResult.Failure(Failures.WithMessage("User not found")))
                }
            } catch (e: Exception){
                FirebaseAuth.getInstance().signOut()
                deferred.complete(BaseResult.Failure(Failures.WithMessage(e.localizedMessage ?: "")))
            }
        }.addOnFailureListener {
            FirebaseAuth.getInstance().signOut()
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }
        return deferred.await()
    }
}