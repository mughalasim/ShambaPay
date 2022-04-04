package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class GetEmployeesUseCase(val globalState: UiGlobalState): BaseUseCase<GetEmployeesUseCase.Input, List<EmployeeEntity>, Failures>() {

    data class Input(val companyId: String? = null, val filter: String)

    override suspend fun run(input: Input): BaseResult<List<EmployeeEntity>, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        val query = if(input.companyId.isNullOrBlank()) QueryBuilder.getEmployees(globalState.user!!.companyId) else QueryBuilder.getEmployees(input.companyId)

        val def = CompletableDeferred<BaseResult<List<EmployeeEntity>, Failures>>()
        FirebaseDatabase.getInstance().getReference(query).get().
        addOnSuccessListener{ dataSnapshot ->
            try {
                if (!dataSnapshot.hasChildren()){
                    def.complete(BaseResult.Success(emptyList()))
                } else {
                    val list = dataSnapshot.children.map { data ->
                        data.getValue(EmployeeEntity::class.java)!!
                    }.sortedBy {
                        it.firstName
                    }
                    if (input.filter.isNotEmpty()){
                        val filter = input.filter.lowercase()
                        def.complete(BaseResult.Success(list.filter {
                            it.firstName.contains(filter, true)
                        }))
                    } else {
                        def.complete(BaseResult.Success(list))
                    }
                }
            } catch (e: Exception){
                def.complete(BaseResult.Failure(Failures.WithMessage("There is an issue with one of the data sets: " + e.localizedMessage)))
            }
        }.addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }
        return def.await()
    }
}