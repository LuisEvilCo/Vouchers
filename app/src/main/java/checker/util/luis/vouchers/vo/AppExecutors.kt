package checker.util.luis.vouchers.vo

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(
    private val diskIO: Executor = Executors.newSingleThreadExecutor(),
    private val networkIO: Executor = Executors.newFixedThreadPool(3),
    private val mainThread: Executor = MainThreadExecutor()
) {

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO(): Executor {
        return networkIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    companion object {
        private var INSTANCE: AppExecutors? = null

        @JvmStatic
        fun getInstance(): AppExecutors {
            if (INSTANCE == null) {
                synchronized(AppExecutors::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AppExecutors()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadExecutor = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            mainThreadExecutor.post(command)
        }
    }
}