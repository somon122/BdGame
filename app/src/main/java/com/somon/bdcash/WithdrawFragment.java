package com.somon.bdcash;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WithdrawFragment extends Fragment {


    public WithdrawFragment() {
        // Required empty public constructor
    }


    private ConstraintLayout constraintLayout1, constraintLayout2, constraintLayout3, constraintLayout4;
    private Spinner spinner;
    private String spinnerValue;
    private TextView checkBalanceTV;

    private EditText paypalAddressET, paypalAmountET, bKashNumberET, bKashAmountET,
            rocketNumberET, rocketAmountET, mobileReNumberET, mobileReAmount;

    private Button submitButton;
    private WithdrawSubmit submit;
    private BalanceSetUp balanceSetUp;
    private ClickBalanceControl clickBalanceControl;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uID;
    private String currentDateTimeString;

    private String paypleAddress;
    private String bKashNumber;
    private String rocketNumber;
    private String rechargeNumber;

    private int payPalAmount;
    private int bKashAmount;
    private int rocketAmount;
    private int rechargeAmount;
    private String time;
    private String pushId;

    private ProgressDialog progressDialog;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_withdraw, container, false);



        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        balanceSetUp = new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();
        if (user != null) {
            uID = user.getUid();
            pushId = myRef.push().getKey();

        }
        constraintLayout1 = root.findViewById(R.id.paypalConstraintLayout_id);
        constraintLayout2 = root.findViewById(R.id.bKashConstraintLayout_id);
        constraintLayout3 = root.findViewById(R.id.rocketConstraintLayout_id);
        constraintLayout4 = root.findViewById(R.id.rechargeConstraintLayout_id);
        spinner = root.findViewById(R.id.spinner_id);
        submitButton = root.findViewById(R.id.withdrawSubmit_id);
        paypalAddressET = root.findViewById(R.id.PaypalAddress_id);
        paypalAmountET = root.findViewById(R.id.paypalAmount_id);

        checkBalanceTV = root.findViewById(R.id.checkBalanceId);

        bKashNumberET = root.findViewById(R.id.bKashNumber_id);
        bKashAmountET = root.findViewById(R.id.bKashAmount_id);

        rocketNumberET = root.findViewById(R.id.rocketNumber_id);
        rocketAmountET = root.findViewById(R.id.rocketAmount_id);

        mobileReNumberET = root.findViewById(R.id.rechargeNumber_id);
        mobileReAmount = root.findViewById(R.id.rechargeAmount_id);

        constraintLayout1.setVisibility(View.GONE);
        constraintLayout2.setVisibility(View.GONE);
        constraintLayout3.setVisibility(View.GONE);
        constraintLayout4.setVisibility(View.GONE);

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();


        time = currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


        myRef.child(uID).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));
                    checkBalanceTV.setText("Your balance: " + value);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), " Data is loading...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();

            }
        });

        List<String> paymentSystem = new ArrayList<String>();
        paymentSystem.add("Mobile Recharge");
        paymentSystem.add("PayPal");
        paymentSystem.add("BKash");
        paymentSystem.add("Rocket");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, paymentSystem);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectItem2();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submittion();

            }
        });



        return root;

    }



    private void submittion() {


        if (spinnerValue.equals("PayPal")) {


            paypleAddress = paypalAddressET.getText().toString().trim();
            final String requestAmount = paypalAmountET.getText().toString().trim();

            if (paypleAddress.isEmpty()) {

                paypalAddressET.setError("Please Enter PayPal Address");


            } else if (!Patterns.EMAIL_ADDRESS.matcher(paypleAddress).matches()){
                paypalAddressET.setError("Please Enter valid PayPal Address");

            }

            else if (requestAmount.isEmpty()) {
                paypalAmountET.setError("Please Enter PayPal Address");


            } else {

                progressDialog.show();
                payPalAmount = Integer.parseInt(requestAmount);

                if (payPalAmount >= 350) {

                    if (clickBalanceControl.getBalance() >= payPalAmount) {
                        String payPal = "PayPal";
                        confirmAlert(payPalAmount, paypleAddress, payPal, requestAmount);


                    } else {
                        progressDialog.dismiss();
                        alertDialog("Sorry..!\nYou don't have enough Balance", "Yes", "Read Rules more..");
                    }
                } else {

                    progressDialog.dismiss();
                    alertDialog("Minimum TK350 need for withdraw", "OK", "Read Rules more..");

                }


            }


        } else if (spinnerValue.equals("BKash")) {


            bKashNumber = bKashNumberET.getText().toString().trim();
            final String amount = bKashAmountET.getText().toString().trim();

            if (bKashNumber.isEmpty() || bKashNumber.length() < 11 || bKashNumber.length() > 11) {

                bKashNumberET.setError("Please Enter Bkash Number");


            } else if (amount.isEmpty()) {
                bKashAmountET.setError("Please Enter Bkash Amount");


            } else {

                progressDialog.show();
                bKashAmount = Integer.parseInt(amount);

                if (bKashAmount >= 100) {

                    if (clickBalanceControl.getBalance() >= bKashAmount) {
                        String bKish = "BKash";
                        confirmAlert(bKashAmount, bKashNumber, bKish, amount);


                    } else {
                        progressDialog.dismiss();
                        alertDialog("Sorry..!\nYou don't have enough Balance", "Yes", "Read Rules more..");
                    }

                } else {
                    progressDialog.dismiss();
                    alertDialog("Minimum TK100 need for withdraw", "OK", "Read Rules more..");
                }


            }


        } else if (spinnerValue.equals("Rocket")) {

            rocketNumber = rocketNumberET.getText().toString().trim();
            final String Ramount = rocketAmountET.getText().toString().trim();

            if (rocketNumber.isEmpty() || rocketNumber.length() > 12 || rocketNumber.length() < 12) {

                rocketNumberET.setError("Please Enter Rocket Number");


            } else if (Ramount.isEmpty()) {
                rocketAmountET.setError("Please Enter Rocket amount");


            } else {

                progressDialog.show();
                rocketAmount = Integer.parseInt(Ramount);

                if (rocketAmount >= 100) {

                    if (clickBalanceControl.getBalance() >= rocketAmount) {

                        String rocket = "Rocket";
                        confirmAlert(rocketAmount, rocketNumber, rocket, Ramount);

                    } else {
                        progressDialog.dismiss();
                        alertDialog("Sorry..!\nYou don't have enough Balance", "Yes", "Read Rules more..");
                    }
                } else {

                    progressDialog.dismiss();
                    alertDialog("Minimum TK100 need for withdraw", "OK", "Read Rules more..");
                }


            }


        } else if (spinnerValue.equals("Mobile Recharge")) {


            rechargeNumber = mobileReNumberET.getText().toString().trim();
            final String MRamount = mobileReAmount.getText().toString().trim();

            if (rechargeNumber.isEmpty() || rechargeNumber.length() < 11 || rechargeNumber.length() > 11) {

                mobileReNumberET.setError("Please Enter  11 Digit Recharge Number");


            } else if (MRamount.isEmpty()) {
                mobileReAmount.setError("Please Enter Recharge Amount");


            } else {

                progressDialog.show();
                rechargeAmount = Integer.parseInt(MRamount);

                if (rechargeAmount >= 50) {

                    if (clickBalanceControl.getBalance() >= rechargeAmount) {

                        String recharge = "mobile Recharge";
                        confirmAlert(rechargeAmount, rechargeNumber, recharge, MRamount);

                    } else {
                        progressDialog.dismiss();
                        alertDialog("Sorry..!\nYou don't have enough Balance", "Yes", "Read Rules more..");

                    }
                } else {
                    progressDialog.dismiss();
                    alertDialog("Minimum TK50 need for withdraw", "OK", "Read Rules more..");

                }


            }


        } else {

            Toast.makeText(getContext(), "Could not match", Toast.LENGTH_SHORT).show();

        }


    }


    private void selectItem2() {

        spinnerValue = spinner.getSelectedItem().toString();


        if (spinnerValue.equals("PayPal")) {

            constraintLayout1.setVisibility(View.VISIBLE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.GONE);


        } else if (spinnerValue.equals("BKash")) {
            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.VISIBLE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.GONE);

        } else if (spinnerValue.equals("Rocket")) {
            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.VISIBLE);
            constraintLayout4.setVisibility(View.GONE);


        } else if (spinnerValue.equals("Mobile Recharge")) {

            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.VISIBLE);


        } else {
            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.GONE);
        }
    }

    private void alertDialog(String massage, final String okMassage, final String cancel) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(massage)
                .setCancelable(false)
                .setPositiveButton(okMassage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (okMassage.equals("Yes")) {

                            RulesFragment rulesFragment = new RulesFragment();
                            FragmentManager manager = getFragmentManager();
                            manager.beginTransaction().replace(R.id.show_allFragment_id,rulesFragment)
                                    .commit();

                        } else if (okMassage.equals("OK")) {
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Not working", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cancel.equals("Read Rules more..")) {

                    RulesFragment rulesFragment = new RulesFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.show_allFragment_id,rulesFragment)
                            .commit();

                } else {
                    Toast.makeText(getContext(), "Not working", Toast.LENGTH_SHORT).show();
                }


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void completeAlert() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Congratulation! \nYour withdraw is successfully")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Home_Fragment home_fragment = new Home_Fragment();
                        FragmentManager manager = getFragmentManager();
                        manager.beginTransaction().replace(R.id.show_allFragment_id,home_fragment)
                                .commit();


                    }
                }).setNegativeButton("Go to home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Home_Fragment home_fragment = new Home_Fragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.show_allFragment_id,home_fragment)
                        .commit();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void confirmAlert(final int taka, final String number, final String rechargeType, final String amount) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirmation Massage")
                .setMessage("your withdraw balance " + taka + "\nAre you sure this " + number + " is correct?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        clickBalanceControl.Withdraw(taka);
                        String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                        myRef.child(uID).child("ConvertBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {


                                    submit = new WithdrawSubmit(time, rechargeType, number, amount);
                                    myRef.child("Withdraw").child(pushId).setValue(submit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                completeAlert();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Withdraw is Field", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Please check your net connection ", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }



}
