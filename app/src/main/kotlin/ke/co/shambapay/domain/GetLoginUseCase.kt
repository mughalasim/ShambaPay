package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import kotlinx.coroutines.CompletableDeferred

class GetLoginUseCase : BaseUseCase<GetLoginUseCase.Input, Boolean, Failures>() {

    data class Input(val email: String, val password: String)

    override suspend fun run(input: Input): BaseResult<Boolean, Failures> {

        val deferred = CompletableDeferred<BaseResult<Boolean, Failures>>()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(input.email, input.password).
        addOnSuccessListener {
               deferred.complete(BaseResult.Success(true))
        }.
        addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return deferred.await()
    }
}