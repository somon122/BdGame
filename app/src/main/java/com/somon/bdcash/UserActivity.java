package com.somon.bdcash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    EditText passwordET,confirmPasswordET,userNameET;
    CheckBox checkBox;
    TextView numberTV;

    FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String number;

    private String uId;
    FirebaseUser user;
    int checkUserInfo;
    ProgressDialog progressDialog;

    String username;
    String password;
    String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        passwordET = findViewById(R.id.password_id);
        confirmPasswordET = findViewById(R.id.confirmPassword2_id);
        userNameET = findViewById(R.id.username_id);
        checkBox = findViewById(R.id.condition_id);
        numberTV = findViewById(R.id.userNumber_id);
        progressDialog = new ProgressDialog(this);


        if (user != null){
            Toast.makeText(this, "Auth is Active", Toast.LENGTH_SHORT).show();
            uId = user.getUid();
            number = user.getPhoneNumber();
            numberTV.setText(number);
            balanceControl();
        }





        if (checkUserInfo >= 1){

            startActivity(new Intent(this,Home.class));
        }


    }

    public void Submit(View view) {

        username = userNameET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        confirmPassword = confirmPasswordET.getText().toString().trim();


         if (username.isEmpty()){
            userNameET.setError("Please Enter username");
            userNameET.requestFocus();


        }
        else if (password.isEmpty()){
            passwordET.setError("Please Enter password");
            passwordET.requestFocus();


        }
        else if (confirmPassword.isEmpty()){
            confirmPasswordET.setError("Please Enter confirmPassword");
            confirmPasswordET.requestFocus();

        }else {

                UserInfo userInfo = new UserInfo(username,number,confirmPassword);
                myRef.child(uId).child("UserInfo").setValue(userInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    checkUserInfo++;

                                    String userSetUpCount = String.valueOf(checkUserInfo);

                                    myRef.child(uId).child("checkUser").setValue(userSetUpCount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(UserActivity.this,Home.class));
                                                finish();

                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                                } else {
                                    Toast.makeText(UserActivity.this, "Net connection is problem", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
                });

         }


    }

    private void balanceControl() {

        myRef.child(uId).child("checkUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    checkUserInfo = Integer.parseInt(value);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
