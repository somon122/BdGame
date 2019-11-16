package com.somon.bdcash;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {


    Home_Fragment home_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       home_fragment = new Home_Fragment();
       replaceFragment(home_fragment);



    }



        private void replaceFragment(Fragment fragment){

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.show_allFragment_id,fragment).commit();


    }








}