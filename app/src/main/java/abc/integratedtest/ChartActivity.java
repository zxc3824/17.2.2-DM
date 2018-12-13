package abc.integratedtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HAN on 2017-10-10.
 */

public class ChartActivity extends Activity {

    private ArrayList<Float> feelList, weatherList;
    private String[] feelStr = {"최고예요", "좋아요", "그저 그래요", "슬퍼요", "화나요"};
    private String[] weatherStr = {"맑아요", "구름꼈어요", "바람불어요", "비와요", "눈와요"};

    private PieChart pieChart_feel, pieChart_weather;
    private TextView tv_date_chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Chart Width, Height 설정
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // 화면 크기 얻기

        pieChart_feel = findViewById(R.id.chart_feel);
        pieChart_feel.getLayoutParams().width = size.x;
        pieChart_feel.getLayoutParams().height = pieChart_feel.getLayoutParams().width * 7 / 10;
        pieChart_weather = findViewById(R.id.chart_weather);
        pieChart_weather.getLayoutParams().width = pieChart_feel.getLayoutParams().width;
        pieChart_weather.getLayoutParams().height = pieChart_feel.getLayoutParams().width * 7 / 10;

        // 상위 Activity 변수 받아오기
        Intent intent = getIntent();

        tv_date_chart = findViewById(R.id.tv_date_chart);
        tv_date_chart.setText(intent.getStringExtra("dateText"));
        feelList = (ArrayList<Float>)intent.getSerializableExtra("feelList");
        weatherList = (ArrayList<Float>)intent.getSerializableExtra("weatherList");

        /////////////////
        // Feel 그래프 //
        /////////////////
        Description description = new Description();
        description.setText("");
        pieChart_feel.setDescription(description);
        pieChart_feel.setRotationEnabled(true); // 그래프 회전 여부
        pieChart_feel.setUsePercentValues(true); // 퍼센트 수치 표시 여부
        //pieChart_feel.setHoleColor(Color.rgb(123, 123, 123)); // 원 내부 배경색
        //pieChart_feel.setCenterTextColor(Color.BLACK); // 원 내부 텍스트 색상
        pieChart_feel.setHoleRadius(50f); // 원 내부 크기
        pieChart_feel.setTransparentCircleAlpha(150); // 그래프와 원 내부 사이 경계선 투명도
        pieChart_feel.setCenterText("기분"); // 원 내부 텍스트
        pieChart_feel.setCenterTextSize(20); // 원 내부 텍스트 크기

        ArrayList<PieEntry> yEntries_feel = new ArrayList<>();
        ArrayList<String> xEntries_feel = new ArrayList<>();

        for(int i = 0; i < feelList.size(); i++) {
            yEntries_feel.add(new PieEntry(feelList.get(i), i));
        }

        for(String str : feelStr) {
            xEntries_feel.add(str);
        }

        // DataSet 생성
        PieDataSet pieDataSet_feel = new PieDataSet(yEntries_feel, "기분");
        pieDataSet_feel.setSliceSpace(2); // 각 그래프 데이터 사이 여백 크기
        //pieDataSet_feel.setSelectionShift(12.0f); // 선택된 데이터 그래프 크기 확장(기본 12.0f)
        pieDataSet_feel.setValueTextSize(12); // 그래프 데이터 텍스트 크기

        // DataSet 색상 설정
        ArrayList<Integer> colors_feel = new ArrayList<>();
        colors_feel.add(Color.rgb(190, 186, 218));
        colors_feel.add(Color.rgb(251, 128, 114));
        colors_feel.add(Color.rgb(128, 177, 211));
        colors_feel.add(Color.rgb(253, 180, 98));
        colors_feel.add(Color.rgb(179, 222, 105));

        pieDataSet_feel.setColors(colors_feel);

        // 범례 추가
        Legend legend_feel = pieChart_feel.getLegend();

        legend_feel.setForm(Legend.LegendForm.CIRCLE);
        legend_feel.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend_feel.setTextSize(12);

        // 범례에 각 데이터 내역 삽입
        List<LegendEntry> entries_feel = new ArrayList<>();

