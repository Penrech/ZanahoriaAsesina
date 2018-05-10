package com.pau.enrech.zanahoriaasesina;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TargetActivity extends AppCompatActivity {

    private ConstraintLayout frameWinner,frameTarget;
    private TextView nameSurname;
    private TextView edad;
    private TextView penya;
    private TextView winRank;
    private ImageView img;
    private User user;
    private User target;
    private Button eliminate_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);


        frameTarget = findViewById(R.id.frame_target);
        frameWinner = findViewById(R.id.frame_winner);
        winRank = findViewById(R.id.win_rank);
        eliminate_btn = findViewById(R.id.target_eliminate);
        img = findViewById(R.id.target_photo);
        nameSurname = findViewById(R.id.target_nameSurname);
        edad = findViewById(R.id.target_age);
        penya = findViewById(R.id.target_penya);

        frameWinner.setVisibility(View.GONE);
        frameTarget.setVisibility(View.VISIBLE);

        user = UserDatabase.getUserFromId("Pau");
        target = UserDatabase.getUserFromId(user.target);

        nameSurname.setText(String.format("%s %s",target.name,target.surname));
        edad.setText(String.format("%d",target.age));
        penya.setText(String.format("%s",target.penya));


    }

    public void eliminateUser(View view){
        target = UserDatabase.eliminateUser(user, target);
        if (target == null) {
            winRank.setText(String.format("%d / %d",user.rank,UserDatabase.getNumUsers()));
            frameWinner.setVisibility(View.VISIBLE);
            frameTarget.setVisibility(View.GONE);
            eliminate_btn.setEnabled(false);
        }
        else {
            nameSurname.setText(String.format("%s %s", target.name, target.surname));
            edad.setText(String.format("%d", target.age));
            penya.setText(String.format("%s", target.penya));
        }
    }

}
