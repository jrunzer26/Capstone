package com.example.android.chartdemo;

import org.achartengine.*;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XYSeries series = new XYSeries("Series 1");
        XYSeries series2 = new XYSeries("Series 2");
        for (int i = 0; i < 20; i++) {
            series.add(i, i*3);
            series2.add(i, i*2);
        }

        XYMultipleSeriesDataset xyMultipleSeriesDataset = new XYMultipleSeriesDataset();
        xyMultipleSeriesDataset.addSeries(series);
        xyMultipleSeriesDataset.addSeries(series2);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(10);
        renderer.setColor(Color.RED);
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(15);

        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        renderer.setLineWidth(10);
        renderer.setColor(Color.BLUE);
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(20);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.addSeriesRenderer(renderer2);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
// Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(35);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid

        GraphicalView chartView = ChartFactory.getLineChartView(this, xyMultipleSeriesDataset, mRenderer);
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        layout.addView(chartView);
        chartView.repaint();
    }
}
