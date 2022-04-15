package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.Query
import kotlinx.coroutines.CompletableDeferred

class DeleteUserAndCompanyUseCase: BaseUseCase<UserEntity, Unit, Failures>() {

    override suspend fun run(input: UserEntity): BaseResult<Unit, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        if (input.id.isEmpty()) return BaseResult.Failure(Failures.WithMessage("Invalid user cannot be deleted"))

        if (input.companyId.isEmpty()) return BaseResult.Failure(Failures.WithMessage("An employee cannot be deleted from an unknown company"))

        val def1 = CompletableDeferred<BaseResult<Unit, Failures>>()
        FirebaseDatabase.getInstance().getReference(Query.getUser(input.id)).removeValue().
        addOnSuccessListener{
            def1.complete(BaseResult.Success(Unit))
        }.
        addOnFailureListener {
            def1.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }
        val response = def1.await()

        if (response is BaseResult.Failure) return response

        if (input.userType == UserType.OWNER){

            val def2 = CompletableDeferred<BaseResult<Unit, Failures>>()

            FirebaseDatabase.getInstance().getReference(Query.getCompany(input.companyId)).removeValue().
            addOnSuccessListener{
                def2.complete(BaseResult.Success(Unit))
            }.
            addOnFailureListener {
                def2.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
            }

            return def2.await()

        } else {
            return response
        }
    }
}