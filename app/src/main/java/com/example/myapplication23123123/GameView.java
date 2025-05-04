package com.example.myapplication23123123;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private Rect redCube = new Rect();
    private Rect blueCube = new Rect();
    private Paint paint = new Paint();
    private int score = 0;
    private boolean redCubeVisible = true;
    private boolean isDragging = false;
    private boolean blueCubeVisible = true;
    private final int cubeSize = 100;
    private final int sizeIncrease = 50;

    private ScoreChangeListener scoreChangeListener;

    public interface ScoreChangeListener {
        void onScoreChanged(int score, boolean blueCubeVisible);
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        resetGame();
    }

    public void resetGame() {
        resetRedCube();
        blueCube.set(500, 500, 500 + cubeSize, 500 + cubeSize);
        blueCubeVisible = true;
        score = 0;
        invalidate();
    }

    public void setOnScoreChangeListener(ScoreChangeListener listener) {
        this.scoreChangeListener = listener;
    }

    private void resetRedCube() {
        redCube.set(50, 500, 50 + cubeSize, 500 + cubeSize);
        redCubeVisible = true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Проверяем, попали ли мы в красный кубик
                if (redCubeVisible && redCube.contains((int)event.getX(), (int)event.getY())) {
                    isDragging = true;
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging && redCubeVisible && blueCubeVisible) {
                    // Перемещаем красный кубик
                    redCube.offsetTo(
                            (int)(event.getX() - cubeSize / 2),
                            (int)(event.getY() - cubeSize / 2)
                    );

                    // Проверка столкновения
                    if (Rect.intersects(redCube, blueCube)) {
                        handleCollision();
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

    private void handleCollision() {
        redCubeVisible = false;
        score++;

        // Увеличиваем синий кубик
        blueCube.inset(-sizeIncrease / 2, -sizeIncrease / 2);

        if (score >= 10) {
            blueCubeVisible = false;
        }

        if (scoreChangeListener != null) {
            scoreChangeListener.onScoreChanged(score, blueCubeVisible);
        }

        postDelayed(() -> {
            if (blueCubeVisible) {
                resetRedCube();
                invalidate();
            }
        }, 500);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем синий кубик, если он видим
        if (blueCubeVisible) {
            paint.setColor(Color.BLUE);
            canvas.drawRect(blueCube, paint);
        }

        // Рисуем красный кубик, если он видим
        if (redCubeVisible && blueCubeVisible) {
            paint.setColor(Color.RED);
            canvas.drawRect(redCube, paint);
        }
    }
}