package com.example.myapplication23123123;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView scoreText, timeText;
    private Button restartButton, increaseDifficultyButton;
    private CountDownTimer timer;

    // Время для каждого уровня в миллисекундах
    private final int[] levelTimes = {20000, 15000, 20000};
    private int currentLevel = 1; // Текущий уровень (1..3)
    private boolean[] levelWins = new boolean[3]; // Отслеживаем победы по уровням

    private boolean gameRunning = true;
    private int currentScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        resetGame();
        setupGame();
    }

    private void initializeViews() {
        gameView = findViewById(R.id.gameView);
        scoreText = findViewById(R.id.scoreText);
        timeText = findViewById(R.id.timeText);
        restartButton = findViewById(R.id.restartButton);
        increaseDifficultyButton = findViewById(R.id.increaseDifficultyButton);
    }

    private void setupGame() {
        updateScore(0);
        hideButtons();

        gameView.setGameEventListener(new GameView.GameEventListener() {
            @Override
            public void onScoreChanged(int score) {
                runOnUiThread(() -> updateScore(score));
            }

            @Override
            public void onGameEnd(boolean isWin) {
                Log.d("Game", "onGameEnd called, isWin=" + isWin + ", currentLevel=" + currentLevel);
                runOnUiThread(() -> {
                    if (timer != null) timer.cancel();

                    if (isWin) {
                        levelWins[currentLevel - 1] = true; // Отмечаем победу на текущем уровне
                    }

                    if (levelWins[0] && levelWins[1] && levelWins[2]) {
                        // Если выиграны все три уровня
                        showWinScreen();
                    } else {
                        // Если текущий уровень меньше 3, переходим к следующему уровню
                        if (isWin && currentLevel < 3) {
                            currentLevel++;
                            Log.d("Game", "Переход к уровню " + currentLevel);
                            restartGame(); // Запускаем следующий уровень
                        } else {
                            // Иначе показываем кнопки окончания игры
                            showEndButtons();
                        }
                    }
                });
            }

            @Override
            public void onDifficultyIncreased(int newDifficulty) {
                // Этот метод можно оставить пустым или убрать, т.к. уровни теперь фиксированы
            }
        });

        restartButton.setOnClickListener(v -> {
            resetGame();
            restartGame();
        });

        increaseDifficultyButton.setOnClickListener(v -> {
            // Можно убрать или реализовать переход на следующий уровень, но сейчас уровни фиксированы
        });

        startTimerForCurrentLevel();
    }

    private void startTimerForCurrentLevel() {
        if (timer != null) {
            timer.cancel();
        }

        gameRunning = true;
        int gameTime = levelTimes[currentLevel - 1];
        timer = new CountDownTimer(gameTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                runOnUiThread(() -> timeText.setText("Время: " + seconds + " сек"));
            }

            @Override
            public void onFinish() {
                runOnUiThread(() -> {
                    timeText.setText("Время вышло!");
                    gameView.setButtonsVisible(true);
                    // Завершаем игру с проигрышем на текущем уровне
                    gameView.endGame(false);
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
        currentScore = 0;
        updateScore(0);
        gameView.resetGame();
        hideButtons();
        timeText.setText("Время: " + (levelTimes[currentLevel - 1] / 1000) + " сек");
        startTimerForCurrentLevel();
    }

    private void updateScore(int score) {
        currentScore = score;
        Log.d("Game", "Score updated: " + currentScore);
        scoreText.setText("Очки: " + score + "/10");

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
        timeText.setText("Победа на всех уровнях!");
    }

    private void showEndButtons() {
        Log.d("Game", "showEndButtons called с currentScore: " + currentScore);
        restartButton.setVisibility(View.VISIBLE);
        increaseDifficultyButton.setVisibility(View.GONE); // Убираем кнопку увеличения сложности, т.к. уровни фиксированы
    }

    private void hideButtons() {
        restartButton.setVisibility(View.GONE);
        increaseDifficultyButton.setVisibility(View.GONE);
    }

    private void resetGame() {
        currentLevel = 1;
        Arrays.fill(levelWins, false);
        currentScore = 0;
        gameRunning = true;
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
