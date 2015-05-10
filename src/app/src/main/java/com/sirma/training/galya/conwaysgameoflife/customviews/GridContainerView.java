package com.sirma.training.galya.conwaysgameoflife.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.sirma.training.galya.conwaysgameoflife.SquareCellsGrid;

public class GridContainerView extends View {
    private SquareCellsGrid grid;

    public GridContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGrid(SquareCellsGrid grid) {
        this.grid = grid;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (grid != null) {
            grid.onDraw(canvas);
        }
    }

}
