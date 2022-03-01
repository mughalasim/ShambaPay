package ke.co.shambapay.domain.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in Input, out successOutput, out failOutput> {

    abstract suspend fun run(input: Input): BaseResult<successOutput, failOutput>

    open operator fun invoke (
        scope: CoroutineScope,
        input: Input,
        onResult: (BaseResult<successOutput, failOutput>) -> Unit = {}
    ) {
        scope.launch {
            val job = withContext(Dispatchers.IO) { run(input) }
            withContext(scope.coroutineContext) { onResult(job) }
        }
    }
}
