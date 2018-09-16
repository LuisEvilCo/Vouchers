package checker.util.luis.vouchers.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import checker.util.luis.vouchers.api.ApiResponse
import checker.util.luis.vouchers.vo.AppExecutors
import checker.util.luis.vouchers.vo.Resource

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread
internal constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> setValue(Resource.success(newData)) }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (!equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    // Manually writing method to support API level 14
    @Suppress("SuspiciousEqualsCombination")
    private fun equals(a: Any?, b: Any): Boolean {
        return a === b || a != null && a == b
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData -> setValue(Resource.loading(newData)) }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            if (response?.isSuccessful == true) {
                appExecutors.diskIO().execute {
                    saveCallResult(processResponse(response)!!)
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(
                            loadFromDb()
                        ) { newData -> setValue(Resource.success(newData)) }
                    }
                }
            } else {
                onFetchFailed()
                result.addSource(
                    dbSource
                ) { newData ->
                    setValue(
                        Resource.error(
                            response?.errorMessage ?: "empty response",
                            newData
                        )
                    )
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    @WorkerThread
    protected fun processResponse(response: ApiResponse<RequestType>): RequestType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @WorkerThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

}
