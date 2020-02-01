package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    Button dailyIntakesButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int REQUEST_CALL = 1;

    private TextView emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailView = findViewById(R.id.emailHomePage);
        try {
            emailView.setText(mAuth.getCurrentUser().getEmail().toString());
        }
        catch(Exception e)
        {
            emailView.setText("user not available");
        }

        dailyIntakesButton = findViewById(R.id.dailyIntake);
        checkOverFlow();
    }

    public void onClickEmergencyCall(View view){
        makeCall();
    }

    public void addAllergiesClick(View view)
    {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }

    public void gotoQRScanner(View view)
    {
        Intent intent = new Intent(this, QRNutritionScanner.class);
        startActivity(intent);
    }

    public void makeCall(){
        String number = "+8801982991409";
        if(number.trim().length() >0){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{
                String dial = "tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{
            Toast.makeText(this, "Please Add a Emergency Contact", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makeCall();
            }
            else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickLogout(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }

    public void manualPageClick(View v){
        Intent intent = new Intent(this, ManualNutritionAdditionPage.class);
        startActivity(intent);
    }

    public void goToMessagePage(View v){
        Intent intent = new Intent(this, MessagesPage.class);
        startActivity(intent);
    }

    public void allNutritionalLogsClick(View v){
        Intent intent = new Intent(this, AllNutritionalLogsPage.class);
        startActivity(intent);
    }

    public void checkOverFlow()
    {
        final DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
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
                    compareValues(maxIntakes, dailyIntakes);
                }
            }
        });
    }

    public void compareValues(ArrayList<Long> maxIntakes, ArrayList<Long> dailyIntakes)
    {
        boolean setRed = false;
        for(int i = 0; i < maxIntakes.size(); i++)
        {
            if((maxIntakes.get(i) != 0) && (dailyIntakes.get(i) >= maxIntakes.get(i)))
            {
                setRed = true;
            }
        }

        if(setRed)
        {
            dailyIntakesButton.setBackgroundColor(Color.RED);
            dailyIntakesButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.alert_symbol, 0, R.drawable.alert_symbol, 0);
        }
    }

    public void currentNutritionalIntakeClick(View v){

        Intent intent = new Intent(this, CurrentNutritionalIntakesPage.class);
        startActivity(intent);
    }
}
