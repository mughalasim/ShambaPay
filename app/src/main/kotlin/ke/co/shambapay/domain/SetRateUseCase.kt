package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.JobType
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class SetRateUseCase(val globalState: UiGlobalState) : BaseUseCase<JobRateEntity, Unit, Failures>() {

    override suspend fun run(input: JobRateEntity): BaseResult<Unit, Failures> {

        if (globalState.user == null)
            return BaseResult.Failure(Failures.NotAuthenticated)

        if (input.jobType == JobType.CASUAL_WORK && input.rate != 1.0)
            return BaseResult.Failure(Failures.WithMessage("Casual work can only have a rate set to 1"))

        val deferred = CompletableDeferred<BaseResult<Unit, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getRates(globalState.user!!.companyId, input.rateId)).setValue(input).
        addOnSuccessListener{
            deferred.complete(BaseResult.Success(Unit))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }
        return deferred.await()
    }
}