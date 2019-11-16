package com.somon.bdcash;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class SignUp_Fragment extends Fragment {


    public SignUp_Fragment() {
        // Required empty public constructor
    }

    private Button signUpButton;
    CountryCodePicker countryCodePicker;
    EditText phoneNumberET;
    ProgressDialog progressDialog;
    TextView showTV;
    private String number;

    FirebaseAuth auth;

    Button confirmButton;
    EditText codeET;
    String varificationId;
    String countryCodeNumber;


    ConstraintLayout sendCodeForm,conformCodeForm;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_sign_up_, container, false);


        signUpButton = root.findViewById(R.id.signUpButton_id);
        countryCodePicker = root.findViewById(R.id.countryCodePicker_Id);
        phoneNumberET= root.findViewById(R.id.signUpNumber_id);



        progressDialog = new ProgressDialog(getContext());
        auth= FirebaseAuth.getInstance();
        confirmButton = root.findViewById(R.id.confirmCode_Id);
        codeET = root.findViewById(R.id.codeEditT_id);
        sendCodeForm = root.findViewById(R.id.sendCodeForm_Id);
        conformCodeForm = root.findViewById(R.id.confirmCodeForm_id);
        showTV = root.findViewById(R.id.showNumber_id);

        conformCodeForm.setVisibility(View.GONE);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeNumber = codeET.getText().toString().trim();
                progressDialog.show();

                if (codeNumber.isEmpty() || codeNumber.length()<6){

                    codeET.setError("Please Enter Code...");
                    codeET.requestFocus();
                    progressDialog.dismiss();
                    return;


                }else {
                    verifyCode(codeNumber);
                    progressDialog.dismiss();


                }
            }
        });




        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (haveNetwork()){
                    phoneAuthentication();

                }else {

                    Toast.makeText(getContext(), "Please check your net connection !", Toast.LENGTH_SHORT).show();
                }



            }
        });


        return root;
    }

    private void phoneAuthentication() {
        countryCodeNumber = countryCodePicker.getSelectedCountryCodeWithPlus();
        String phoneNumber = phoneNumberET.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {

            phoneNumberET.setError("Please Enter the Correct Phone Number");
            phoneNumberET.requestFocus();
            return;
        }
        else {
                number = countryCodeNumber + phoneNumber;
                showTV.setText("Your Number: "+number);
                sentVarificationCode(number);

            }

        }



    private boolean haveNetwork ()
    {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
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

    private void signInWithCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            startActivity(new Intent(getContext(),UserActivity.class));
                            progressDialog.dismiss();

                        }else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }



    private void sentVarificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
        progressDialog.show();
        sendCodeForm.setVisibility(View.GONE);
        conformCodeForm.setVisibility(View.VISIBLE);


    }

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            varificationId = s;



        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            progressDialog.show();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null)
            {
                codeET.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void verifyCode( String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(varificationId,code);
        signInWithCredential(credential);


    }

}
