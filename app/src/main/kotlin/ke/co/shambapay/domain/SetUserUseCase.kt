package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.Query
import kotlinx.coroutines.CompletableDeferred

class SetUserUseCase: BaseUseCase<UserEntity, Unit, Failures>() {

    override suspend fun run(input: UserEntity): BaseResult<Unit, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        if (input.id.isEmpty()) return BaseResult.Failure(Failures.WithMessage("User has not been registered"))

        if (input.companyId.isEmpty()) return BaseResult.Failure(Failures.WithMessage("A user cannot exist without a company"))

        val def = CompletableDeferred<BaseResult<Unit, Failures>>()

        FirebaseDatabase.getInstance().getReference(Query.getUser(input.id)).setValue(input).
        addOnSuccessListener{
            def.complete(BaseResult.Success(Unit))
        }.
        addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return def.await()
    }
}