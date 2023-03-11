package ddwu.mobile.finalproject.ma02_20201021;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class WriteActivity extends AppCompatActivity {

    public static final String TAG = "20201021_ma";

    EditText etName;
    EditText etAddress;
    EditText etContent;

    DiaryDB diaryDB;
    DiaryDao diaryDao;

    String content = null;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etContent = findViewById(R.id.etContent);

        content = getIntent().getStringExtra("content");

        etName.setText(getIntent().getStringExtra("title"));
        etAddress.setText(getIntent().getStringExtra("address"));
        if (content != null && !content.equals("")) {
            etContent.setText(content);
        }

        diaryDB = DiaryDB.getDatabase(this);
        diaryDao = diaryDB.diaryDao();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn:
                String title = etName.getText().toString();
                String address = etAddress.getText().toString();
                String fillContent = etContent.getText().toString();
                if (title.equals("") || address.equals("") || fillContent.equals("")) {
                    Toast.makeText(this, "이름 혹은 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Diary diary = new Diary(title, address, fillContent);
                    if (content == null || content.equals("")) { // insert
                        Single<Long> insertResult = diaryDao.insertDiary(diary);

                        mDisposable.add(
                                insertResult.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(result -> { setResult(RESULT_OK); finish(); },
                                                throwable -> { setResult(RESULT_CANCELED); finish(); } )
                        );
                    } else { // update
                        Completable updateResult = diaryDao.updateDiary(title, address, fillContent);
                        mDisposable.add(
                                updateResult.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> { setResult(RESULT_OK); finish(); },
                                                throwable -> { setResult(RESULT_CANCELED); finish(); })
                        );
                    }

                }

                break;

            case R.id.cancelBtn:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.mapButton:
                Intent intent = new Intent(WriteActivity.this, MapActivity.class);
                intent.putExtra("address", etAddress.getText().toString());
                startActivity(intent);
                break;
        }
    }
}
