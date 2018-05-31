package com.pau.enrech.zanahoriaasesina;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private EditText motivoReporte;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private String playerId, targetId, targetNomAp, userNomAp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        motivoReporte = findViewById(R.id.reportDescriptionInput);

        Intent intent = getIntent();
        playerId = intent.getStringExtra("idUser");
        targetId = intent.getStringExtra("idAReportar");
        targetNomAp = intent.getStringExtra("nombreAReportar");
        userNomAp = intent.getStringExtra("nombreReportador");
    }

    public void sendReport(View view){
        Log.d("Longitud motivoReporte", "sendReport: Valor de motivoreporte "+motivoReporte.getText().toString().length());
        String mReporte = motivoReporte.getText().toString().trim();
        if ( mReporte.matches("")){
            String msg = "Debes especificar el motivo del reporte";
            String title = "Reportar usuario";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
        }
        else{
            String msg = "¿Reportar a "+targetNomAp+" ?";
            String title = "Reportar usuario";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String key = root.child("reportes").push().getKey();
                    Date date = new Date();
                    Long dateTime = date.getTime();
                    Map<String,Object> reportValues = new HashMap<>();
                    reportValues.put("date",dateTime);
                    reportValues.put("nomApReporter",userNomAp);
                    reportValues.put("nomApReported",targetNomAp);
                    reportValues.put("message",motivoReporte.getText().toString());
                    reportValues.put("ReporterId",playerId);
                    reportValues.put("ReportedId",targetId);
                    root.child("reportes").child(key).setValue(reportValues);
                    finish();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }

    }
}
