package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CurrentNutritionalIntakesPage extends AppCompatActivity {

    private float caloriePercentage;
    private float fatPercentage;
    private float fiberPercentage;
    private float sodiumPercentage;
    private float proteinPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_nutritional_intakes_page);
    }

}
