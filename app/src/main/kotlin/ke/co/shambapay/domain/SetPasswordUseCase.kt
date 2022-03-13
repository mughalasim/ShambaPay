package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class SetPasswordUseCase : BaseUseCase<String, Unit, Failures>() {

    override suspend fun run(input: String): BaseResult<Unit, Failures> {

        val firebaseUser =  FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val deferred = CompletableDeferred<BaseResult<Unit, Failures>>()

        firebaseUser.updatePassword(input).addOnSuccessListener {
            deferred.complete(BaseResult.Success(Unit))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return deferred.await()
    }
}