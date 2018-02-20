package checker.util.luis.vouchers.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.UUID;

import checker.util.luis.vouchers.database.dao.BalanceDao;
import checker.util.luis.vouchers.database.entity.BalanceEntity;


// Room seems to be incompatible with the kotlin version of this
// or I'm too dumb
@Database(entities = {BalanceEntity.class}, version = 1)
public abstract class BalanceDatabase extends RoomDatabase {
    private static BalanceDatabase INSTANCE;

    public abstract BalanceDao balanceDao();

    public static BalanceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BalanceEntity.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BalanceDatabase.class, "word_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BalanceDao mDao;

        PopulateDbAsync(BalanceDatabase db) {
            mDao = db.balanceDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteAll();
            BalanceEntity entity = new BalanceEntity( "Luis", "$89.99", "");
            mDao.insert(entity);
            entity = new BalanceEntity( "Luis T", "$85.2", "");
            mDao.insert(entity);
            return null;
        }
    }
}
