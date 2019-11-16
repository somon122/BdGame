package com.somon.bdcash;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class emailLogInFragment extends Fragment {


    public emailLogInFragment() {
        // Required empty public constructor
    }


    Button signUpButton, logInButton;
    EditText emailEditText, passwordEditText;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_email_log_in, container, false);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        signUpButton = root.findViewById(R.id.gotoSignUpPageId);
        logInButton = root.findViewById(R.id.logInId);
        emailEditText = root.findViewById(R.id.logInEmailId);
        passwordEditText = root.findViewById(R.id.logInPasswordId);

        dialog = new ProgressDialog(getContext());

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Problem", Toast.LENGTH_SHORT).show();
                emailSignUpFragment emailSignUpFragment = new emailSignUpFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.show_allFragment_id, emailSignUpFragment).commit();
            }
        });


        return root;

    }

    private void isLogIn() {

        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Please Enter Valid Email Address");
        } else if (password.isEmpty()) {
            passwordEditText.setError("Please Enter Valid Password");
        } else {

            dialog.show();
            dialog.setMessage("LogIn is progressing ...");

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getContext(), "LogIn is successful", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getActivity(), Home.class));
                                /*Home_Fragment home_fragment = new Home_Fragment();
                                FragmentManager manager = getFragmentManager();
                                manager.beginTransaction().replace(R.id.show_allFragment_id,home_fragment)
                                        .commit();
*/

                            } else {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Email and Password is not match", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Something missing", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

}

