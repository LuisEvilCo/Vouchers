@file:JvmName("BalanceDaoTest")
@file:JvmMultifileClass

package checker.util.luis.vouchers.database

import android.support.test.runner.AndroidJUnit4
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.util.LiveDataTestUtil
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class BalanceDaoTest : DbTest() {

    @Test
    @Throws(InterruptedException::class)
    fun insertAndLoad() {
        // creating and inserting a new record
        val balance = BalanceEntity(
            name = "Luis",
            amount = "$99.05"
        )

        inMemoryDb.balanceDao().insert(balance)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        // checking some things
        assertNotNull(readData)
        assertThat(readData.size, equalTo(1))
        assertThat(readData[0].name, equalTo("Luis"))
        assertThat(readData[0].amount, equalTo("$99.05"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertSeveral() {
        var balance = BalanceEntity(
            name = "Luis",
            amount = "$99.05"
        )
        inMemoryDb.balanceDao().insert(balance)

        balance = BalanceEntity(
            name = "Luis A",
            amount = "$99.15"
        )
        inMemoryDb.balanceDao().insert(balance)

        balance = BalanceEntity(
            name = "Luis B",
            amount = "$99.20"
        )
        inMemoryDb.balanceDao().insert(balance)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(3))
        assertThat(readData[0].name, equalTo("Luis"))
        assertThat(readData[1].name, equalTo("Luis A"))
        assertThat(readData[2].name, equalTo("Luis B"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertBatch() {
        val balance1 = BalanceEntity(
            name = "Luis",
            amount = "$99.05"
        )

        val balance2 = BalanceEntity(
            name = "Luis A",
            amount = "$99.15"
        )

        val balance3 = BalanceEntity(
            name = "Luis B",
            amount = "$99.20"
        )
        inMemoryDb.balanceDao().insert(balance1, balance2, balance3)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(3))
        assertThat(readData[0].name, equalTo("Luis"))
        assertThat(readData[1].name, equalTo("Luis A"))
        assertThat(readData[2].name, equalTo("Luis B"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertList() {
        val balanceList = listOf(
            BalanceEntity(
                name = "Luis",
                amount = "$99.05"
            ),
            BalanceEntity(
                name = "Luis A",
                amount = "$99.15"
            ),
            BalanceEntity(
                name = "Luis B",
                amount = "$99.20"
            )
        )

        inMemoryDb.balanceDao().insert(balanceList)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(3))
    }


    @Test
    @Throws(InterruptedException::class)
    fun delete() {
        val balance1 = BalanceEntity(
            name = "Luis",
            amount = "$99.05"
        )

        val balance2 = BalanceEntity(
            name = "Luis A",
            amount = "$99.15"
        )

        // inserting two records
        inMemoryDb.balanceDao().insert(balance1, balance2)

        var readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        // checking those two records
        assertNotNull(readData)
        assertThat(readData.size, equalTo(2))

        // deleting one of them
        inMemoryDb.balanceDao().delete(balance2)

        readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        // checking the remaining record
        assertNotNull(readData)
        assertThat(readData.size, equalTo(1))
        assertThat(readData[0] == balance1, equalTo(true))
    }

    @Test
    @Throws(InterruptedException::class)
    fun deleteAll() {
        val balance1 = BalanceEntity(
            name = "Luis",
            amount = "$99.05"
        )

        val balance2 = BalanceEntity(
            name = "Luis A",
            amount = "$99.15"
        )

        val balance3 = BalanceEntity(
            name = "Luis B",
            amount = "$99.20"
        )

        inMemoryDb.balanceDao().insert(balance1, balance2, balance3)

        var readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        assertThat(readData.size, equalTo(3))

        inMemoryDb.balanceDao().deleteAll()

        readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        assertThat(readData.size, equalTo(0))
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertReplace() {
        val balance = BalanceEntity(
            name = "Luis",
            amount = "$99.05"
        )

        inMemoryDb.balanceDao().insert(balance)
        inMemoryDb.balanceDao().insert(balance)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().allRecords
        )

        assertNotNull(readData)
        // twice the same insert should give us only one answer
        assertThat(readData.size, equalTo(1))
    }

    @Test
    @Throws(InterruptedException::class)
    fun orderByDate() {
        val myTime = "14:10"
        val df = SimpleDateFormat("HH:mm")
        val d = df.parse(myTime)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.YEAR, -2)
        //val newTimeString = df.format(cal.time)

        val balance1 = BalanceEntity(
            name = "Luis",
            amount = "$89.99",
            lastUpdated = cal.time
        )

        cal.add(Calendar.MINUTE, 15)

        val balance2 = BalanceEntity(
            name = "Luis 2",
            amount = "$99.05",
            lastUpdated = cal.time
        )

        cal.add(Calendar.YEAR, 5)

        val balance3 = BalanceEntity(
            name = "Luis 3",
            amount = "$99.35",
            lastUpdated = cal.time
        )

        inMemoryDb.balanceDao().insert(balance1, balance2, balance3)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getDescendantAsync()
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(3))

        assertThat(readData[0].name, equalTo("Luis 3"))
        assertThat(readData[1].name, equalTo("Luis 2"))
        assertThat(readData[2].name, equalTo("Luis"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun getByName() {
        val balance1 = BalanceEntity(
            name = "Luis",
            amount = "$89.99"
        )

        val balance2 = BalanceEntity(
            name = "Luis A",
            amount = "$99.05"
        )

        val balance3 = BalanceEntity(
            name = "Luis AB",
            amount = "$99.35"
        )

        inMemoryDb.balanceDao().insert(balance1, balance2, balance3)

        var readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getByName("Luis")
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(1))

        readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getByName("Luis A")
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(1))

        readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getByName("Luis A%")
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(2))

        readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getByName("Luis%")
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(3))

        readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getByName("NotExisting")
        )

        assertThat(readData.size, equalTo(0))
    }

    @Test
    @Throws(InterruptedException::class)
    fun orderByDateLimit(){
        val myTime = "14:10"
        val df = SimpleDateFormat("HH:mm")
        val d = df.parse(myTime)
        val cal = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.YEAR, -2)
        //val newTimeString = df.format(cal.time)

        val balance1 = BalanceEntity(
            name = "Luis",
            amount = "$89.99",
            lastUpdated = cal.time
        )

        cal.add(Calendar.MINUTE, 15)

        val balance2 = BalanceEntity(
            name = "Luis 2",
            amount = "$99.05",
            lastUpdated = cal.time
        )

        cal.add(Calendar.YEAR, 5)

        val balance3 = BalanceEntity(
            name = "Luis 3",
            amount = "$99.35",
            lastUpdated = cal.time
        )

        cal.add(Calendar.MINUTE,60 * 24)

        val balance4 = BalanceEntity(
            name = "Luis 4",
            amount = "$99.45",
            lastUpdated = cal.time
        )

        inMemoryDb.balanceDao().insert(balance1, balance2, balance3, balance4)

        val readData = LiveDataTestUtil.getValue(
            inMemoryDb.balanceDao().getDescendantAsync(2)
        )

        assertNotNull(readData)
        assertThat(readData.size, equalTo(2))
        assertThat(readData[0].lastUpdated > readData[1].lastUpdated, equalTo(true))
    }
}