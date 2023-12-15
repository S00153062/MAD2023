package com.secondlab.finalmadapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;
public class GamePlayActivity extends AppCompatActivity implements SensorEventListener {
    private final int RED = 2;
    private final int BLUE = 1;
    private final int YELLOW = 3;
    private final int GREEN = 4;
    private static int requestCodeCounter = 1;
    int score;
    int clickCount = 0;
    int arrayIndex = 0;

    TextView tvScore;
    SensorManager mSensorManager;
    Sensor mSensor;
    Button red, blue, green, yellow;

    boolean isFlat = false;
    int[] gameSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        tvScore = findViewById(R.id.tvScore);
        red = findViewById(R.id.red);
        blue = findViewById(R.id.blue);
        green = findViewById(R.id.green);
        yellow = findViewById(R.id.yellow);

        Intent intent = getIntent();
        int SCount = intent.getIntExtra("sequenceCount", 0);
        gameSequence = intent.getIntArrayExtra("seqArray");
        score = Integer.parseInt(intent.getStringExtra("score"));
        arrayIndex = SCount;

        // Retrieve the entire gameSequence array
        int[] initialGameSequence = intent.getIntArrayExtra("gameSequence");
        if (initialGameSequence != null) {
            gameSequence = initialGameSequence;
        }


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setClickListeners();
    }
    private void setClickListeners() {
        red.setOnClickListener(v -> processClick(RED));
        blue.setOnClickListener(v -> processClick(BLUE));
        green.setOnClickListener(v -> processClick(GREEN));
        yellow.setOnClickListener(v -> processClick(YELLOW));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    public void onSensorChanged(SensorEvent event) {
        float x, y, z;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        Log.d("Sensor", "X: " + x + ", Y: " + y + ", Z: " + z);
        int maxTilt = 2;

        if (Math.abs(x) < 1 && Math.abs(y) < 1 && !isFlat) {
            isFlat = true;
        }

        int tiltedColor = -1;

        if (y < -maxTilt && isFlat) {
            tiltedColor = BLUE;
        } else if (y > maxTilt && isFlat) {
            tiltedColor = RED;
        } else if (x < -maxTilt && isFlat) {
            tiltedColor = GREEN;
        } else if (x > maxTilt && isFlat) {
            tiltedColor = YELLOW;
        }

        // Check if the tilted color matches the expected color
        if (tiltedColor != -1) {
            int expectedColor = gameSequence[clickCount];
            if (tiltedColor == expectedColor) {
                processClick(tiltedColor);

                isFlat = false;
            }
            else {goToHighScoreScreen();}
        }
    }

    /*private void handleColorChange(final int color, final String colorName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (color) {
                    case BLUE:
                        blue.performClick();
                        Toast.makeText(GamePlayActivity.this, "Color: " + colorName, Toast.LENGTH_SHORT).show();
                        break;
                    case RED:
                        red.performClick();
                        Toast.makeText(GamePlayActivity.this, "Color: " + colorName, Toast.LENGTH_SHORT).show();
                        break;
                    case GREEN:
                        green.performClick();
                        Toast.makeText(GamePlayActivity.this, "Color: " + colorName, Toast.LENGTH_SHORT).show();
                        break;
                    case YELLOW:
                        yellow.performClick();
                        Toast.makeText(GamePlayActivity.this, "Color: " + colorName, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }*/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void processClick(int buttonColor) {
        Log.d("GamePlayActivity", "Click detected: " + buttonColor + " ArrayIndex: " + arrayIndex + " gs: " + Arrays.toString(gameSequence));

        // Check if the user's tilt matches the current position in the sequence
        if (buttonColor == gameSequence[clickCount]) {
            clickCount++;
            score += 1;

            if (clickCount == arrayIndex) {
                if (arrayIndex <= 100) {
                    startNextRound();
                } else {
                    //goToHighScoreScreen();
                }
            }
        } else {
            // Incorrect tilt leads to immediate Game Over
            //goToHighScoreScreen();
        }

        Log.d("GamePlayActivity", "Current Click Count: " + clickCount + " ArrayIndex:" + arrayIndex + " GS.Length:" + gameSequence.length);
    }


    private void startNextRound() {
        arrayIndex += 2;
        goToHighScoreScreen();
        if (arrayIndex <= 100) {
            // Generate a new sequence if arrayIndex is within bounds
            int[] newSequence = generateNewGameSequence(arrayIndex);
            updateUI();
            clickCount = 0;

            // Ensure there is enough space in gameSequence
            if (arrayIndex + newSequence.length <= gameSequence.length) {
                // Copy the newSequence to the gameSequence starting from arrayIndex
                System.arraycopy(newSequence, 0, gameSequence, arrayIndex, newSequence.length);

                // Auto-increment the request code
                int requestCode = requestCodeCounter++;

                // Use a Handler to delay
                new Handler().postDelayed(() -> {
                    // Send the updated arrayIndex, score, and newGameSequence back to MainActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newArrayIndex", arrayIndex);
                    resultIntent.putExtra("score", score);

                    // Pass the entire updated gameSequence array
                    resultIntent.putExtra("newGameSequence", gameSequence);

                    // Set the result before starting the MainActivity
                    setResult(RESULT_OK, resultIntent);

                    // Finish the current activity with the incremented request code
                    finishActivityWithCode(requestCode);

                    // Start the main activity after finishing the current activity
                    Intent mainIntent = new Intent(GamePlayActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                    Log.d("GamePlayActivity", "Next Round Started! Array Index: " + arrayIndex);

                }, 1000);
            } else {
                int[] resizedGameSequence = new int[gameSequence.length + newSequence.length];
                System.arraycopy(gameSequence, 0, resizedGameSequence, 0, gameSequence.length);
                gameSequence = resizedGameSequence;

                // Retry
                startNextRound();
            }
        } else {
            goToHighScoreScreen();
        }
    }
    private int[] generateNewGameSequence(int length) {

        Random random = new Random();
        int[] newSequence = new int[length];
        for (int i = 0; i < newSequence.length; i++) {
            newSequence[i] = random.nextInt(4) + 1; // Generate random values between 1 and 4
        }
        return newSequence;
    }
    private void finishActivityWithCode(int requestCode) {
        Intent data = new Intent();
        data.putExtra("requestCode", requestCode);
        setResult(RESULT_OK, data);
        finish();
    }
    private void goToHighScoreScreen() {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
        Log.d("GamePlayActivity", "Going to High Score Screen");
    }

    private void updateUI() {
        // update score display, show the new sequence, etc.
        tvScore.setText("Score: " + score);
    }
}