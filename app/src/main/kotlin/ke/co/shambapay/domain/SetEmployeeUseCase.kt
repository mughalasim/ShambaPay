package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred
import java.util.*

class SetEmployeeUseCase: BaseUseCase<SetEmployeeUseCase.Input, Unit, Failures>() {

    data class Input (val employee: EmployeeEntity, val companyId: String)

    override suspend fun run(input: Input): BaseResult<Unit, Failures> {

        if (input.companyId.isEmpty()) return BaseResult.Failure(Failures.NotAuthenticated)

        val query = if (input.employee.id.isEmpty()) {
            QueryBuilder.getEmployee(input.companyId, UUID.randomUUID().toString())
        } else {
            QueryBuilder.getEmployee(input.companyId, input.employee.id)
        }

        val def = CompletableDeferred<BaseResult<Unit, Failures>>()

        FirebaseDatabase.getInstance().getReference(query).setValue(input.employee).addOnSuccessListener{
            def.complete(BaseResult.Success(Unit))

        }.addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }

        return def.await()
    }
}