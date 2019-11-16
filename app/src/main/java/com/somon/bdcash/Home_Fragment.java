package com.somon.bdcash;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class Home_Fragment extends Fragment {


    public Home_Fragment() {
        // Required empty public constructor
    }


    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    String interstitialAd = "ca-app-pub-3940256099942544/1033173712";

    private Button tapButton;
    private TextView scoreShow, tapCount;
    private SharedPreferences myScoreStore;

    private Random r;
    private int degree = 0, degree_old = 0;
    private static final float FACTOR = 15f;
    private ImageView imageView;

    private MediaPlayer player;

    private int score = 0;

    FirebaseAuth auth;
    FirebaseUser user;

    private static final long START_TIME_IN_MILLIS = 50000;

    //private static final long START_TIME_IN_MILLIS = 3657000;

    private TextView waitingTV;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private CountDownTimer countDownTimer;
    private long timeLeft = 10000;
    private boolean timeRunning;
    private String timeText;
    private ProgressBar progressBar;

    private ControlClass controlClass;
    private int showScore;



    SharedPreferences.Editor editor;
    int waitingScore;
    int lastScore;


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home_, container, false);




           if (haveNetwork()){

               mAdView = root.findViewById(R.id.adView);
               tapButton = root.findViewById(R.id.tapButton_id);
               imageView = root.findViewById(R.id.imageView);
               scoreShow = root.findViewById(R.id.scoreTV_id);
               waitingTV = root.findViewById(R.id.waiting_id);
               progressBar= root.findViewById(R.id.progressBar);
               tapCount = root.findViewById(R.id.showScore);
               inisilization();


           }else {
               Toast.makeText(getContext(), "Net connection is problem ", Toast.LENGTH_SHORT).show();

           }


        return root;

    }

    private void inisilization() {

        waitingTV.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tapButton.setEnabled(false);
        controlClass = new ControlClass(getContext());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null){
            uId = user.getUid();
            balanceControl();
            startTimerLoad();
        }
        MobileAds.initialize(getContext(),getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(interstitialAd);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        r = new Random();

        Bundle bundle = getArguments();
        if (bundle != null){
            waitingScore++;
            // Toast.makeText(getContext(), waitingScore, Toast.LENGTH_SHORT).show();

        }


        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startTimer();


                {
                    degree_old = degree % 360;
                    degree = r.nextInt(3600) + 720;

                    RotateAnimation animationRotate = new RotateAnimation(degree_old,degree,RotateAnimation.RELATIVE_TO_SELF,
                            0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    animationRotate.setDuration(3600);
                    animationRotate.setFillAfter(true);
                    animationRotate.setInterpolator(new DecelerateInterpolator());
                    animationRotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            tapButton.setEnabled(false);

                            if (player == null){
                                player = MediaPlayer.create(getContext(),R.raw.sound);
                                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        stopPlayer();
                                    }
                                });
                            }
                            player.start();


                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            stopPlayer();
                            courrentNumber(360 - (degree%360));

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    imageView.startAnimation(animationRotate);

                }

            }
        });

        //scoreShow.setText("Score is "+controlClass.getScore());


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {


                Toast.makeText(getContext(), "You are doing mistake...!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {


                int addScore = lastScore + score;
                String setScore = String.valueOf(addScore);

                myRef.child(uId).child("Balance").setValue(setScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gameOver(score);

                    }
                });



            }

        });


    }


    private void reLoaded_Ads(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Your net connection is slow\n\nCan you try again? ")
                .setCancelable(false)
                .setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                     Home_Fragment home_fragment = new Home_Fragment();
                     FragmentManager manager = getFragmentManager();
                     manager.beginTransaction().replace(R.id.show_allFragment_id,home_fragment).commit();


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void balanceControl() {

        tapCount.setText("Show: "+controlClass.getScore());

        // Read from the database
        myRef.child(uId).child("Balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    lastScore = Integer.parseInt(value);
                    scoreShow.setText("T.Points: "+value);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



    }

    private void stopPlayer() {
        if (player != null){
            player.release();
            player=null;
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        //updateCountDownText();
        //updateButtons();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                //updateButtons();
                resetTimer();
            } else {
                waitingScore++;
                startTimer();
            }
        }

        Bundle bundle = getArguments();
        if (bundle != null){
            startTimer();

        }


    }


    @Override
    public void onStop() {
        super.onStop();

        if (player != null){
            stopPlayer();

        }
        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //updateButtons();
                resetTimer();

            }
        }.start();

        mTimerRunning = true;
        //updateButtons();
    }


    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        //updateButtons();
        tapButton.setVisibility(View.VISIBLE);
        tapButton.setEnabled(false);
        waitingTV.setVisibility(View.GONE);
        if (mInterstitialAd.isLoaded()){

            tapButton.setEnabled(true);

        }else {
            reLoaded_Ads();
        }

    }

    private void updateCountDownText() {
        int hour = (int) ((mTimeLeftInMillis/1000) /60) /60;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",hour, minutes, seconds);

        if (waitingScore == 1){
            waitingTV.setVisibility(View.VISIBLE);
            tapButton.setVisibility(View.GONE);
            waitingTV.setText("Wait for next Work.."+"\n\n"+timeLeftFormatted);
        }


    }



    private boolean haveNetwork ()
    {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
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



    private String courrentNumber (int degrees){
        String text = "";

        if (degrees >= (FACTOR *1) && degrees < (FACTOR * 3)){

            score = score+1;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *3) && degrees < (FACTOR * 5)){

            score = score+2;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *5) && degrees < (FACTOR * 7)){

            score = score+3;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *7) && degrees < (FACTOR * 9)){

            score = score+4;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *9) && degrees < (FACTOR * 11)){

            score = score+5;
            mInterstitialAd.show();


        } if (degrees >= (FACTOR *11) && degrees < (FACTOR * 13)){

            score = score+6;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *13) && degrees < (FACTOR * 15)){

            score = score+7;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *15) && degrees < (FACTOR * 17)){

            score = score+8;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *17) && degrees < (FACTOR * 19)){

            score = score+9;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *19) && degrees < (FACTOR * 21)){

            score = score+10;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *21) && degrees < (FACTOR * 23)){

            score = score+11;
            mInterstitialAd.show();

        }

        if ((degrees >= (FACTOR * 23 ) && degrees < 360) || (degrees >= 0 && degrees < (FACTOR * 1)))
        {

            score = score+12;
            mInterstitialAd.show();

        }

        return text;

    }

    private void gameOver(final int mScore){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Congratulation..!"+"\n\n"+"You Got : "+mScore+" point"+
                "\n\n"+" Click Ok For Continue Game ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (controlClass.getScore() >= 25){
                            Click_Fragment click_fragment = new Click_Fragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("interstitialAd",interstitialAd);
                            click_fragment.setArguments(bundle);
                            FragmentManager manager = getFragmentManager();
                            manager.beginTransaction().replace(R.id.show_allFragment_id,click_fragment)
                                    .commit();

                        }else {

                            showScore++;
                            int tapCounter = controlClass.getScore()+showScore;
                            controlClass.scoreStore(tapCounter);

                            Home_Fragment home_fragment = new Home_Fragment();
                            FragmentManager manager = getFragmentManager();
                            manager.beginTransaction().replace(R.id.show_allFragment_id,home_fragment)
                                    .commit();

                        }

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void startTimerLoad() {
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
               if (mInterstitialAd.isLoaded()){
                   progressBar.setVisibility(View.GONE);
                   tapButton.setEnabled(true);
               }else {
                   progressBar.setVisibility(View.GONE);
                   reLoaded_Ads();

               }



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
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        // startBtn.setText("Start");



    }





}
