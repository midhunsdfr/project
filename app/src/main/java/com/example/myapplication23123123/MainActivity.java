package com.example.myapplication23123123;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private ProgressBar progressBar, timeBar;
    private TextView scoreText, timeText;
    private Button restartButton, increaseDifficultyButton;
    private CountDownTimer timer;
    private int gameTime = 20000;
    private boolean gameRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupGame();
    }

    private void initializeViews() {
        gameView = findViewById(R.id.gameView);
        progressBar = findViewById(R.id.progressBar);
        scoreText = findViewById(R.id.scoreText);
        timeBar = findViewById(R.id.timeBar);
        timeText = findViewById(R.id.timeText);
        restartButton = findViewById(R.id.restartButton);
        increaseDifficultyButton = findViewById(R.id.increaseDifficultyButton);
    }

    private void setupGame() {
        progressBar.setMax(10);
        timeBar.setMax(gameTime / 1000);
        updateScore(0);
        hideButtons();

        gameView.setGameEventListener(new GameView.GameEventListener() {
            @Override
            public void onScoreChanged(int score) {
                runOnUiThread(() -> updateScore(score));
            }

            @Override
            public void onGameEnd(boolean isWin) {
                runOnUiThread(() -> {
                    if (timer != null) timer.cancel();
                    if (isWin) {
                        showWinScreen();
                    } else {
                        showEndButtons();
                    }
                });
            }

            @Override
            public void onDifficultyIncreased(int newDifficulty) {
                gameTime = Math.max(10000, 20000 - (newDifficulty-1)*5000);
            }
        });

        restartButton.setOnClickListener(v -> restartGame());
        increaseDifficultyButton.setOnClickListener(v -> {
            gameView.increaseDifficulty();
            restartGame();
        });

        startTimer();
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        gameRunning = true;
        timer = new CountDownTimer(gameTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                runOnUiThread(() -> {
                    timeBar.setProgress(seconds);
                    timeText.setText(seconds + "s");
                });
            }

            @Override
            public void onFinish() {
                runOnUiThread(() -> {
                    timeText.setText("Время вышло!");
                    gameView.setButtonsVisible(true);
                    showEndButtons();
                });
            }
        }.start();
    }

    private void restartGame() {
        if (timer != null) {
            timer.cancel();
        }

        gameRunning = true;
        gameView.resetGame();
        hideButtons();
        updateScore(0);
        timeBar.setMax(gameTime / 1000);
        timeText.setText((gameTime / 1000) + "s");
        startTimer();
    }

    private void updateScore(int score) {
        progressBar.setProgress(score);
        scoreText.setText(score + "/10");

        if (score >= 10 && gameRunning) {
            gameRunning = false;
            if (timer != null) timer.cancel();
            gameView.winGame();
        }
    }

    private void showWinScreen() {
        gameView.setButtonsVisible(true);
        restartButton.setVisibility(View.VISIBLE);
        increaseDifficultyButton.setVisibility(View.GONE);
        timeText.setText("Победа!");
    }

    private void showEndButtons() {
        restartButton.setVisibility(View.VISIBLE);
        increaseDifficultyButton.setVisibility(
                progressBar.getProgress() >= 10 ? View.VISIBLE : View.GONE
        );
    }

    private void hideButtons() {
        restartButton.setVisibility(View.GONE);
        increaseDifficultyButton.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (gameView != null) {
            gameView.setGameEventListener(null);
        }
        super.onDestroy();
    }
}