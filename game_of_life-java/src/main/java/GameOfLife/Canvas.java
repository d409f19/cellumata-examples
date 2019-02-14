package GameOfLife;

import java.util.Date;
import java.util.Random;

class Canvas {
    private final int x;
    private final int y;
    private Cell[][] canvas;
    private final Cell[][] tempCanvas;

    Canvas(int x, int y) {
        this.x = x;
        this.y = y;
        this.canvas = new Cell[x][y];
        this.tempCanvas = this.canvas;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Random random = new Random(new Date().getTime());
                this.canvas[i][j] = new Cell(random.nextInt(2) == 1);
            }
        }
    }

    void tick() {
        int neighbours;
        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
                neighbours = countMooreNeighbours(i, j);

                // deep copy cell
                tempCanvas[i][j] = canvas[i][j];

                // set new state based on the life of the cell
                tempCanvas[i][j].setState(cellLife(tempCanvas[i][j], neighbours));
            }
        }
        canvas = tempCanvas;
    }

    private int countMooreNeighbours(int x, int y) {
        int neighbours = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                // bound coordinate within constraints
                if (this.canvas[constrainToRange(x + i, this.x)][constrainToRange(y + j, this.y)].isAlive())
                    neighbours++;
            }
        }
        return neighbours;
    }

    private static int constrainToRange(int value, int max) {
        return Math.min(Math.max(value, 0), max);
    }

    private boolean cellLife(Cell cell, int neighbours) {
        if (cell.isAlive()) {
            // underpopulation
            if (neighbours < 2) return false;
            // just right
            return 2 == neighbours || neighbours == 3;
            // else overpopulation
        } else return neighbours == 3;
    }
}
