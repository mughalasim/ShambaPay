package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class GetCompaniesUseCase(val globalState: UiGlobalState): BaseUseCase<Unit, List<CompanyEntity>, Failures>() {

    override suspend fun run(input: Unit): BaseResult<List<CompanyEntity>, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        val def = CompletableDeferred<BaseResult<List<CompanyEntity>, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getCompanies()).get().
        addOnSuccessListener{ dataSnapshot ->
            try {
                if (!dataSnapshot.hasChildren()){
                    def.complete(BaseResult.Success(emptyList()))
                } else {
                    val list = dataSnapshot.children.map { data ->
                        data.getValue(CompanyEntity::class.java)!!.apply {
                            isDefault = globalState.settings!!.companyId == this.Settings.companyId
                        }
                    }
                    def.complete(BaseResult.Success(list))
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