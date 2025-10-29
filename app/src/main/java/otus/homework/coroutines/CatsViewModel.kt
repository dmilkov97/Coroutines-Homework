package otus.homework.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel

class CatsViewModel(
    private val catsService: CatsService,
    private val catsServicePics: CatsServicePics
) : ViewModel() {
    private var _catsView: ICatsView? = null
    private val _result = MutableLiveData<Result<Fact>>()
    val result: LiveData<Result<Fact>> = _result

    fun onInitComplete() {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            CrashMonitor.trackWarning(throwable)
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                val factDeferred = async { catsService.getCatFact() }
                val imageDeferred = async { catsServicePics.getCatPicUrl() }

                val fact = factDeferred.await()
                val pic = imageDeferred.await()
                val url = pic.firstOrNull()?.url
                fact.url = url.toString()
                _catsView?.populate(fact)
                _result.value = Result.Success(fact)
            }
            catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> {
                        CrashMonitor.trackWarning(e)
                        _result.value = Result.Error(e)
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
    }

    override fun onCleared() {
        super.onCleared()
        detachView()
    }

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val exception: Throwable) : Result<Nothing>()
    }
}