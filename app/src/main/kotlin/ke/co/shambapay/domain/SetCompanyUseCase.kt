package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.Query
import kotlinx.coroutines.CompletableDeferred

class SetCompanyUseCase: BaseUseCase<String, String, Failures>() {

    override suspend fun run(input: String): BaseResult<String, Failures> {
        
        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        if (input.isEmpty()) return BaseResult.Failure(Failures.WithMessage("Invalid company name"))

        val companyEntity = CompanyEntity().getNewCompany(input)

        val deferred = CompletableDeferred<BaseResult<String, Failures>>()

        FirebaseDatabase.getInstance().getReference(Query.getCompany(companyEntity.settings.companyId)).setValue(companyEntity).
        addOnSuccessListener{
            deferred.complete(BaseResult.Success(companyEntity.settings.companyId))
        }.addOnFailureListener {
            deferred.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return deferred.await()
    }
}