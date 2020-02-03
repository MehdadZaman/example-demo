package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class NutritionistHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView emailView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutritionist_home_page);
        mAuth = FirebaseAuth.getInstance();
        emailView = findViewById(R.id.emailHomePage);
        try {
            emailView.setText(mAuth.getCurrentUser().getEmail().toString());
        }
        catch(Exception e)
        {
            emailView.setText("user not available");
        }
    }

    public void onClickPatientsButton(View view){

    }
    public void onClickLogout2(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}
