package abc.integratedtest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by HAN on 2017-10-08.
 */

public class EditActivity extends Activity {

    // Layout 관련
    private EditText et_text;
    private Spinner spin_feel;
    private Spinner spin_weather;
    private Button btn_modify;

    // Intent 관련
    Intent intent;
    boolean isCreate; // 추가/수정 판별
    String updateId; // 수정 시 위치 식별

    // Spinner 관련
    int feel_index;
    int weather_index;
    private CustomArrayAdapter adapter_feel;
    private CustomArrayAdapter adapter_weather;

    // DB 관련
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Layout 초기화
        new Runnable() {
            @Override
            public void run() {
                List<SpinnerItem> items_feel = new ArrayList<>();
                items_feel.add(new SpinnerItem(R.drawable.excited, "최고예요"));
                items_feel.add(new SpinnerItem(R.drawable.happy, "좋아요"));
                items_feel.add(new SpinnerItem(R.drawable.worried, "그저 그래요"));
                items_feel.add(new SpinnerItem(R.drawable.sad, "슬퍼요"));
                items_feel.add(new SpinnerItem(R.drawable.angry, "화나요"));

                spin_feel = findViewById(R.id.spin_feel);
                adapter_feel = new CustomArrayAdapter(EditActivity.this, R.layout.spinner_item, items_feel);
                spin_feel.setAdapter(adapter_feel);

                List<SpinnerItem> items_weather = new ArrayList<>();
                items_weather.add(new SpinnerItem(R.drawable.sunny, "맑아요"));
                items_weather.add(new SpinnerItem(R.drawable.cloud, "구름투성이"));
                items_weather.add(new SpinnerItem(R.drawable.windy, "바람불어요"));
                items_weather.add(new SpinnerItem(R.drawable.rainy, "비와요"));
                items_weather.add(new SpinnerItem(R.drawable.snowy, "눈와요"));

                spin_weather = findViewById(R.id.spin_weather);
                adapter_weather = new CustomArrayAdapter(EditActivity.this, R.layout.spinner_item, items_weather);
                spin_weather.setAdapter(adapter_weather);

                btn_modify = findViewById(R.id.btn_modify);
            }
        }.run();

        // MainActivity Intent.putExtra 값 가져와서 초기화
        intent = getIntent();
        isCreate = intent.getBooleanExtra("isCreate", true);
        et_text = findViewById(R.id.et_text);

        if(isCreate) {
            btn_modify.setText("추가");
        } else {
            btn_modify.setText("수정");

            updateId = intent.getStringExtra("_id");

            et_text.setText(intent.getStringExtra("text"));
            spin_feel.setSelection(intent.getIntExtra("feel_index", 0));
            spin_weather.setSelection(intent.getIntExtra("weather_index", 0));
        }

        // 기분 선택 시
        spin_feel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                feel_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 날씨 선택 시
        spin_weather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weather_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onClick_modify(View v) {

        if(!et_text.getText().toString().equals("")) {
            helper = new MySQLiteOpenHelper(this, "list.db", null, MainActivity.DB_VERSION); // ※MainActivity 내 초기화 값과 통일

            Calendar calendar = Calendar.getInstance(); // 날짜 가져오기

            db = helper.getWritableDatabase(); // DB 수정 권한 얻기

            ContentValues values = new ContentValues(); // 테이블 수정할 값 지정(단순 query 문보다 간단)

            values.put("content", et_text.getText().toString());
            values.put("feel_id", feel_index);
            values.put("weather_id", weather_index);

            if(isCreate) { // 메모 추가 시
                values.put("date_year", calendar.get(Calendar.YEAR));
                values.put("date_month", calendar.get(Calendar.MONTH) + 1);
                values.put("date_day", calendar.get(Calendar.DATE));

                db.insert("memo", null, values);
            }
            else { // 메모 수정 시
                db.update("memo", values, "_id=?", new String[]{updateId});
                Toast.makeText(this, "수정되었어요.", Toast.LENGTH_SHORT).show();
            }
            finish(); // MainActivity 로 복귀(MainActivity 의 onResume 함수 실행)
        }
        else {
            Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show(); // EditText 내용이 없을 시 Toast 로 알림
        }
    }
}