        for(int i = 0; i < xEntries_feel.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors_feel.get(i);
            entry.label = xEntries_feel.get(i);
            entries_feel.add(entry);
        }

        legend_feel.setCustom(entries_feel);

        // Pie 데이터 객체 생성
        final PieData pieData_feel = new PieData(pieDataSet_feel);
        pieChart_feel.setData(pieData_feel);
        pieChart_feel.invalidate();

        pieChart_feel.animateY(1500); // 그래프 펼쳐지는 시간

        pieChart_feel.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                pieChart_feel.setCenterText(feelStr[(int)highlight.getX()] + "\n" + Integer.toString((int)highlight.getY()) + " 회");
            }

            @Override
            public void onNothingSelected() {
                pieChart_feel.setCenterText("기분");
            }
        });

        ////////////////////
        // Weather 그래프 //
        ////////////////////
        description.setText("");
        pieChart_weather.setDescription(description);
        pieChart_weather.setRotationEnabled(true); // 그래프 회전 여부
        pieChart_weather.setUsePercentValues(true); // 퍼센트 수치 표시 여부
        //pieChart_weather.setHoleColor(Color.rgb(123, 123, 123)); // 원 내부 배경색
        //pieChart_weather.setCenterTextColor(Color.BLACK); // 원 내부 텍스트 색상
        pieChart_weather.setHoleRadius(50f); // 원 내부 크기
        pieChart_weather.setTransparentCircleAlpha(150); // 그래프와 원 내부 사이 경계선 투명도
        pieChart_weather.setCenterText("날씨"); // 원 내부 텍스트
        pieChart_weather.setCenterTextSize(20); // 원 내부 텍스트 크기

        ArrayList<PieEntry> yEntries_weather= new ArrayList<>();
        ArrayList<String> xEntries_weather = new ArrayList<>();

        for(int i = 0; i < weatherList.size(); i++) {
            yEntries_weather.add(new PieEntry(weatherList.get(i), i));
        }

        for(String str : weatherStr) {
            xEntries_weather.add(str);
        }

        // DataSet 생성
        PieDataSet pieDataSet_weather = new PieDataSet(yEntries_weather, "날씨");
        pieDataSet_weather.setSliceSpace(2); // 각 그래프 데이터 사이 여백 크기
        //pieDataSet_weather.setSelectionShift(12.0f); // 선택된 데이터 그래프 크기 확장(기본 12.0f)
        pieDataSet_weather.setValueTextSize(12); // 그래프 데이터 텍스트 크기

        // DataSet 색상 설정
        ArrayList<Integer> colors_weather = new ArrayList<>();
        colors_weather.add(Color.rgb(190, 186, 218));
        colors_weather.add(Color.rgb(251, 128, 114));
        colors_weather.add(Color.rgb(128, 177, 211));
        colors_weather.add(Color.rgb(253, 180, 98));
        colors_weather.add(Color.rgb(179, 222, 105));

        pieDataSet_weather.setColors(colors_weather);

        // 범례 추가
        Legend legend_weather = pieChart_weather.getLegend();

        legend_weather.setForm(Legend.LegendForm.CIRCLE);
        legend_weather.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend_weather.setTextSize(12);

        // 범례에 각 데이터 내역 삽입
        List<LegendEntry> entries_weather = new ArrayList<>();

        for(int i = 0; i < xEntries_weather.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors_weather.get(i);
            entry.label = xEntries_weather.get(i);
            entries_weather.add(entry);
        }

        legend_weather.setCustom(entries_weather);

        // Pie 데이터 객체 생성
        PieData pieData_weather = new PieData(pieDataSet_weather);
        pieChart_weather.setData(pieData_weather);
        pieChart_weather.invalidate();

        pieChart_weather.animateY(1650); // 그래프 펼쳐지는 시간

        pieChart_weather.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                pieChart_weather.setCenterText(weatherStr[(int)highlight.getX()] + "\n" + Integer.toString((int)highlight.getY()) + " 회");
            }

            @Override
            public void onNothingSelected() {
                pieChart_weather.setCenterText("날씨");
            }
        });
    }
}
