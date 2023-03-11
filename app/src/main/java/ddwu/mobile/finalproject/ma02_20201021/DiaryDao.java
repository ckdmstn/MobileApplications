package ddwu.mobile.finalproject.ma02_20201021;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface DiaryDao {
    @Insert
    Single<Long> insertDiary(Diary diary);

    @Query("UPDATE diary SET title = :title, content = :content WHERE address = :address")
    Completable updateDiary(String title, String address, String content);

    @Query("DELETE FROM diary WHERE title = :title and address = :address")
    Completable deleteDiary(String title, String address);

    @Query("UPDATE diary SET dateTime = :dateTime WHERE address = :address")
    Completable updateDateTime(String dateTime, String address);

    @Query("SELECT * FROM diary")
    Flowable<List<Diary>> getAllDiary();

    @Query("SELECT * FROM diary WHERE title LIKE :title")
    Flowable<List<Diary>> searchByTitle(String title);

    @Query("SELECT * FROM diary WHERE title = :title and address = :address")
    Single<Diary> getDiary(String title, String address);

}
