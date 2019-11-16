package com.somon.bdcash;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class emailSignUpFragment extends Fragment {


    public emailSignUpFragment() {
        // Required empty public constructor
    }


    FirebaseAuth auth;
    FirebaseUser user;
    Button signUpButton,logInButton;
    EditText emailEditText,passwordEditText,confirmPasswordET;
    ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_email_sign_up, container, false);


        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        signUpButton = root.findViewById(R.id.signUpButton_id);
        logInButton = root.findViewById(R.id.logInButton_id);
        emailEditText = root.findViewById(R.id.signUpUsernameId);
        passwordEditText = root.findViewById(R.id.signUpPasswordId);
        confirmPasswordET = root.findViewById(R.id.confirmPasswordId);
        dialog = new ProgressDialog(getContext());


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegister();


            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogInFragment emailLogInFragment = new emailLogInFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.show_allFragment_id,emailLogInFragment).commit();
            }
        });




        return root;
    }



    private void isRegister()
    {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        if (TextUtils.isEmpty(email)){
            emailEditText.setError("Please Enter Valid Email Address");
        }else if (TextUtils.isEmpty(password)){
            passwordEditText.setError("Please Enter Valid Password");
        }else if (TextUtils.isEmpty(confirmPassword)){
            confirmPasswordET.setError("Please enter matching Password by above");
        }else {
            if (user == null){

                if (!password.equals(confirmPassword))
                {
                    Toast.makeText(getContext(), "Confirm Password could not match ", Toast.LENGTH_SHORT).show();

                }else {
                    dialog.show();
                    dialog.setMessage("Register is progressing ...");

                    auth.createUserWithEmailAndPassword(email,confirmPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        dialog.dismiss();
                                        emailLogInFragment emailLogInFragment = new emailLogInFragment();
                                        FragmentManager manager = getFragmentManager();
                                        manager.beginTransaction().replace(R.id.show_allFragment_id,emailLogInFragment).commit();
                                        Toast.makeText(getContext(), "Register is successfully", Toast.LENGTH_SHORT).show();

                                    }else {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Email and Password could not Valid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }else {
                dialog.dismiss();
                Toast.makeText(getContext(), "You have already Registered", Toast.LENGTH_SHORT).show();
            }



        }



    }


}


