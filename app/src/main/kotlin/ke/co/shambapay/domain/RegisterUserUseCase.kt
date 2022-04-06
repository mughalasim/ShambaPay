package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.utils.isInvalidEmail
import kotlinx.coroutines.CompletableDeferred

class RegisterUserUseCase: BaseUseCase<String, String, Failures>() {

    override suspend fun run(input: String): BaseResult<String, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        if (input.isEmpty() || input.isInvalidEmail()) return BaseResult.Failure(Failures.WithMessage("Invalid email"))

        val deferredCreate = CompletableDeferred<BaseResult<String, Failures>>()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(input, "123456")
            .addOnSuccessListener {
                if (it.user != null) {
                    deferredCreate.complete(BaseResult.Success(it.user!!.uid))
                } else {
                    deferredCreate.complete(BaseResult.Failure(Failures.WithMessage("Unable to create user")))
                }
            }.addOnFailureListener {
                deferredCreate.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
            }

        return deferredCreate.await()
    }
}