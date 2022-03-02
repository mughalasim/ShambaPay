package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetEmployeesUseCase: BaseUseCase<GetEmployeesUseCase.Input, List<EmployeeEntity>, Failures>() {

    sealed class Input{
        data class Filtered(val userEntity: UserEntity, val filterText: String): Input()
    }

    override suspend fun run(input: Input): BaseResult<List<EmployeeEntity>, Failures> {

        val def = CompletableDeferred<BaseResult<List<EmployeeEntity>, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getEmployees((input as Input.Filtered).userEntity.companyId)).get().
        addOnSuccessListener{ dataSnapshot ->
            try {
                if (!dataSnapshot.hasChildren()){
                    def.complete(BaseResult.Success(emptyList()))
                } else {
                    val filter = input.filterText.lowercase()
                    val list = dataSnapshot.children.map { data ->
                        data.getValue(EmployeeEntity::class.java)!!
                    }.filter {
                        (it.firstName.lowercase().contains(filter) ||
                        it.lastName.lowercase().contains(filter) ||
                        it.phone.toString().lowercase().contains(filter))
                    }
                    def.complete(BaseResult.Success(list))
                }
            } catch (e: Exception){
                def.complete(BaseResult.Failure(Failures.WithMessage("There is an issue with one of the data sets: " + e.localizedMessage)))
            }
        }.addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }
        return def.await()
    }
}