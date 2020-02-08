package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PatientsListPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list_page);
    }

    public void goToPatient(View view){
        Intent intent = new Intent(this, AllNutritionalLogsDoctorPage.class);
        startActivity(intent);
    }

    public void goToEmail(View view){
        Intent intent = new Intent(this, SendEmailPage.class);
        startActivity(intent);
    }

    public void backToDoctorHome(View view){
        Intent intent = new Intent(this, NutritionistHomePage.class);
        startActivity(intent);
    }
}
