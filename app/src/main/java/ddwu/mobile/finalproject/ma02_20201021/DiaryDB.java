package ddwu.mobile.finalproject.ma02_20201021;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diary.class}, version = 1)
public abstract class DiaryDB extends RoomDatabase {
    public abstract DiaryDao diaryDao();

    private static volatile DiaryDB INSTANCE;

    static DiaryDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiaryDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                DiaryDB.class, "diary_db.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
