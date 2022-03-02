package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.BuildConfig
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class SetEmployeesUseCase: BaseUseCase<UserEntity, SetEmployeesUseCase.Output, Failures>() {

    sealed class Output {
        object Empty: Output()
    }

    override suspend fun run(input: UserEntity): BaseResult<Output, Failures> {

        val def = CompletableDeferred<BaseResult<Output, Failures>>()
        FirebaseDatabase.getInstance().getReference(BuildConfig.DB_REF_ROOT +
                input.companyId +
                BuildConfig.DB_REF_EMPLOYEES +
                input.id).setValue(input).
        addOnSuccessListener{
             BaseResult.Success(Output.Empty)
        }.addOnFailureListener {
            BaseResult.Failure(Failures.NoNetwork)
        }
        return def.await()
    }
}