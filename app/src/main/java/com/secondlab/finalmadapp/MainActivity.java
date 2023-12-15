package com.secondlab.finalmadapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.Arrays;
import java.util.Random;
public class MainActivity extends AppCompatActivity {
    int sequenceCount = 4;
    private final int RED = 2;
    private final int BLUE = 1;
    private final int YELLOW = 3;
    private final int GREEN = 4;

    int[] gameSequence = new int[100];

    public int score;

    TextView tvDirection, tvScore;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    Button red, blue, green, yellow;
    int arrayIndex = 0;
    Random r = new Random();
    public boolean pressed;
    boolean isTimerRunning = false;
    CountDownTimer ct = new CountDownTimer(6000, 1500) {
        public void onTick(long millisUntilFinished) {
            oneButton();
        }

        public void onFinish() {
            pressed = false;

            Log.d("game sequence", Arrays.toString(gameSequence));

            // Create a new sequence with the updated array index
            int[] newGameSequence = new int[arrayIndex];
            System.arraycopy(gameSequence, 0, newGameSequence, 0, arrayIndex);

            // Start GamePlayActivity with the new sequence and updated score
            startGamePlayActivity(newGameSequence, score, arrayIndex);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        tvDirection = findViewById(R.id.tvDirection);
        red = findViewById(R.id.red);
        blue = findViewById(R.id.blue);
        green = findViewById(R.id.green);
        yellow = findViewById(R.id.yellow);
        Intent i = getIntent();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void play(View view) {
        if (!isTimerRunning) {
            // Start the timer and reset arrayIndex
            ct.start();
            isTimerRunning = true;
        }
    }
    private int requestCodeCounter = 1;

    private void startGamePlayActivity(int[] gameSequence, int currentScore, int newArrayIndex) {
        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);
        intent.putExtra("sequenceCount", sequenceCount);
        intent.putExtra("seqArray", gameSequence);
        intent.putExtra("score", String.valueOf(currentScore));
        intent.putExtra("newArrayIndex", newArrayIndex);

        // Pass the entire gameSequence array to GamePlayActivity
        intent.putExtra("gameSequence", gameSequence);

        // Use a unique request code to distinguish between different rounds
        int requestCode = requestCodeCounter++;
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            int newArrayIndex = data.getIntExtra("newArrayIndex", -1);
            int newScore = data.getIntExtra("score", -1);

            // Handle the new arrayIndex and score as needed
            arrayIndex = newArrayIndex;
            score = newScore;

            // Retrieve the entire gameSequence array
            int[] newGameSequence = data.getIntArrayExtra("newGameSequence");
            if (newGameSequence != null) {
                // Update the gameSequence array in MainActivity
                gameSequence = newGameSequence;

                // Debug statement to check the size of the new gameSequence array
                Log.d("MainActivity", "New gameSequence size: " + newGameSequence.length);
            }

            // Debug statement to check the updated arrayIndex and score
            Log.d("MainActivity", "Updated arrayIndex: " + arrayIndex + ", Updated score: " + score);

            // Update the UI with the new sequence
            updateSequenceView(arrayIndex);
        }
    }

    private void updateSequenceView(int a) {
        // Debug statement to check arrayIndex before flashing buttons
        Log.d("MainActivity", "Flashing buttons for arrayIndex: " + arrayIndex);

        for (int i = 0; i < a; i++) {
            // Get the color from the gameSequence array
            int color = gameSequence[i];

            // Get the corresponding button for the color
            Button button = getButtonForColor(color);

            // Flash the button
            flashButton(button);

            // Delay between button flashes (adjust as needed)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void oneButton() {
        int color = getRandomColor();
        flashButton(getButtonForColor(color));
        gameSequence[arrayIndex++] = color;
    }

    private int getRandomColor() {
        return getRandom(sequenceCount);
    }

    private Button getButtonForColor(int color) {
        switch (color) {
            case RED:
                return red;
            case BLUE:
                return blue;
            case GREEN:
                return green;
            case YELLOW:
                return yellow;
            default:
                return null; // Handle this case as needed
        }
    }

    private void flashButton(Button fb) {
        if (fb == null) {
            return; // Handle null button as needed
        }

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                fb.setPressed(true);
                fb.invalidate();
                fb.performClick();
                Handler handler1 = new Handler();
                Runnable r1 = new Runnable() {
                    public void run() {
                        fb.setPressed(false);
                        fb.invalidate();
                    }
                };
                handler1.postDelayed(r1, 600);
            } // end runnable
        };
        handler.postDelayed(r, 600);
    }

    private int getRandom(int maxValue) {
        return ((int) ((Math.random() * maxValue) + 1));
    }}
