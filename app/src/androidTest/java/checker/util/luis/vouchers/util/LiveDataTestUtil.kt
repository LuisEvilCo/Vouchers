package checker.util.luis.vouchers.util


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertThat
import org.hamcrest.core.IsEqual.equalTo

object LiveDataTestUtil {

    @Suppress("UNCHECKED_CAST")
    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T {

        val data = arrayOfNulls<Any>(1) // equivalent to Object[] data = new Object[1];
        val latch = CountDownLatch(1)

        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                assertThat(liveData.hasActiveObservers(), equalTo(true))
                liveData.removeObserver(this)
                assertThat(liveData.hasActiveObservers(), equalTo(false))
            }
        }

        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as T
    }
}
