package checker.util.luis.vouchers.database;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import checker.util.luis.vouchers.database.entity.BalanceEntity;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class BalanceDaoTest {
    private BalanceDatabase mDatabase;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getTargetContext();

         mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                BalanceDatabase.class
        ).build();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void insertAndGetBalance() {
        // Inserting Balance registry
        BalanceEntity mBalance = new BalanceEntity( UUID.randomUUID(),"Luis", "$11.23");
        mDatabase.balanceDao().insert(mBalance);

        LiveData<List<BalanceEntity>> data = mDatabase.balanceDao().getAllRecords();

        assertThat(data.hasActiveObservers(), equalTo(false));

        data.observe(this, new Observer<List<BalanceEntity>>() {
            @Override
            public void onChanged(@Nullable List<BalanceEntity> balanceEntities) {

            }
        });


        assertNotNull(data);
        assertThat(data.getValue().size(), equalTo(1));
    }
}
