package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;

public class ManualNutitionAdditionPage extends AppCompatActivity {

    private EditText calorieText;
    private EditText fatText;
    private EditText fiberText;
    private EditText sodiumText;
    private EditText proteinText;

    private int calorie;
    private int fat;
    private int fiber;
    private int sodium;
    private int protein;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    int[] nutritionIntegers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_nutition_addition_page);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        calorieText = findViewById(R.id.intakeCalories);
        fatText = findViewById(R.id.intakeFat);
        fiberText = findViewById(R.id.intakeFiber);
        sodiumText = findViewById(R.id.intakeSodium);
        proteinText = findViewById(R.id.intakeProtein);
    }

    public void addMealClick(View v)
    {
        try {calorie = Integer.parseInt(calorieText.getText().toString());} catch (Exception e){e.printStackTrace();}
        try {fat = Integer.parseInt(fatText.getText().toString());} catch (Exception e){e.printStackTrace();}
        try {fiber = Integer.parseInt(fiberText.getText().toString());} catch (Exception e){e.printStackTrace();}
        try {sodium = Integer.parseInt(sodiumText.getText().toString());} catch (Exception e){e.printStackTrace();}
        try {protein = Integer.parseInt(proteinText.getText().toString());} catch (Exception e){e.printStackTrace();}
        processIngredients();
    }

    public void processIngredients()
    {
        //Weird cases below
        //String[] ingredientList = ingredients.split("[^a-zA-Z \n]");
        nutritionIntegers = new int[5];
        nutritionIntegers[0] = calorie;
        nutritionIntegers[1] = fat;
        nutritionIntegers[2] = fiber;
        nutritionIntegers[3] = sodium;
        nutritionIntegers[4] = protein;

        FirebaseUser user = firebaseAuth.getCurrentUser();
        final DocumentReference docRef = db.collection("users").document(user.getUid());
        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.SERVER;
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> dataMap = document.getData();
                    ArrayList<Long> numbers = (ArrayList<Long>)document.get("Current Daily Intakes");
                    processIngredients2(numbers);
                }
            }
        });
    }

    public void processIngredients2(ArrayList<Long> nutNums)
    {
        for(int i = 0; i < nutritionIntegers.length; i++)
        {
            nutNums.set(i, (nutNums.get(i) + (int)((long)nutritionIntegers[i])));
        }

        DocumentReference df = db.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        df.update("Current Daily Intakes", nutNums);
        showDialogueNutritionAdded();
    }

    public void showDialogueNutritionAdded()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ManualNutitionAdditionPage.this);
        builder.setMessage("Nutritional Information Added")
                .setPositiveButton("Add Another Item", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ManualNutitionAdditionPage.this, ManualNutitionAdditionPage.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ManualNutitionAdditionPage.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }
}