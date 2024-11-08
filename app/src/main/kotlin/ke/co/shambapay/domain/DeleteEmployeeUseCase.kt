package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.Query
import kotlinx.coroutines.CompletableDeferred

class DeleteEmployeeUseCase: BaseUseCase<DeleteEmployeeUseCase.Input, Unit, Failures>() {

    data class Input (val employee: EmployeeEntity, val companyId: String)

    override suspend fun run(input: Input): BaseResult<Unit, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        if (input.companyId.isEmpty()) return BaseResult.Failure(Failures.WithMessage("An employee cannot be deleted from an unknown company"))

        val def = CompletableDeferred<BaseResult<Unit, Failures>>()

        FirebaseDatabase.getInstance().
        getReference(Query.getEmployee(input.companyId, input.employee.id)).
        removeValue().
        addOnSuccessListener{
            def.complete(BaseResult.Success(Unit))
        }.
        addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return def.await()
    }
}