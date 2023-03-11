package ddwu.mobile.finalproject.ma02_20201021;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {
    public static final String TAG = "20201021_ma";

    Long _id;
    String address;
    String title;

    private TimePicker timePicker;
    private DatePicker datePicker;
    private AlarmManager alarmManager;
    private int year = -1, month = -1, day = -1, hour, minute;

    PendingIntent pIntent = null;
    Intent intent = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        _id = getIntent().getLongExtra("id", -1);
        title = getIntent().getStringExtra("title");
        address = getIntent().getStringExtra("address");

        timePicker = findViewById(R.id.timePicker);
        datePicker = findViewById(R.id.datePicker);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                    year = y;
                    month = m;
                    day = d;
                }
            });
        }

        intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        intent.putExtra("title", title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            pIntent = PendingIntent.getBroadcast(AlarmActivity.this, _id.intValue(), intent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pIntent = PendingIntent.getBroadcast(AlarmActivity.this, _id.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.registerBtn2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }

                if (year == -1 || month == -1 || day == -1) {
                    Toast.makeText(AlarmActivity.this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_YEAR, year);
                calendar.set(Calendar.DAY_OF_MONTH, month);
                calendar.set(Calendar.DATE, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour - 1);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long selectTime = calendar.getTimeInMillis();
                long currentTime = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                String dateTime = sdf.format(calendar.getTime());
                String curTime = sdf.format(System.currentTimeMillis());

                Log.d(TAG, dateTime + " / " + curTime);


                if(currentTime < selectTime){
                    alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, pIntent);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("flag", dateTime);
                    resultIntent.putExtra("address", address);
                    setResult(RESULT_OK, resultIntent);
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("flag", "noCondition");
                    resultIntent.putExtra("address", address);
                    setResult(RESULT_OK, resultIntent);
                }
                finish();

                break;
            case R.id.cancelBtn2:
                alarmManager.cancel(pIntent);
                Intent resultIntent2 = new Intent();
                resultIntent2.putExtra("flag", "delete");
                resultIntent2.putExtra("address", address);
                setResult(RESULT_OK, resultIntent2);
                finish();
                break;
        }
    }
}
