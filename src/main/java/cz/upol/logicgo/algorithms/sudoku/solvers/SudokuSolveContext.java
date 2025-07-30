package cz.upol.logicgo.algorithms.sudoku.solvers;

import cz.upol.logicgo.algorithms.sudoku.SudokuGame;

import java.util.concurrent.atomic.AtomicInteger;

public class SudokuSolveContext {
    private final AtomicInteger solutionCount = new AtomicInteger(0);
    private volatile SudokuGame solvedGame = null;
    private volatile boolean stopFlag = false;

    public synchronized int incrementSolutionCountAndGet() {
        return solutionCount.incrementAndGet();
    }

    public int getSolutionCount() {
        return solutionCount.get();
    }

    public boolean shouldStop() {
        return stopFlag;
    }


    public synchronized void setSolvedGame(SudokuGame game) {
       if (solvedGame == null) solvedGame = game.makeCopy();
    }

    public SudokuGame getSolvedGame() {
        return solvedGame;
    }

    public synchronized void setStopFlag(boolean stopFlag) {
        System.out.println("SudokuSolveContext.setStopFlag: " + stopFlag);
        this.stopFlag = stopFlag;
    }
}
