package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred
import java.io.InputStream

class SetEmployeeWorkUseCase(private val globalState: UiGlobalState) : BaseUseCase<SetEmployeeWorkUseCase.Input, Unit, Failures>() {

    sealed class Input {
        data class Details(val workEntity: WorkEntity, val employeeId: String): Input()
    }

    override suspend fun run(input: Input): BaseResult<Unit, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        input as Input.Details

        val deferred = CompletableDeferred<BaseResult<Unit, Failures>>()

        FirebaseDatabase.getInstance().getReference(QueryBuilder.setWork(globalState.user!!.companyId, input.employeeId)).setValue(input.workEntity).
        addOnSuccessListener{
            deferred.complete(BaseResult.Success(Unit))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return deferred.await()
    }
}