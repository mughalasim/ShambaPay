package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseInput
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetLoginUseCase (
    val getUserUseCase: GetUserUseCase
    ) : BaseUseCase<GetLoginUseCase.Input, UserEntity, Failures>() {

    data class Input(val email: String, val password: String)

    override suspend fun run(input: Input): BaseResult<UserEntity, Failures> {

        val completableLogin = CompletableDeferred<Failures?>()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(input.email, input.password).
        addOnSuccessListener {
               completableLogin.complete(null)
        }.
        addOnFailureListener {
            completableLogin.complete(Failures.NotAuthenticated)
        }
        val result = completableLogin.await()
        if (result!=null) return BaseResult.Failure(result)


        val completableGetUser = CompletableDeferred<BaseResult<UserEntity, Failures>>()
        getUserUseCase.run(BaseInput.EmptyInput).result(
            onSuccess = {
                  completableGetUser.complete(BaseResult.Success(it))
            },
            onFailure = {
                completableGetUser.complete(BaseResult.Failure(it))
            }
        )
        return completableGetUser.await()
    }
}