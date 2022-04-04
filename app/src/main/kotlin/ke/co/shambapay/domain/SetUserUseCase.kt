package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class SetUserUseCase : BaseUseCase<UserEntity, Boolean, Failures>() {

    override suspend fun run(input: UserEntity): BaseResult<Boolean, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val entity: UserEntity = input
        val deferredCreate = CompletableDeferred<BaseResult<Boolean, Failures>>()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(input.email, "123456").
        addOnSuccessListener {
            if (it.user != null){
                entity.id = it.user!!.uid
                deferredCreate.complete(BaseResult.Success(true))
            }else{
                deferredCreate.complete(BaseResult.Failure(Failures.WithMessage("Unable to create user")))
            }
        }.addOnFailureListener {
            deferredCreate.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }

        val result = deferredCreate.await()
        if (result is Failures) return result

        val deferred = CompletableDeferred<BaseResult<Boolean, Failures>>()

        FirebaseDatabase.getInstance().getReference(QueryBuilder.geUser(entity.id)).setValue(entity).
        addOnSuccessListener{
            deferred.complete(BaseResult.Success(true))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }

        return deferred.await()
    }
}