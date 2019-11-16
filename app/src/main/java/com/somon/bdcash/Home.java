package com.somon.bdcash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;

    FirebaseAuth auth;
    FirebaseUser user;

    private int lastScore;
    private int money;
    private ClickBalanceControl clickBalanceControl;

    private TextView mainPointTV;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        clickBalanceControl = new ClickBalanceControl();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mainPointTV = findViewById(R.id.mainPoints_id);



        if (user != null){
            uId = user.getUid();
            balanceControl();
        }



    }


    @Override
    protected void onStart() {
        super.onStart();


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, new SignUp_Fragment ()).commit();

          /*  SignUp_Fragment signUp_fragment = new SignUp_Fragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.show_allFragment_id,signUp_fragment).commit();
*/

           /* emailLogInFragment emailLogInFragment = new emailLogInFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.show_allFragment_id,emailLogInFragment).commit();
*/

        }

    }

    public void UpdateButton(View view) {
        Toast.makeText(this, "Coming Soon..", Toast.LENGTH_SHORT).show();

    }

    public void UnlimitedButton(View view) {

        startActivity(new Intent(Home.this,MainActivity.class));

    }

    public void ClickWork(View view) {

        Toast.makeText(this, "Coming Soon..", Toast.LENGTH_SHORT).show();

    }

    private void balanceControl() {
        // Read from the database
        myRef.child(uId).child("Balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    lastScore = Integer.parseInt(value);
                    mainPointTV.setText("Your points: "+lastScore);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        myRef.child(uId).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    money = Integer.parseInt(value);
                    clickBalanceControl.setBalance(money);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.youtube_id){

            Toast.makeText(this, "Youtube is coming..", Toast.LENGTH_SHORT).show();

            return true;
        }
        if (id == R.id.facebook_id){

            Toast.makeText(this, "Facebook is coming..", Toast.LENGTH_SHORT).show();

            return true;
        }

        if (id == R.id.twitter_id){

            Toast.makeText(this, "Twitter is coming...", Toast.LENGTH_SHORT).show();

            return true;
        }

        if (id == R.id.whatsApp_id){

            Toast.makeText(this, "WhatsApp is coming...", Toast.LENGTH_SHORT).show();

            return true;
        }

        if (id == R.id.home_id) {

            startActivity(new Intent(getApplicationContext(),Home.class));

            return true;
        }

        if (id == R.id.convert_id) {

            convertPoint();

            return true;
        }
        if (id == R.id.withdraw_id){

            if (money >= 50){


                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, new WithdrawFragment ()).commit();

            }

            return true;
        }



        if (id == R.id.rules_id){

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new RulesFragment ()).commit();


            return true;
        }

        if (id == R.id.logOut_id){

            alert();

            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void alert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you Sure?")
                .setCancelable(false)
                .setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Home.this, "Successfully LogOut ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        finish();



                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Toast.makeText(Home.this, "Thank You for Staying...", Toast.LENGTH_SHORT).show();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void convertPoint() {

        if (haveNetwork()){

            if (lastScore >= 5000){

                int setScore = lastScore - 50;

                String updateScore = String.valueOf(setScore);

                myRef.child(uId).child("Balance").setValue(updateScore)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    clickBalanceControl.AddBalance(50);
                                    String updateBalance = String.valueOf(clickBalanceControl.getBalance());

                                    myRef.child(uId).child("ConvertBalance").setValue(updateBalance)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        convertAlert();
                                                    }

                                                }
                                            });



                                }
                            }
                        });




            }else {
                Toast.makeText(this, "Sorry..! You don't have enough point\n\nMinimum 50 points needed", Toast.LENGTH_LONG).show();
            }


        }else {

            Toast.makeText(this, "Please Check your Net connection", Toast.LENGTH_SHORT).show();

        }



    }




    private boolean haveNetwork ()
    {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo){

            if (info.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (info.isConnected())
                {
                    have_WiFi = true;
                }
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))

            {
                if (info.isConnected())
                {
                    have_Mobile = true;
                }
            }

        }
        return have_WiFi || have_Mobile;

    }

    private void convertAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

        builder.setTitle("Convert Point")
                .setMessage("Congratulation..! \n You got Tk50")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


}
