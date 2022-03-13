package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class GetEmployeesUseCase(val globalState: UiGlobalState): BaseUseCase<String?, List<EmployeeEntity>, Failures>() {

    override suspend fun run(input: String?): BaseResult<List<EmployeeEntity>, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        val def = CompletableDeferred<BaseResult<List<EmployeeEntity>, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getEmployees(globalState.user!!.companyId)).get().
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
                    if (!input.isNullOrEmpty()){
                        val filter = input.lowercase()
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
            def.complete(BaseResult.Failure(Failures.WithMessage("${it.localizedMessage}")))
        }
        return def.await()
    }
}