package com.secondlab.finalmadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class GameOverActivity extends AppCompatActivity {
    TextView score;
    EditText editText;
    ListView lv;
    List<String> highScoresStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        lv = findViewById(R.id.listview1);
        score = findViewById(R.id.score);
        lv = findViewById(R.id.listview1);
        score = findViewById(R.id.score);

        Intent i = getIntent();
        int sequenceScore = getIntent().getIntExtra("score", 0); // Default value is 0
        score.setText(String.valueOf(sequenceScore));
        MySQLLiteHelper db = new MySQLLiteHelper(this);

        List<HiScore> highscore = db.getAllHighscore();


       //db.emptyHighscore(); // empty table

        // Inserting High Scores
        //db.addHighscore(new HiScore("John", 1));

        // Reading all high scores
        List<HiScore> hiScoreList = db.top5Highscore();

        // Display high scores in ListView
        displayHighScores(hiScoreList);
    }

    private void displayHighScores(List<HiScore> highScoreList) {
        highScoresStr = new ArrayList<>();
        int j = 1;
        for (HiScore cn2 : highScoreList) {
            String log = j++ + ": " + cn2.getName() + "\t" + cn2.getHiscore();
            highScoresStr.add(log);
            Log.i("High Score: ", log);
        }

        ArrayAdapter itemsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, highScoresStr);
        lv.setAdapter(itemsAdapter);
    }

    public void addHighScore(View view) {
        editText = findViewById(R.id.editTextTextPersonName);
        score = findViewById(R.id.score);

        String name = String.valueOf(editText.getText());
        String highscore = String.valueOf(score.getText());

        if (!name.isEmpty()) {
            // Retrieve updated high scores after adding a new one
            MySQLLiteHelper db = new MySQLLiteHelper(this);
            db.addHighscore(new HiScore(name, Integer.parseInt(highscore)));
            List<HiScore> updatedHighScoreList = db.top5Highscore();

            // Display updated high scores in ListView
            displayHighScores(updatedHighScoreList);
        } else {
            // Handle the case where the name is empty
            Toast.makeText(this, "Please enter a name before saving.", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextGame(View view) {
        // Start a new game or navigate back to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}