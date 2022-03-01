package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetEmployeesUseCase: BaseUseCase<UserEntity, GetEmployeesUseCase.Output, Failures>() {

    sealed class Output {
        data class List(val list: kotlin.collections.List<EmployeeEntity>): Output()
    }

    override suspend fun run(input: UserEntity): BaseResult<Output, Failures> {

        val def = CompletableDeferred<BaseResult<Output, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getEmployees(input.companyId)).get().
        addOnSuccessListener{
            try {
                BaseResult.Success(Output.List(it.children as List<EmployeeEntity>))
            } catch (e: Exception){
                BaseResult.Failure(Failures.NoNetwork)
            }
        }.addOnFailureListener {
            BaseResult.Failure(Failures.NoNetwork)
        }
        return def.await()
    }
}