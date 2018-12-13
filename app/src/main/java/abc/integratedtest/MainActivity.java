package abc.integratedtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // DB, ListView 관련
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;
    ListView listView;
    ListCursorAdapter listCursorAdapter;
    final private String queryDefaultFront = "select * from memo ";
    final private String queryDefaultEnd = "order by _id desc";
    private String query;
    private Cursor c;

    // Date 관련
    private TextView tv_date;
    private int fY, fM, tY, tM; // DB 검색 기간
    final private String dateTextDefaultFront = "기간 : ";
    final private String dateTextDefaultAll = "전체";
    private String dateText; // 상단 출력 텍스트
    private boolean isCancel;

    // Delete 관련
    private boolean isDelete = false; // 삭제 버튼 체크 여부
    private Button btn_left;
    private Button btn_center;
    private Button btn_right;
    private boolean allChecked;
    private boolean allUnChecked;
    private int clickIndex;

    public final static int DB_VERSION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 첫 실행 시
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB 초기화
        helper = new MySQLiteOpenHelper(this, "list.db", null, DB_VERSION); // DB 생성 or 업그레이드용 함수(업그레이드는 버전 변수 변경 시 실행) (※다른 Class 의 helper 와 통일)

        // Date 초기화
        Calendar calendar = Calendar.getInstance();
        fY =  calendar.get(Calendar.YEAR);
        fM = calendar.get(Calendar.MONTH) + 1;
        tY =  calendar.get(Calendar.YEAR);
        tM = calendar.get(Calendar.MONTH) + 1;

        tv_date = (TextView)findViewById(R.id.tv_btn_date);
        dateText = dateTextDefaultFront + dateTextDefaultAll;
        tv_date.setText(dateText);

        isCancel = false;

        // ListView 에 Cursor 내역 삽입
        query = queryDefaultFront + queryDefaultEnd;

        listView = (ListView)findViewById(R.id.listview);
        db = helper.getReadableDatabase(); // DB 읽기 권한 부여
        c = db.rawQuery(query, null);
        listCursorAdapter = new ListCursorAdapter(this, c, 0);

        listView.setAdapter(listCursorAdapter);

        allChecked = false;
        allUnChecked = true;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, int i, long l) {
                clickIndex = i; // 내부 다이얼로그 Listener 에서 Index 확인용

                if(!isDelete) { // 클릭 시 다이얼로그 생성
                    listView.setItemChecked(i, !listView.isItemChecked(i)); // 체크 상태 유지

                    LayoutInflater inflater = getLayoutInflater();

                    View dialogView = inflater.inflate(R.layout.dialog_itemview, null);

                    float[] hsv = {360f / listCursorAdapter.getCount() * i + 1, 0.12f, 1.6f};
                    dialogView.setBackgroundColor(Color.HSVToColor(hsv));

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); // ※해당 Activity 의 Context 매개변수
                    builder.setView(dialogView);
                    builder.setCancelable(false);

                    // Layout Data Set
                    ImageView iv_feel_itemView = dialogView.findViewById(R.id.iv_feel_itemView);
                    TextView tv_feel_itemView = dialogView.findViewById(R.id.tv_feel_itemView);
                    ImageView iv_weather_itemView = dialogView.findViewById(R.id.iv_weather_itemView);
                    TextView tv_weather_itemView = dialogView.findViewById(R.id.tv_weather_itemView);
                    TextView tv_content_itemView = dialogView.findViewById(R.id.tv_content_itemView);
                    tv_content_itemView.setMovementMethod(new ScrollingMovementMethod()); // TextView 스크롤 기능 Set

                    c.moveToPosition(i);
                    int imageId = c.getInt(2);
                    if(imageId == 0) {
                        iv_feel_itemView.setImageResource(R.drawable.excited);
                        tv_feel_itemView.setText("최고예요");
                    }
                    else if(imageId == 1) {
                        iv_feel_itemView.setImageResource(R.drawable.happy);
                        tv_feel_itemView.setText("좋아요");
                    }
                    else if(imageId == 2) {
                        iv_feel_itemView.setImageResource(R.drawable.worried);
                        tv_feel_itemView.setText("그저 그래요");
                    }
                    else if(imageId == 3) {
                        iv_feel_itemView.setImageResource(R.drawable.sad);
                        tv_feel_itemView.setText("슬퍼요");
                    }
                    else if(imageId == 4) {
                        iv_feel_itemView.setImageResource(R.drawable.angry);
                        tv_feel_itemView.setText("화나요");
                    }
                    imageId = c.getInt(3);
                    if(imageId == 0) {
                        iv_weather_itemView.setImageResource(R.drawable.sunny);
                        tv_weather_itemView.setText("맑아요");
                    }
                    else if(imageId == 1) {
                        iv_weather_itemView.setImageResource(R.drawable.cloud);
                        tv_weather_itemView.setText("구름꼈어요");
                    }
                    else if(imageId == 2) {
                        iv_weather_itemView.setImageResource(R.drawable.windy);
                        tv_weather_itemView.setText("바람불어요");
                    }
                    else if(imageId == 3) {
                        iv_weather_itemView.setImageResource(R.drawable.rainy);
                        tv_weather_itemView.setText("비와요");
                    }
                    else if(imageId == 4) {
                        iv_weather_itemView.setImageResource(R.drawable.snowy);
                        tv_weather_itemView.setText("눈와요");
                    }
                    tv_content_itemView.setText(c.getString(1));

                    builder.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            c.moveToPosition(clickIndex);
                            db = helper.getWritableDatabase();

                            db.delete("memo", "_id=?", new String[] {c.getString(0)});
                            c = db.rawQuery(query, null);
                            listCursorAdapter.swapCursor(c);
                            Toast.makeText(getApplicationContext(), "삭제되었어요.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), EditActivity.class);

                            intent.putExtra("isCreate", false);
                            c.moveToPosition(clickIndex);
                            intent.putExtra("_id", c.getString(0)); // ※query 문에서 사용하기 때문에 String 으로 형변환
                            intent.putExtra("text", c.getString(1));
                            intent.putExtra("feel_index", c.getInt(2));
                            intent.putExtra("weather_index", c.getInt(3));

                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            startActivity(intent);
                        }
                    });

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("MainActivity", String.format("컬러 : %s", ((ColorDrawable)view.getBackground()).getColor()));
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else { // 체크박스 토글 시 UI Image 조절

                    allChecked = true;
                    allUnChecked = true;

                    for (int n = 0; n < listView.getCount(); n++) {
                        if(!listView.isItemChecked(n)) {
                            allChecked = false;
                            break;
                        }
                    }

                    for (int n = 0; n < listView.getCount(); n++) {
                        if(listView.isItemChecked(n)) {
                            allUnChecked = false;
                            break;
                        }
                    }

                    if(allChecked) btn_left.setBackgroundResource(R.drawable.cb_checked);
                    else btn_left.setBackgroundResource(R.drawable.cb_unchecked);

                    if(allUnChecked) btn_center.setBackgroundResource(R.drawable.ic_trashcan_grey);
                    else btn_center.setBackgroundResource(R.drawable.ic_trashcan_red);
                }
            }
        });

        btn_left = (Button)findViewById(R.id.btn_chart);
        btn_center = (Button)findViewById(R.id.btn_delete);
        btn_right = (Button)findViewById(R.id.btn_create);
    }

    @Override
    protected void onResume() { // 현 Activity 복귀 시
        super.onResume();

        // ListView 갱신
        c = db.rawQuery(query, null);
        listCursorAdapter.swapCursor(c);
    }

    // DB 기간 설정
    public void onClick_date(View v) {
        dialog_setDate(false); // 기간 설정 다이얼로그 생성
    }

    public void onClick_createMemo(View v) {
        if(!isDelete) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("isCreate", true);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
        }
        else { // 삭제 취소 버튼 클릭 시
            isDelete = false;
            btn_left.setBackgroundResource(R.drawable.ic_chart);
            btn_center.setBackgroundResource(R.drawable.ic_trashcan);
            btn_right.setBackgroundResource(R.drawable.ic_pencil);

            for (int i = 0; i < listView.getCount(); i++) {
                listView.setItemChecked(i, false);
            }
            allUnChecked = true;

            listCursorAdapter.toggleVisibility(isDelete);
        }
    }

    public void onClick_delete(View v) {
        if(isDelete) {
            if(allUnChecked) { // 아무것도 체크되지 않았을 시
                Toast.makeText(this, "하나 이상 체크해주세요.", Toast.LENGTH_SHORT).show();
            }
            else { // 한 개라도 체크 시
                db = helper.getWritableDatabase();

                for (int i = 0; i < listView.getCount(); i++) {
                    if(listView.isItemChecked(i)) {
                        c.moveToPosition(i);
                        db.delete("memo", "_id=?", new String[] {c.getString(0)}); // 제거문
                    }
                }
                Toast.makeText(this, "삭제되었어요.", Toast.LENGTH_SHORT).show();

                c = db.rawQuery(query, null);
                listCursorAdapter.swapCursor(c);
                btn_center.setBackgroundResource(R.drawable.ic_trashcan);
            }
        } else {
            isDelete = true;
            btn_left.setBackgroundResource(R.drawable.cb_unchecked);
            btn_center.setBackgroundResource(R.drawable.ic_trashcan_grey);
            btn_right.setBackgroundResource(R.drawable.ic_cancel);
        }
        listCursorAdapter.toggleVisibility(isDelete);
    }

    public void onClick_chart(View v) {
        if(!isDelete) {
            dialog_setDate(true); // 기간 설정 다이얼로그 생성
        }
        else { // 삭제 도중 전체 체크 버튼
            if(!allChecked) { // 전체 체크
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, true);
                }

                allChecked = true;
                allUnChecked = false;
                btn_left.setBackgroundResource(R.drawable.cb_checked);
                btn_center.setBackgroundResource(R.drawable.ic_trashcan_red);
            }
            else { // 이미 전부 체크된 상태인 경우(전체 체크 해제)
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, false);
                }

                allChecked = false;
                allUnChecked = true;
                btn_left.setBackgroundResource(R.drawable.cb_unchecked);
                btn_center.setBackgroundResource(R.drawable.ic_trashcan_grey);
            }

        }
    }

    public void dialog_setDate(final boolean isChart) {

        final LayoutInflater inflater = getLayoutInflater(); // 레이아웃 객체 생성 및 초기화

        View dialogView = inflater.inflate(R.layout.dialog_date, null); // 다이얼로그 View 객체 생성

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Builder 객체 생성
        builder.setTitle("기간 설정"); // Dialog 타이틀
        builder.setView(dialogView); // 생성한 Dialog View 지정
        builder.setCancelable(false);

        Calendar calendar = Calendar.getInstance(); // 현재 날짜 가져오기
        final NumberPicker np_fromYear = dialogView.findViewById(R.id.np_fromYear); // ★해당 아이템이 존재하는 layout 을 지정한 View 객체로 findViewById 를 사용해야함
        np_fromYear.setMaxValue(calendar.get(Calendar.YEAR));
        np_fromYear.setMinValue(calendar.get(Calendar.YEAR) - 10);
        np_fromYear.setValue(calendar.get(Calendar.YEAR));
        final NumberPicker np_fromMonth = dialogView.findViewById(R.id.np_fromMonth);
        np_fromMonth.setMaxValue(12);
        np_fromMonth.setMinValue(1);
        np_fromMonth.setValue(calendar.get(Calendar.MONTH) + 1);
        final NumberPicker np_toYear = dialogView.findViewById(R.id.np_toYear);
        np_toYear.setMaxValue(calendar.get(Calendar.YEAR));
        np_toYear.setMinValue(calendar.get(Calendar.YEAR) - 10);
        np_toYear.setValue(calendar.get(Calendar.YEAR));
        final NumberPicker np_toMonth = dialogView.findViewById(R.id.np_toMonth);
        np_toMonth.setMaxValue(12);
        np_toMonth.setMinValue(1);
        np_toMonth.setValue(calendar.get(Calendar.MONTH) + 1);

        // 버튼 생성 및 onClick 이벤트 작성
        builder.setPositiveButton("적용", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setNeutralButton("모두 보기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Calendar calendar = Calendar.getInstance();
                fY = 0;
                fM = 0;
                tY = 0;
                tM = 0;

                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isCancel = true;
                dialogInterface.cancel();
            }
        });

        final AlertDialog dialog = builder.create(); // 다이얼로그를 생성해서 builder 내용으로 초기화
        dialog.setCanceledOnTouchOutside(false); // 다이얼로그 바깥 터치 시 사라질지 여부

        dialog.show(); // 다이얼로그 출력

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isClose = false;
                fY = np_fromYear.getValue();
                fM = np_fromMonth.getValue();
                tY = np_toYear.getValue();
                tM = np_toMonth.getValue();
                if(fY < tY || (fY == tY && fM <= tM)) {
                    isClose = true;
                } else {
                    Toast.makeText(getApplicationContext(), "시작 날짜가 더 커요.", Toast.LENGTH_SHORT).show();
                }

                if(isClose) dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                String conditions = "";
                if(fY != 0) {
                    conditions = "where (date_year between " + fY + " and " + tY + ") and not(date_year = " + fY + " and date_month < " + fM + ") and not(date_year = " + tY + " and date_month > " + tM + ") ";
                }

                query = queryDefaultFront + conditions + queryDefaultEnd;
                c = db.rawQuery(query, null);

                if(fY != 0) {
                    if(fY == tY) {
                        if(fM == tM) dateText = dateTextDefaultFront + fY + "." + fM;
                        else dateText = dateTextDefaultFront + fY + "." + fM + " - " + tM;
                    } else {
                        dateText = dateTextDefaultFront + fY + "." + fM + " - " + tY + "." + tM;
                    }
                }
                else {
                    dateText = dateTextDefaultFront + dateTextDefaultAll;
                }

                if(!isChart) { // 리스트뷰 기간 설정 시
                    tv_date.setText(dateText);

                    listCursorAdapter.swapCursor(c);
                }
                else { // 차트 버튼 선택 시
                    if(isCancel) { // 취소 예외 처리
                        isCancel = false;
                        return;
                    }

                    if(c.getCount() != 0) {
                        String chartQuery = "select feel_id, weather_id from memo " + conditions;

                        ArrayList<Float> feelList = new ArrayList<>();

                        if(fY == 0) { // 모두 보기 선택 시
                            chartQuery += "where ";
                        } else { // 적용 선택 시
                            chartQuery += "and ";
                        }
                        for(int i = 0; i < 5; i++) {
                            c = db.rawQuery(chartQuery + "feel_id = " + String.format("%s", i), null);
                            feelList.add((float) c.getCount());
                        }

                        ArrayList<Float> weatherList = new ArrayList<>();

                        for(int i = 0; i < 5; i++) {
                            c = db.rawQuery(chartQuery + "weather_id = " + String.format("%s", i), null);
                            weatherList.add((float) c.getCount());
                        }

                        Intent intent = new Intent(getApplicationContext(), ChartActivity.class);

                        intent.putExtra("feelList", feelList);
                        intent.putExtra("weatherList", weatherList);
                        intent.putExtra("dateText", dateText);

                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(intent);
                    } else { // 검색된 메모 없을 시
                        Toast.makeText(getApplicationContext(), "검색된 내용이 없어요.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

}
