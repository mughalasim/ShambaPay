package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import kotlinx.coroutines.CompletableDeferred

class SetUserUseCase : BaseUseCase<SetUserUseCase.Input, Boolean, Failures>() {

    // TODO - Split this into SetCompanyUseCase and SetSupervisorUseCase

    data class Input(val userEntity: UserEntity, val companyName: String)

    override suspend fun run(input: Input): BaseResult<Boolean, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        val entity: UserEntity = input.userEntity

        if (entity.userType == UserType.OWNER && input.companyName.isEmpty()) return BaseResult.Failure(Failures.WithMessage("Company name cannot be empty"))

        if (entity.id.isEmpty()) {
            val deferredCreate = CompletableDeferred<BaseResult<Boolean, Failures>>()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(entity.email, "123456")
                .addOnSuccessListener {
                    if (it.user != null) {
                        entity.id = it.user!!.uid
                        deferredCreate.complete(BaseResult.Success(true))
                    } else {
                        deferredCreate.complete(BaseResult.Failure(Failures.WithMessage("Unable to create user")))
                    }
                }.addOnFailureListener {
                    deferredCreate.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
                }

            if (deferredCreate.await() is Failures) return deferredCreate.await()

            if (entity.userType == UserType.OWNER){

                val companyEntity = CompanyEntity().getNewCompany(input.companyName)

                val deferred = CompletableDeferred<BaseResult<Boolean, Failures>>()

                FirebaseDatabase.getInstance().getReference(QueryBuilder.getCompany(companyEntity.Settings.companyId)).setValue(companyEntity).
                addOnSuccessListener{
                    deferred.complete(BaseResult.Success(true))
                }.addOnFailureListener {
                    deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
                }

                if (deferred.await() is Failures) return deferred.await()
            }

        }

        val deferred = CompletableDeferred<BaseResult<Boolean, Failures>>()

        FirebaseDatabase.getInstance().getReference(QueryBuilder.geUser(entity.id)).setValue(entity).
        addOnSuccessListener{
            deferred.complete(BaseResult.Success(true))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }

        return deferred.await()
    }

    fun deleteUserAndCompany(email: String, companyId: String){

    }
}