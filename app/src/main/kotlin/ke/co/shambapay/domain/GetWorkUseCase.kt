package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class GetWorkUseCase: BaseUseCase<GetWorkUseCase.Input, GetWorkUseCase.Output, Failures>() {

    sealed class Output {
        data class List(val list: kotlin.collections.List<WorkEntity>): Output()
    }

    data class Input(val companyId: String, val employeeId: String)


    override suspend fun run(input: Input): BaseResult<Output, Failures> {

        val def = CompletableDeferred<BaseResult<Output, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getWork(input.companyId, input.employeeId)).get().
        addOnSuccessListener{
            try {
                BaseResult.Success(Output.List(it.children as List<WorkEntity>))
            } catch (e: Exception){
                BaseResult.Failure(Failures.NoNetwork)
            }
        }.addOnFailureListener {
            BaseResult.Failure(Failures.NoNetwork)
        }
        return def.await()

    }
}