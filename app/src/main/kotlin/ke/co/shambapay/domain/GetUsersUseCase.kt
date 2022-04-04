package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred

class GetUsersUseCase(val globalState: UiGlobalState): BaseUseCase<String, List<UserEntity>, Failures>() {

    override suspend fun run(input: String): BaseResult<List<UserEntity>, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        val def = CompletableDeferred<BaseResult<List<UserEntity>, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getUsers()).orderByChild("companyId").equalTo(input) .get().
        addOnSuccessListener{ dataSnapshot ->
            try {
                if (!dataSnapshot.hasChildren()){
                    def.complete(BaseResult.Success(emptyList()))
                } else {
                    val list = dataSnapshot.children.map { data ->
                        data.getValue(UserEntity::class.java)!!
                    }.filter { it.userType != UserType.ADMIN }

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