package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class Statistic extends AppCompatActivity {
    SQLiteDatabase db;
    ArrayList bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        BarChart barChart = findViewById(R.id.chart);
        getData();
        BarDataSet barDataSet = new BarDataSet(bar, "Training types");
        barDataSet.setColors(new int[] {Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW});
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new LargeValueFormatter());
        barChart.setData(barData);
        barChart.getData().setHighlightEnabled(false);
        barChart.invalidate();
        barChart.setDescription(null);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXOffset(1f);
        l.setTextSize(11f);

        LegendEntry legendEntryA = new LegendEntry();
        legendEntryA.label = "Group training";
        legendEntryA.formColor = Color.BLUE;
        LegendEntry legendEntryB = new LegendEntry();
        legendEntryB.label = "Personal training";
        legendEntryB.formColor = Color.GREEN;
        LegendEntry legendEntryC = new LegendEntry();
        legendEntryC.label = "Cardio training";
        legendEntryC.formColor = Color.RED;
        LegendEntry legendEntryD = new LegendEntry();
        legendEntryD.label = "Yoga session";
        legendEntryD.formColor = Color.YELLOW;
        l.setCustom(Arrays.asList(legendEntryA, legendEntryB, legendEntryC, legendEntryD));

        XAxis xAxis = barChart.getXAxis();
      //  xAxis.setGranularity(1f);
      //  xAxis.setGranularityEnabled(true);
        xAxis.setDrawLabels(false);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(barChart.getBarData().getXMin() - 1f);
        xAxis.setAxisMaximum(barChart.getBarData().getXMax() + 1f);

    }

    private void getData(){
        bar = new ArrayList();
        int group = 0, personal = 0, cardio = 0, yoga = 0;
        Cursor c = db.rawQuery("SELECT trainings.type FROM userTrainings, trainings WHERE trainings.id = userTrainings.id", null);
        if (c.moveToFirst()) {
            do {
                if(c.getString(0).equals("Group training")) {
                    group++;
                } else if(c.getString(0).equals("Personal training")) {
                    personal++;
                } else if(c.getString(0).equals("Cardio training")) {
                    cardio++;
                } else if(c.getString(0).equals("Yoga session")) {
                    yoga++;
                }
            } while (c.moveToNext());
            c.close();
        }

        bar.add(new BarEntry(2f, group));
        bar.add(new BarEntry(3f, personal));
        bar.add(new BarEntry(4f, cardio));
        bar.add(new BarEntry(5f, yoga));

    }
}