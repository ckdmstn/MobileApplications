package ddwu.mobile.finalproject.ma02_20201021;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyAdapter.AlarmBtnClickListener {

    public static final String TAG = "20201021_ma";

    ListView listView;
    MyAdapter adapter;
    ArrayList<Diary> diaryList = null;


    DiaryDB diaryDB;
    DiaryDao diaryDao;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        diaryList = new ArrayList<Diary>();
        adapter = new MyAdapter(this, R.layout.custom_adapter_view, diaryList, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Diary diary = (Diary) listView.getAdapter().getItem(i);

                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                intent.putExtra("title", diary.title);
                intent.putExtra("address", diary.address);
                intent.putExtra("content", diary.content);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Diary diary = (Diary) listView.getAdapter().getItem(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(diary.title + "을(를) 정말 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Completable deleteResult = diaryDao.deleteDiary(diary.title, diary.address);
                                mDisposable.add(
                                        deleteResult.subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(() -> Log.d(TAG, "Delete success: "),
                                                        throwable -> Log.d(TAG, "error"))
                                );
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();

                return true;
            }
        });


        diaryDB = DiaryDB.getDatabase(this);
        diaryDao = diaryDB.diaryDao();

        Flowable<List<Diary>> showResult = diaryDao.getAllDiary();

        mDisposable.add(
                showResult.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( diaries -> {
                                    diaryList.clear();
                                    diaryList.addAll(diaries);
                                    adapter.notifyDataSetChanged();
                                }
                        )
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.item02:
                Intent intent2 = new Intent(this, MapActivity.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void alarmBtnClick(long _id, String title, String address) {
        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
        intent.putExtra("id", _id);
        intent.putExtra("title", title);
        intent.putExtra("address", address);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    String flag = data.getStringExtra("flag");
                    String address = data.getStringExtra("address");
                    if (flag.equals("noCondition")) {
                        Toast.makeText(MainActivity.this, "최소 1시간 이후로 설정해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (!flag.equals("delete")) {
                        Completable result = diaryDao.updateDateTime(flag, address);
                        mDisposable.add(
                                result.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> Log.d(TAG, "Alarm Success "),
                                                throwable -> Log.d(TAG, "error"))
                        );
                    } else {
                        Completable result = diaryDao.updateDateTime(null, address);
                        mDisposable.add(
                                result.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> Log.d(TAG, "Alarm Delete "),
                                                throwable -> Log.d(TAG, "error"))
                        );
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case RESULT_CANCELED:
                    Log.d(TAG, "취소");
                    break;
            }
        }
    }
}