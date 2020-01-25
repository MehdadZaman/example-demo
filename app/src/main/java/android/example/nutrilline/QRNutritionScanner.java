package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
//import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.PendingIntent.getActivity;

public class QRNutritionScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    int[] nutritionIntegers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrnutrition_scanner);

        scannerView = findViewById(R.id.zxscan);
        txtResult = findViewById(R.id.txt_result);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        scannerView.setResultHandler(QRNutritionScanner.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QRNutritionScanner.this, "You must accept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        processRawResult(rawResult.getText());
    }

    public void processRawResult(String s) {
        s = s.toLowerCase();
        if(s.startsWith("nutritional information:"))
        {
            String nutritionalNumbers = s.substring(s.indexOf('\n') + 1);
            txtResult.setText("Valid QR Code");
            processIngredients(nutritionalNumbers);
        }
        else
        {
            txtResult.setText("Invalid QR Code");
            showDialogueInvalidQRCode();
        }
    }

    public void processIngredients(String nutritionalNumbers)
    {
        String[] nutritionList = nutritionalNumbers.split(",");
        //Weird cases below
        //String[] ingredientList = ingredients.split("[^a-zA-Z \n]");
        for(int i = 0; i < nutritionList.length; i++) {
            nutritionList[i] = nutritionList[i].trim();
        }
        nutritionIntegers = new int[nutritionList.length];
        try{
            for(int i = 0; i < nutritionList.length; i++)
            {
                nutritionIntegers[i] = Integer.parseInt(nutritionList[i]);
            }
        }catch (Exception e){e.printStackTrace();}
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

    public void showDialogueInvalidQRCode()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRNutritionScanner.this);
        builder.setMessage("Invalid QR Code")
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, QRNutritionScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    public void showDialogueNutritionAdded()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRNutritionScanner.this);
        builder.setMessage("Nutritional Information Added")
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, QRNutritionScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }
}