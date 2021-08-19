package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.contentcapture.DataShareWriteAdapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gelitenight.waveview.library.WaveView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    String []choices  = {"MANUAL", "WiFi","BLUETOOTH"};
    Button lockbutton ;
    String connection_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progress bar section
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(MainActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Get Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();


        //spinner
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,choices);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinner.setAdapter(arrayAdapter);

        //locking system
        lockbutton =findViewById(R.id.button);
        final FirebaseDatabase database =FirebaseDatabase.getInstance("https://smart-home-93a3b-default-rtdb.firebaseio.com/");
        final DatabaseReference lockref = database.getReference("lock_status");
        final DatabaseReference conn_status = database.getReference("connection_status");
        //Forgot Password

        //led ON / OFF
        ImageView bulbimage = (ImageView) findViewById(R.id.bulb_id);
        bulbimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog bulbdialog = new Dialog(MainActivity.this);
                bulbdialog.setContentView(R.layout.bulb);
                bulbdialog.show();
                final Switch sw1 = (Switch) bulbdialog.findViewById(R.id.switch1);
                final Switch sw2 = (Switch) bulbdialog.findViewById(R.id.switch2);
                final DatabaseReference led1 = database.getReference("LED1");
                final DatabaseReference led2= database.getReference("LED2");
                sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            led1.setValue("True");

                            Toast.makeText(MainActivity.this, "LED 1 is ON", Toast.LENGTH_SHORT).show();
                            bulbdialog.dismiss();
                        }
                        else{
                            led1.setValue("False");

                            Toast.makeText(MainActivity.this, "LED 1 is OFF", Toast.LENGTH_SHORT).show();
                            bulbdialog.dismiss();
                        }

                    }
                });
                sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            led2.setValue("True");

                            Toast.makeText(MainActivity.this, "LED 2 is ON", Toast.LENGTH_SHORT).show();
                            bulbdialog.dismiss();
                        }
                        else{
                            led2.setValue("False");

                            Toast.makeText(MainActivity.this, "LED 2 is OFF", Toast.LENGTH_SHORT).show();
                            bulbdialog.dismiss();
                        }
                    }
                });
            }
        });


        //FAN
        ImageView fanimage = findViewById(R.id.imageView5);

        fanimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog fania = new Dialog(MainActivity.this);
                fania.setContentView(R.layout.fan);
                fania.show();
                final Switch sw3= (Switch) fania.findViewById(R.id.switch3);
                final DatabaseReference fan = database.getReference("FAN");
                sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            fan.setValue(sw3.getTextOn());
                            sw3.setChecked(false);
                            Toast.makeText(MainActivity.this, "FAN is ON", Toast.LENGTH_SHORT).show();
                            fania.dismiss();

                        }else{
                            fan.setValue(sw3.getTextOn());
                            sw3.setChecked(true );
                            Toast.makeText(MainActivity.this, "FAN is OFF", Toast.LENGTH_SHORT).show();

                            fania.dismiss();
                        }
                    }
                });
            }
        });

        //water level
         ImageView waterimage = (ImageView) findViewById(R.id.water_id);
         waterimage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final Dialog waterdia = new Dialog(MainActivity.this);
                 waterdia.setContentView(R.layout.water);
                 waterdia.show();


                 //UI
                 final TextView sensorData;
                 final ImageView imgDat;
                 final EditText tartaly;



                 sensorData = waterdia.findViewById(R.id.sensorData);
                 imgDat = waterdia.findViewById(R.id.imgData);

                 Button okz =(Button) waterdia.findViewById(R.id.ok_);
                 final DatabaseReference water= database.getReference("WATER");
                 water.setValue(99);
                 okz.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         String max;
                         int maxI = 0;
                         //EditText ptanhi= waterdia.findViewById(R.id.tartalyET);
                         //max = ptanhi.getText().toString();
                         //maxI = Integer.parseInt(max);
                         final DatabaseReference water= database.getReference("WATER");
                         final int finalMaxI = 100;

                         water.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 int statusKorr;
                                 int statusFb;
                                 int status;
                                 int statusPC1;
                                 int statusPC2;
                                 statusFb = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());
                                 statusKorr = finalMaxI - statusFb;
                                 status = (int) (((double) statusKorr / (double) finalMaxI) * 100);



                                 sensorData.setText(Integer.toString(status) + " %");
                                 if (status >= 95 && status <= 100)
                                 {
                                     imgDat.setImageResource(R.drawable.i100);
                                 }
                                 else if (status > 0 && status < 10)
                                 {
                                     imgDat.setImageResource(R.drawable.i10);
                                 }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });

                     }
                 });



             }
         });




        //lock password
        final DatabaseReference password =database.getReference("lock_password");


        lockref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String stl = (String) dataSnapshot.getValue();


                if (stl.equals("true")){
                    lockbutton.setText("UNLOCK");
                    nDialog.dismiss();
                    findViewById(R.id.progress_loader).setVisibility(View.GONE);
                    lockbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final Dialog passdialog = new Dialog(MainActivity.this);
                            passdialog.setContentView(R.layout.lock_password);
                            passdialog.show();

                            TextView forpass = passdialog.findViewById(R.id.textView);
                            forpass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    passdialog.dismiss();
                                    Intent i = new Intent(MainActivity.this, forgot_password.class);
                                    startActivity(i);
                                }
                            });

                            Button ok  = (Button)  passdialog.findViewById(R.id.ok_btn);
                            final EditText password_text = (EditText) passdialog.findViewById(R.id.editTextNumberPassword);
                            ok.setOnClickListener(new View.OnClickListener() {



                                @Override
                                public void onClick(View view) {



                                    final String num1 = password_text.getText().toString();

                                    password.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            String p =(String) snapshot.getValue();
                                            if (num1.equals(p)){
                                                Toast.makeText(MainActivity.this, "Welcome Back..", Toast.LENGTH_SHORT).show();
                                                lockref.setValue("false");
                                                lockbutton.setText("LOCK");
                                                passdialog.dismiss();
                                                finish();
                                                startActivity(getIntent());
                                                }
                                            else{
                                                Toast.makeText(MainActivity.this, "Wrong Password..", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                        Button cancel = (Button) passdialog.findViewById(R.id.cancel_btn);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                passdialog.dismiss();
                            }
                        });

                        }
                    });
                }
                else if (stl.equals("false")){
                    lockbutton.setText("LOCK");
                    nDialog.dismiss();
                    findViewById(R.id.progress_loader).setVisibility(View.GONE);

                    lockbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lockref.setValue("true");
                            lockbutton.setText("UNLOCK");
                            finish();
                            startActivity(getIntent());



                        }
                    });

                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







            }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getApplicationContext(),choices[i] , Toast.LENGTH_LONG).show();
        FirebaseDatabase database =FirebaseDatabase.getInstance("https://smart-home-93a3b-default-rtdb.firebaseio.com/");
        final DatabaseReference conn_status = database.getReference("connection_status");
        conn_status.setValue(choices[i]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}