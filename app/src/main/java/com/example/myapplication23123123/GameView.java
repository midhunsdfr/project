package com.example.myapplication23123123;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class GameView extends View {
    private Rect redCube, blackCube, blueCube, greenCube;
    private Paint paint, textPaint;
    private int score = 0;
    private boolean redCubeVisible = false;
    private boolean blackCubeVisible = false;
    private boolean blueCubeVisible = true;
    private boolean greenCubeVisible = true;
    private boolean isDragging = false;
    private boolean draggingRed = false;
    private boolean buttonsVisible = false;
    private final int movingCubeSize = 120;
    private final int targetCubeSize = 150;
    private final int sizeIncrease = 30;
    private Random random = new Random();
    private boolean gameEnded = false;
    private int difficulty = 1;
    private int centerY;
    private String winMessage = "";

    public interface GameEventListener {
        void onScoreChanged(int score);
        void onGameEnd(boolean isWin);
        void onDifficultyIncreased(int newDifficulty);
    }

    private GameEventListener gameEventListener;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        redCube = new Rect();
        blackCube = new Rect();
        blueCube = new Rect();
        greenCube = new Rect();

        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerY = h * 2/3;
        resetCubesPosition();
        spawnRandomCube();
    }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void resetGame() {
        score = 0;
        winMessage = "";
        gameEnded = false;
        buttonsVisible = false;
        isDragging = false;
        draggingRed = false;
        blueCubeVisible = true;
        greenCubeVisible = true;
        resetCubesPosition();
        spawnRandomCube();
        invalidate();
    }

    private void resetCubesPosition() {
        if (getWidth() == 0 || getHeight() == 0) return;

        int centerX = getWidth()/2;

        greenCube.set(
                centerX - targetCubeSize - 400,
                centerY - targetCubeSize/2,
                centerX - 400,
                centerY + targetCubeSize/2
        );

        blueCube.set(
                centerX + 400,
                centerY - targetCubeSize/2,
                centerX + 400 + targetCubeSize,
                centerY + targetCubeSize/2
        );
    }

    public void setButtonsVisible(boolean visible) {
        buttonsVisible = visible;
        gameEnded = visible;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameEnded || buttonsVisible) return false;
        if (!redCubeVisible && !blackCubeVisible) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (redCubeVisible && redCube.contains((int)event.getX(), (int)event.getY())) {
                    isDragging = true;
                    draggingRed = true;
                    return true;
                }
                if (blackCubeVisible && blackCube.contains((int)event.getX(), (int)event.getY())) {
                    isDragging = true;
                    draggingRed = false;
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    if (draggingRed && redCubeVisible) {
                        redCube.offsetTo((int)(event.getX() - movingCubeSize/2),
                                (int)(event.getY() - movingCubeSize/2));
                        checkCollision(redCube, true);
                    } else if (!draggingRed && blackCubeVisible) {
                        blackCube.offsetTo((int)(event.getX() - movingCubeSize/2),
                                (int)(event.getY() - movingCubeSize/2));
                        checkCollision(blackCube, false);
                    }
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void checkCollision(Rect movingCube, boolean isRed) {
        if (blueCubeVisible && Rect.intersects(movingCube, blueCube)) {
            handleCollision(isRed, true);
        } else if (greenCubeVisible && Rect.intersects(movingCube, greenCube)) {
            handleCollision(isRed, false);
        }
    }

    private void handleCollision(boolean isRed, boolean withBlue) {
        if (isRed) {
            redCubeVisible = false;
            if (withBlue) {
                score++;
                blueCube.inset(-sizeIncrease/2, -sizeIncrease/2);
            } else {
                score = Math.max(0, score-1);
            }
        } else {
            blackCubeVisible = false;
            if (!withBlue) {
                score++;
                greenCube.inset(-sizeIncrease/2, -sizeIncrease/2);
            } else {
                score = Math.max(0, score-1);
            }
        }

        if (gameEventListener != null) {
            gameEventListener.onScoreChanged(score);
        }

        if (score >= 10) {
            winGame();
            return;
        }

        postDelayed(() -> {
            if (!gameEnded) {
                spawnRandomCube();
                invalidate();
            }
        }, 500);
    }
    public void endGame(boolean isWin) {
        gameEnded = true;
        buttonsVisible = true; // Показываем кнопки (если у вас есть UI-кнопки, управляйте их видимостью вне этого класса)
        if (isWin) {
            winMessage = "Вы победили!";
        } else {
            winMessage = "Вы проиграли!";
        }
        invalidate();

        if (gameEventListener != null) {
            gameEventListener.onGameEnd(isWin);
        }
    }

    void winGame() {
        endGame(true);
    }

    public void increaseDifficulty() {
        difficulty++;
        if (gameEventListener != null) {
            gameEventListener.onDifficultyIncreased(difficulty);
        }
    }

    private void spawnRandomCube() {
        if (getWidth() == 0) return;

        int centerX = getWidth()/2;
        if (random.nextBoolean()) {
            redCube.set(
                    centerX - movingCubeSize/2,
                    centerY - movingCubeSize/2,
                    centerX + movingCubeSize/2,
                    centerY + movingCubeSize/2
            );
            redCubeVisible = true;
            blackCubeVisible = false;
        } else {
            blackCube.set(
                    centerX - movingCubeSize/2,
                    centerY - movingCubeSize/2,
                    centerX + movingCubeSize/2,
                    centerY + movingCubeSize/2
            );
            blackCubeVisible = true;
            redCubeVisible = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.LTGRAY);

        if (greenCubeVisible) {
            paint.setColor(Color.GREEN);
            canvas.drawRect(greenCube, paint);
        }

        if (blueCubeVisible) {
            paint.setColor(Color.BLUE);
            canvas.drawRect(blueCube, paint);
        }

        if (redCubeVisible) {
            paint.setColor(Color.RED);
            canvas.drawRect(redCube, paint);
        }

        if (blackCubeVisible) {
            paint.setColor(Color.BLACK);
            canvas.drawRect(blackCube, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        //canvas.drawText("Уровень: " + difficulty, 50, 100, paint);
        //canvas.drawText("Очки: " + score, 50, 160, paint);

        if (!winMessage.isEmpty()) {
            canvas.drawText(winMessage, getWidth()/2, getHeight()/2, textPaint);
        }
    }
}