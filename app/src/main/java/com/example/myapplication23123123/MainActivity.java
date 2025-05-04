package com.example.myapplication23123123;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private ProgressBar progressBar;
    private TextView scoreText;
    private Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        progressBar = findViewById(R.id.progressBar);
        scoreText = findViewById(R.id.scoreText);
        restartButton = findViewById(R.id.restartButton);

        progressBar.setMax(10);
        updateScore(0);
        restartButton.setVisibility(View.GONE);

        gameView.setOnScoreChangeListener(new GameView.ScoreChangeListener() {
            @Override
            public void onScoreChanged(int score, boolean blueCubeVisible) {
                updateScore(score);
                if (!blueCubeVisible) {
                    restartButton.setVisibility(View.VISIBLE);
                }
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
    }

    private void updateScore(int score) {
        progressBar.setProgress(score);
        scoreText.setText(score + "/10");
    }

    private void restartGame() {
        gameView.resetGame();
        restartButton.setVisibility(View.GONE);
        updateScore(0);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Убираем обработку касаний из Activity, переносим в GameView
        return super.onTouchEvent(event);
    }
}