package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class DeleteRateUseCase(val globalState: UiGlobalState) : BaseUseCase<String, Unit, Failures>() {

    override suspend fun run(input: String): BaseResult<Unit, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        val deferred = CompletableDeferred<BaseResult<Unit, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getRates(globalState.user!!.companyId, input)).removeValue().
        addOnSuccessListener{
            deferred.complete(BaseResult.Success(Unit))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }
        return deferred.await()
    }
}