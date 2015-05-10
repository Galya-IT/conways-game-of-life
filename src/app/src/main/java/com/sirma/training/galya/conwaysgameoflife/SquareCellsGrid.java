package com.sirma.training.galya.conwaysgameoflife;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SquareCellsGrid {
    private int rows;
    private int cols;
    private float cellBorderWidth;
    private int cellBorderColor;
    private int aliveCellColor;
    private int deadCellColor;
    private float cellSize;

    private float gridXPos;
    private float gridYPos;
    private float gridWidth;
    private float gridHeight;

    private float[] coordinatesVerticalBorders;
    private float[] coordinatesHorizontalBorders;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Set<Cell> livingCells = new HashSet<Cell>();

    public SquareCellsGrid(float containerWidth, float containerHeight, int rows, int cols, float cellBorderWidth, int cellBorderColor, int aliveCellColor, int deadCellColor) {
        this.rows = rows;
        this.cols = cols;
        this.cellBorderWidth = cellBorderWidth;
        this.cellBorderColor = cellBorderColor;
        this.aliveCellColor = aliveCellColor;
        this.deadCellColor = deadCellColor;

        // makes the cell size to be the one of a square
        float initialCellWidth = containerWidth / cols;
        float initialCellHeight = containerHeight / rows;
        cellSize = (initialCellWidth < initialCellHeight) ? initialCellWidth : initialCellHeight;

        gridWidth = cellSize * cols;
        gridHeight = cellSize * rows;

        // centers the grid in the container
        gridXPos = (containerWidth - gridWidth) / 2;
        gridYPos = (containerHeight - gridHeight) / 2;

        // calculates the coordinates for the cell borders
        int verticalBordersCount = cols + 1;

        List<Float> coordinatesVerticalBordersList = new ArrayList<Float>();
        for (int borderNumber = 0; borderNumber < verticalBordersCount; borderNumber++) {
            float startXPos = gridXPos + (borderNumber * cellSize);
            float startYPos = gridYPos;
            float endXPos = startXPos;
            float endYPos = gridYPos + gridHeight;

            coordinatesVerticalBordersList.add(startXPos);
            coordinatesVerticalBordersList.add(startYPos);
            coordinatesVerticalBordersList.add(endXPos);
            coordinatesVerticalBordersList.add(endYPos);
        }

        coordinatesVerticalBorders = new float[coordinatesVerticalBordersList.size()];
        for (int i = 0; i < coordinatesVerticalBordersList.size(); i++) {
            coordinatesVerticalBorders[i] = coordinatesVerticalBordersList.get(i);
        }

        int horizontalBordersCount = rows + 1;

        List<Float> coordinatesHorizontalBordersList = new ArrayList<Float>();
        for (int borderNumber = 0; borderNumber < horizontalBordersCount; borderNumber++) {
            float startXPos = gridXPos;
            float startYPos = gridYPos + (borderNumber * cellSize);
            float endXPos = gridXPos + gridWidth;
            float endYPos = startYPos;

            coordinatesHorizontalBordersList.add(startXPos);
            coordinatesHorizontalBordersList.add(startYPos);
            coordinatesHorizontalBordersList.add(endXPos);
            coordinatesHorizontalBordersList.add(endYPos);
        }

        coordinatesHorizontalBorders = new float[coordinatesHorizontalBordersList.size()];
        for (int i = 0; i < coordinatesHorizontalBordersList.size(); i++) {
            coordinatesHorizontalBorders[i] = coordinatesHorizontalBordersList.get(i);
        }
    }

    public void onDraw(Canvas canvas) {
        // draws grid background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(deadCellColor);
        paint.setStrokeWidth(0);
        canvas.drawRect(gridXPos, gridYPos, gridXPos + gridWidth, gridYPos + gridHeight, paint);

        // draws lines for borders
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(cellBorderColor);
        paint.setStrokeWidth(cellBorderWidth);
        canvas.drawLines(coordinatesVerticalBorders, paint);
        canvas.drawLines(coordinatesHorizontalBorders, paint);

        // draws live cells
        for (Cell livingCell : livingCells) {
            paint.setColor(aliveCellColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);

            float[] cellCoordinates = livingCell.getCoordinates();
            canvas.drawRect(cellCoordinates[0] + cellBorderWidth, cellCoordinates[1] + cellBorderWidth,
                    cellCoordinates[2] - cellBorderWidth, cellCoordinates[3] - cellBorderWidth, paint);
        }
    }

    public boolean containsPoint(float x, float y) {
        return gridXPos <= x && x <= gridXPos + gridWidth && gridYPos <= y && y <= gridYPos + gridHeight;
    }

    public Cell getCellByPoint(float x, float y) {
        Cell cell = null;

        float[] cellCoordinates = calculateCellCoordinatesByPoint(x, y);

        if (cellCoordinates != null) {
            cell = new Cell(cellCoordinates);
        }

        return cell;
    }

    public Set<Cell> getLivingCells() {
        return livingCells;
    }

    public void addLivingCells(Collection<Cell> cells) {
        livingCells.addAll(cells);
    }

    public void removeLivingCells(Collection<Cell> cells) {
        livingCells.removeAll(cells);
    }

    public void triggerCellAliveStatus(Cell cell) {
        if (livingCells.contains(cell)) {
            livingCells.remove(cell);
        } else {
            livingCells.add(cell);
        }
    }

    public Set<Cell> getAliveCellNeighbours(Cell cell) {
        Set<Cell> neighbours = new HashSet<Cell>();

        int[] cellRowAndCol = calculateCellRowAndColByPoint(cell.getLeft(), cell.getTop());
        int cellRow = cellRowAndCol[0];
        int cellCol = cellRowAndCol[1];

        for (Cell livingCell : livingCells) {
            if (!livingCell.equals(cell)) {
                if (isNeighbour(cellRow, cellCol, livingCell)) {
                    neighbours.add(livingCell);
                }
            }
        }

        return neighbours;
    }

    public Set<Cell> findAllCellsWithThreeLiveNeighbours() {
        Set<Cell> deadCellsWithThreeNeighbours = new HashSet<Cell>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (getCellNeighbours(row, col).size() == 3) {
                    float[] cellCoordinates = calculateCellCoordinatesByRowAndCol(row, col);
                    deadCellsWithThreeNeighbours.add(new Cell(cellCoordinates));
                }
            }
        }

        return deadCellsWithThreeNeighbours;
    }

    private Set<Cell> getCellNeighbours(int cellRow, int cellCol) {
        Set<Cell> neighbours = new HashSet<Cell>();

        for (Cell livingCell : livingCells) {
            if (isNeighbour(cellRow, cellCol, livingCell)) {
                neighbours.add(livingCell);
            }
        }

        return neighbours;
    }

    private boolean isNeighbour(int cellRow, int cellCol, Cell cell) {
        boolean isNeighbourCell = false;

        int[] livingCellRowAndCol = calculateCellRowAndColByPoint(cell.getLeft(), cell.getTop());
        int livingCellRow = livingCellRowAndCol[0];
        int livingCellCol = livingCellRowAndCol[1];

        if ((livingCellRow - 1 == cellRow || livingCellRow == cellRow || livingCellRow + 1 == cellRow)
                && (livingCellCol - 1 == cellCol || livingCellCol == cellCol || livingCellCol + 1 == cellCol)) {
            isNeighbourCell = true;
        }
        return isNeighbourCell;
    }

    /**
     * Calculates row and col for a cell that contains a given point.
     */
    private int[] calculateCellRowAndColByPoint(float x, float y) {
        int[] cellRowAndCol = null;

        int col = (int) Math.floor((x - gridXPos) / cellSize);
        int row = (int) Math.floor((y - gridYPos) / cellSize);

        if (col < cols && row < rows) {
            cellRowAndCol = new int[]{row, col};
        }

        return cellRowAndCol;
    }

    /**
     * Calculates left, top, right, bottom coordinates of a cell in the grid.
     */
    private float[] calculateCellCoordinatesByRowAndCol(int row, int col) {
        float[] cellCoordinates = null;

        if (row > -1 && row < rows && col > -1 && col < cols) {
            float left = col * cellSize + gridXPos;
            float top = row * cellSize + gridYPos;
            float right = left + cellSize;
            float bottom = top + cellSize;
            cellCoordinates = new float[]{left, top, right, bottom};
        }

        return cellCoordinates;
    }

    /**
     * Calculates left, top, right, bottom coordinates of a cell by given point.
     */
    private float[] calculateCellCoordinatesByPoint(float x, float y) {
        int[] cellRowAndCol = calculateCellRowAndColByPoint(x, y);
        return calculateCellCoordinatesByRowAndCol(cellRowAndCol[0], cellRowAndCol[1]);
    }
}