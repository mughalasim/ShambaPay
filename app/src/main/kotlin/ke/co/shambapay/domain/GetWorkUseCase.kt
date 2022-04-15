package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.Query
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class GetWorkUseCase(val globalState: UiGlobalState): BaseUseCase<GetWorkUseCase.Input, GetWorkUseCase.Output, Failures>() {

    sealed class Output {
        data class List(val list: kotlin.collections.List<WorkEntity>): Output()
    }

    data class Input(val employeeId: String, val year: Int, val month: Int)

    override suspend fun run(input: Input): BaseResult<Output, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val def = CompletableDeferred<BaseResult<Output, Failures>>()
        FirebaseDatabase.getInstance().getReference(Query.getWork(globalState.user!!.companyId)).get().
        addOnSuccessListener{
            try {
                val list = if (it.hasChildren()) it.children.filterIsInstance<WorkEntity>() else emptyList()
                BaseResult.Success(Output.List(list))
            } catch (e: Exception){
                BaseResult.Failure(Failures.WithMessage("" + e.localizedMessage))
            }
        }.addOnFailureListener {
            BaseResult.Failure(Failures.WithMessage("" + it.localizedMessage))
        }
        return def.await()

    }
}