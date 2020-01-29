package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.icu.text.Transliterator;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrentNutritionalIntakesPage extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    private double caloriePercentage;
    private double fatPercentage;
    private double fiberPercentage;
    private double sodiumPercentage;
    private double proteinPercentage;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_nutritional_intakes_page);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setIntakePercentages();

    }

    public void setIntakePercentages()
    {
        final DocumentReference docRef = db.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.SERVER;
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> dataMap = document.getData();
                    ArrayList<Long> dailyIntakes = (ArrayList<Long>) dataMap.get("Current Daily Intakes");
                    ArrayList<Long> maxIntakes = (ArrayList<Long>) dataMap.get("Max Intakes");
                    setIntakePercentages2(maxIntakes, dailyIntakes);
                }
            }
        });
    }

    public void setIntakePercentages2(ArrayList<Long> maxIntakes, ArrayList<Long> dailyIntakes)
    {
        if(maxIntakes.get(0) !=  0)
        {
            caloriePercentage = (((double)dailyIntakes.get(0))/(double)maxIntakes.get(0))*100.0;
        }
        if(maxIntakes.get(1) !=  0)
        {
            fatPercentage = (((double)dailyIntakes.get(1))/(double)maxIntakes.get(1))*100.0;
        }
        if(maxIntakes.get(2) !=  0)
        {
            fiberPercentage = (((double)dailyIntakes.get(2))/(double)maxIntakes.get(2))*100.0;
        }
        if(maxIntakes.get(3) !=  0)
        {
            sodiumPercentage = (((double)dailyIntakes.get(3))/(double)maxIntakes.get(3))*100.0;
        }
        if(maxIntakes.get(4) !=  0)
        {
            proteinPercentage = (((double)dailyIntakes.get(4))/(double)maxIntakes.get(4))*100.0;
        }

        createBarGraph( caloriePercentage, fatPercentage, fiberPercentage, sodiumPercentage, proteinPercentage);
    }

    public void createBarGraph(double calorie, double fat, double fiber, double sodium, double protein){

        barChart = (BarChart) findViewById(R.id.barChart);
        Description description = barChart.getDescription();
        description.setEnabled(false);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawGridBackground(true);
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        String[] calories = new String[]{"Calories","Fat","Fiber","Sodium","Protein"};
        //xAxis.setValueFormatter(new MyAxisValueFormatter(calories));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(calories));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        //xAxis.setLabelCount(5);
        //xAxis.setGranularityEnabled(true);
        //xAxis.setGranularity(1f);


        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisLineWidth(3);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, (float) calorie));
        barEntries.add(new BarEntry(1f, (float) fat));
        barEntries.add(new BarEntry(2f, (float) fiber));
        barEntries.add(new BarEntry(3f, (float) sodium));
        barEntries.add(new BarEntry(4f, (float) protein));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Calories(%)");
        barDataSet.setValueTextSize(10f);

        List<Integer> colors = new  ArrayList<>();
        if(calorie > 100){
            colors.add(Color.RED);
        }
        else{
            colors.add(Color.GREEN);
        }
        if(fat > 100){
            colors.add(Color.RED);
        }
        else{
            colors.add(Color.GREEN);
        }
        if(fiber > 100){
            colors.add(Color.RED);
        }
        else{
            colors.add(Color.GREEN);
        }
        if(sodium > 100){
            colors.add(Color.RED);
        }
        else{
            colors.add(Color.GREEN);
        }
        if(protein > 100){
            colors.add(Color.RED);
        }
        else{
            colors.add(Color.GREEN);
        }
        barDataSet.setColors(colors);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
    }

    public class MyAxisValueFormatter extends ValueFormatter {
        private String[] mvalues;
        public MyAxisValueFormatter(String[] values){
            this.mvalues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return mvalues[(int)value];
        }
    }
}
