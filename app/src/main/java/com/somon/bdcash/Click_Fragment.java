package com.somon.bdcash;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;



public class Click_Fragment extends Fragment {


    public Click_Fragment() {

    }

    private TextView showScore;
    private Button clickButton;

    private InterstitialAd mInterstitialAd;
    private String interstitialAd;
    private int adCount;


    private CountDownTimer countDownTimer;
    private long timeLeft = 50000;
    private boolean timeRunning;
    private String timeText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_click_, container, false);



        showScore = root.findViewById(R.id.clickScoreShow_id);
        clickButton = root.findViewById(R.id.clickButton_id);

        Bundle bundle = getArguments();
        if (bundle != null){

            interstitialAd = bundle.getString("interstitialAd");

        }




        MobileAds.initialize(getContext(),getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(interstitialAd);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        clickButton.setVisibility(View.GONE);



        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else {

                    Toast.makeText(getContext(), " Try Again.. Ok! ", Toast.LENGTH_SHORT).show();

                }


            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                clickButton.setVisibility(View.VISIBLE);

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getContext(), "Please try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {

                startTimer();


            }

            @Override
            public void onAdClosed() {

                if (adCount >=1){
                    ControlClass controlClass = new ControlClass(getContext());
                    controlClass.Delete();

                    Home_Fragment home_fragment = new Home_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("success","success");

                    home_fragment.setArguments(bundle);
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.show_allFragment_id,home_fragment)
                            .commit();

                }else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Toast.makeText(getContext(), " Try Again.. Ok! ", Toast.LENGTH_SHORT).show();

                }


            }

        });





        return root;

    }

    private void startTimer() {
        if (timeRunning){
            stopTime();
        }else {
            startTime();
        }

    }


    private void startTime() {
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft =millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {
                adCount++;



            }
        }.start();
        timeRunning = true;
        //startBtn.setText("Pause");

    }

    private void updateTimer() {

        int minutes = (int) (timeLeft /60000);
        int seconds = (int) (timeLeft % 60000 /1000);
        timeText = ""+minutes;
        timeText += ":";
        if (seconds <10)timeText += "0";
        timeText +=seconds;
        Toast.makeText(getContext(), "Wait: "+timeText, Toast.LENGTH_SHORT).show();

    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        // startBtn.setText("Start");



    }




}
