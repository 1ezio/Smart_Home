package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgot_password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button okb = (Button) findViewById(R.id.button2);
        okb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changepass();
            }
        });
        Button cancel = (Button) findViewById(R.id.button3);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgot_password.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void changepass() {
        FirebaseDatabase forpass = FirebaseDatabase.getInstance("https://smart-home-93a3b-default-rtdb.firebaseio.com/");

        final EditText password = (EditText) findViewById(R.id.editTextNumberPassword2);
        final EditText cnfpass = (EditText) findViewById(R.id.editTextNumberPassword3);
        String p1 = password.getText().toString();
        String p2 = cnfpass.getText().toString();
        DatabaseReference passreference = forpass.getReference("lock_password");
        if (p1.equals(p2)){

            passreference.setValue(p1);
            Toast.makeText(this, "Password Changed Succesfully..", Toast.LENGTH_SHORT).show();
            password.setText("");

            cnfpass.setText("");
            Intent intent = new Intent(forgot_password.this, MainActivity.class);
            startActivity(intent);
        }else{

            Toast.makeText(this, "Password does not Match...", Toast.LENGTH_SHORT).show();
        }



    }
}