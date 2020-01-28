package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;

public class CurrentNutritionalIntakesPage extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    private double caloriePercentage;
    private double fatPercentage;
    private double fiberPercentage;
    private double sodiumPercentage;
    private double proteinPercentage;

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
            caloriePercentage = (((double)dailyIntakes.get(0))/maxIntakes.get(0));
        }
        if(maxIntakes.get(1) !=  0)
        {
            fatPercentage = (((double)dailyIntakes.get(1))/maxIntakes.get(1));
        }
        if(maxIntakes.get(2) !=  0)
        {
            fiberPercentage = (((double)dailyIntakes.get(2))/maxIntakes.get(2));
        }
        if(maxIntakes.get(3) !=  0)
        {
            sodiumPercentage = (((double)dailyIntakes.get(3))/maxIntakes.get(3));
        }
        if(maxIntakes.get(4) !=  0)
        {
            proteinPercentage = (((double)dailyIntakes.get(4))/maxIntakes.get(4));
        }
    }
}
