package namba.wallet.nambaone.common

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class AppCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
}

fun CoroutineScope.invokeOnCancelled(cancellationHandler: (Throwable?) -> Unit) {
    coroutineContext[Job]?.invokeOnCompletion(cancellationHandler)
}