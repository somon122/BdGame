package com.somon.bdcash;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class Opening_Fragment extends Fragment {


    public Opening_Fragment() {
        // Required empty public constructor
    }

    private int progress;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_opening_, container, false);

        progressBar = root.findViewById(R.id.progressBar2);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                    doTheWork();
                    startApp();

            }
        });
        thread.start();



        return root;

    }

    private void startApp() {

        startActivity(new Intent(getContext(),Home.class));
        getActivity().finish();

    }


        private void doTheWork() {

        for (progress = 20; progress <= 100; progress = progress+20){
        try {
        Thread.sleep(1000);
        progressBar.setProgress(progress);

        } catch (InterruptedException e) {
        e.printStackTrace();
        }
        }

    }


}