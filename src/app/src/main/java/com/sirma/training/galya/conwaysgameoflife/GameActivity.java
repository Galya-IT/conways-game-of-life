package com.sirma.training.galya.conwaysgameoflife;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.sirma.training.galya.conwaysgameoflife.customviews.GridContainerView;

import java.util.HashSet;
import java.util.Set;

public class GameActivity extends Activity {
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int GAME_UPDATE_INTERVAL_MILLISEC = 1500;

    private float CELL_BORDER_WIDTH;
    private int CELL_BORDER_COLOR;
    private int ALIVE_CELL_COLOR;
    private int DEAD_CELL_COLOR;
    private SquareCellsGrid squareCellsGrid;
    private GridContainerView gridContainerView;
    private boolean isGameRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridContainerView = (GridContainerView) findViewById(R.id.gridContainerView);
        gridContainerView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        CELL_BORDER_WIDTH = getResources().getDimension(R.dimen.cell_border_width);
        CELL_BORDER_COLOR = Color.parseColor(getResources().getString(R.string.color_marine));
        ALIVE_CELL_COLOR = Color.parseColor(getResources().getString(R.string.color_black));
        DEAD_CELL_COLOR = Color.parseColor(getResources().getString(R.string.color_gray));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.start_stop_button) {
            if (getResources().getString(R.string.start_game).equals(item.getTitle())) {
                // isGameRunning = true;
                // squareCellsGrid.startGame();
                startGame();
                item.setTitle(R.string.stop_game);
            } else {
                isGameRunning = false;
                item.setTitle(R.string.start_game);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                gridContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                gridContainerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }

            float gridContainerWidth = gridContainerView.getWidth();
            float gridContainerHeight = gridContainerView.getHeight();

            squareCellsGrid = new SquareCellsGrid(gridContainerWidth, gridContainerHeight, ROWS, COLS, CELL_BORDER_WIDTH, CELL_BORDER_COLOR, ALIVE_CELL_COLOR, DEAD_CELL_COLOR);
            gridContainerView.setGrid(squareCellsGrid);
            gridContainerView.invalidate();

            gridContainerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();

                        if (!isGameRunning) {
                            if (squareCellsGrid.containsPoint(x, y)) {
                                Cell clickedCell = squareCellsGrid.getCellByPoint(x, y);
                                squareCellsGrid.triggerCellAliveStatus(clickedCell);
                                gridContainerView.invalidate();
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.stop_to_change_msg, Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                    return false;
                }
            });
        }
    };

    private void startGame() {
        isGameRunning = true;

        Thread gameLoopThread = new Thread() {
            public void run() {
                gameHandler.sendEmptyMessage(1);
            }
        };
        gameLoopThread.run();
    }

    private Handler gameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isGameRunning) {
                Set<Cell> cellsToDie = new HashSet<Cell>();

                Set<Cell> livingCells = squareCellsGrid.getLivingCells();

                for (Cell livingCell : livingCells) {
                    Set<Cell> livingNeighbours = squareCellsGrid.getAliveCellNeighbours(livingCell);
                    int livingNeighboursCount = livingNeighbours.size();

                    // Any live cell with fewer than two live neighbours dies, as if caused by under-population.
                    // Any live cell with two or three live neighbours lives on to the next generation.
                    // Any live cell with more than three live neighbours dies, as if by overcrowding.
                    if (livingNeighboursCount != 2 && livingNeighboursCount != 3) {
                        cellsToDie.add(livingCell);
                    }
                }

                // Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
                Set<Cell> allCellsWithThreeNeighbours = squareCellsGrid.findAllCellsWithThreeLiveNeighbours();

                squareCellsGrid.addLivingCells(allCellsWithThreeNeighbours);
                squareCellsGrid.removeLivingCells(cellsToDie);
                gridContainerView.invalidate();

                gameHandler.sendEmptyMessageDelayed(1, GAME_UPDATE_INTERVAL_MILLISEC);
            }
        }
    };
}
