package otus.homework.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null
    val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job + CoroutineName("CatsCoroutine"))

    fun onInitComplete() {
        scope.launch {
            try {
                val fact = catsService.getCatFact()
                _catsView?.populate(fact)
            }
            catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException -> {
                        _catsView?.showServerErrorToast()
                    }
                    else -> {
                        _catsView?.showToast(e.message.toString())
                        CrashMonitor.trackWarning()
                    }
                }
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
        job.cancel()
    }
}