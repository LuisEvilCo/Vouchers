package checker.util.luis.vouchers.database


import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry

import org.junit.After
import org.junit.Before

abstract class DbTest {
    protected lateinit var inMemoryDb: BalanceDatabase

    @Before
    fun initDb() {
        inMemoryDb = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            BalanceDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        inMemoryDb.close()
    }
}
