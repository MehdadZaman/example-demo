package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MultiLoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_login_page);
    }

    public void userLogInClick(View v){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public void nutritionistlogInClick(View v){
        Intent intent = new Intent(this, NutritionistLogInPage.class);
        startActivity(intent);
    }
}
