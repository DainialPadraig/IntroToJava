/**
 * My implementation of an agent that plays a defensive game (blocking a potentially winning move by the
 * opponent) and moving randomly if there is no blocking move. This agent shouldn't play well, but should
 * be better than a strictly random agent.
 * 
 * @author Dan Stormont 
 * @version 1.0
 */

import java.util.Random;
import java.util.ArrayList;

public class MyDefensiveAgent extends Agent
{
    private Random r;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     * 
     * @param game The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public MyDefensiveAgent(Connect4Game game, boolean iAmRed)
    {
        super(game, iAmRed);
        this.r = new Random();
    }

    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * 
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * 
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if:
     * - No token was place into the game.
     * - More than one token was placed into the game.
     * - A previous token was removed from the game.
     * - The color of a previous token was changed.
     * - There are empty spaces below where the token was placed.
     * 
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     * 
     */
    public void move()
    {
        int col = theyCanWin();  // see if the opponent has a winning move
        if (col < 0) {          // the opponent doesn't have a winning move
            col = randomMove();  // so choose a random move
        }
        moveOnColumn(col);       // block the winning move or move randomly otherwise
    }

    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     * 
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber)
    {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
                                                                                                  // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            }
            else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    /**
     * Returns the index of the top empty slot in a particular column.
     * 
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for  (int i = 0; i < column.getRowCount(); i++)
        {
            if (!column.getSlot(i).getIsFilled())
            {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     * 
     * @return a random valid move.
     */
    public int randomMove()
    {
        int i = this.r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1)
        {
            i = this.r.nextInt(myGame.getColumnCount());
        }
        return i;
    }

    /**
     * A helper method to check for winning moves with three pieces in a row vertically.
     * @param isRed determines whether the check is being made for winning red moves or yellow moves
     * @return a column number for a winning move or -1 if there is no winning move
     */
    private int checkForVerticalWin(boolean isRed) {
        
        for (int col = 0; col < super.myGame.getColumnCount(); col++) {
            int pieceCount = 0;
            Connect4Column currentCol = super.myGame.getColumn(col);
            if (!currentCol.getIsFull()) {  // don't bother checking the column if it's already full
                for (int row = currentCol.getRowCount() - 1; row >= 0; row--) {
                    Connect4Slot currentSlot = currentCol.getSlot(row);
                    if (currentSlot.getIsFilled()) {  // only check colors if a piece is in the slot
                        if (currentSlot.getIsRed() == isRed) {  // the piece is the color we are checking
                            pieceCount++;  // add to the count of same color pieces
                        } else {  // the piece is not the color we are looking for
                            pieceCount = 0;  // reset the piece count
                        }
                    }
                }
            }
            if (pieceCount == 3) {
                System.out.println("Vertical 3 in a row found in col " + col);
                return col;  // vertical three in a row found
            }
        }
       
        return -1;  // no vertical three in a row found
        
    }

    /**
     * A helper method to check for winning moves with three pieces with no more than one blank space
     * between them in a horizontal row, e.g.,
     *    B X X X B
     *    X B X X
     *    X X B X
     * It also checks to see if there is a piece under the blank space before choosing that as a winning
     * move. (Filling in the underlying blank space is a potentially losing move.)
     * @param isRed determines whether the check is being made for winning red moves or yellow moves
     * @return a column number for a winning move or -1 if there is no winning move
     */
    private int checkForHorizontalWin(boolean isRed) {
        
        Connect4Column[] currentColumns = new Connect4Column[super.myGame.getColumnCount()];
        for (int col = 0; col < super.myGame.getColumnCount(); col++) {  // get copy of game columns
            currentColumns[col] = super.myGame.getColumn(col);
        }
        
        int pieceCount = 0;  // count of same color pieces
        int blankIndex = -1;  // index of blank space adjacent to same color pieces
        boolean blankInRow = false;  // flag for blank in row of three pieces
        for (int row = super.myGame.getRowCount() - 1; row >= 0; row--) {
            for (int col = 0; col < super.myGame.getColumnCount(); col++) {
                Connect4Slot currentSlot = currentColumns[col].getSlot(row);
                if (currentSlot.getIsFilled()) {  // only check colors if a piece is in the slot
                    if (currentSlot.getIsRed() == isRed) {  // the piece is the color we are checking
                        pieceCount++;  // add to the count of same color pieces
                    } else {  // the piece is not the color we are looking for
                        pieceCount = 0;  // reset the piece count
                        blankIndex = -1; // reset the blank index
                        blankInRow = false; // reset blank in row flag
                    }
                } else {  // current slot is empty
                    if (blankInRow && (blankIndex == (col - 1))) { // two blanks between the three same color pieces, so reset
                        blankIndex = col;   // set this as a new end blank
                        blankInRow = false; // clear the blank in the row flag
                    } else if (blankInRow) {
                        pieceCount = col - (blankIndex + 1);  // calculate how many pieces are between the last blank and this
                        blankIndex = col;  // reset the blank index
                    } else if (row < super.myGame.getRowCount() - 1) {  // only check slot below if this isn't the bottom row
                        Connect4Slot slotBelow = currentColumns[col].getSlot(row + 1);
                        if (slotBelow.getIsFilled()) {
                            blankIndex = col;  // if there is a piece below the blank space, choose it
                            if (pieceCount > 0) {
                                blankInRow = true;  // if we've already seen one piece, this blank is in the row
                            }
                        } else {
                            blankIndex = -1;    // if there is a blank below, this could be a losing move
                            blankInRow = false; // clear blank in row flag
                            pieceCount = 0;     // clear the count of the pieces
                        }
                    } else {  // if this is the bottom row we can safely select the blank slot
                        blankIndex = col;
                        if (pieceCount > 0) {
                            blankInRow = true;
                        }
                    }
                }
                if ((pieceCount == 3) && (blankIndex >= 0)) {
                    System.out.println("Horizontal 3 in a row block found in row " + row + " col " + blankIndex);
                    return blankIndex;  // horizontal three in a row found
                }
            }
            pieceCount = 0;      // reset all variables for next row
            blankIndex = -1;
            blankInRow = false;
        }
       
        return -1;  // no horizontal three in a row found
        
    }
    
    /**
     * A helper method to check for winning moves with three pieces with no more than one blank space
     * between them in a diagonal row, both forward and back diagonals.
     * 
     * It also checks to see if there is a piece under the blank space before choosing that as a winning
     * move. (Filling in the underlying blank space is a potentially losing move.)
     * @param isRed determines whether the check is being made for winning red moves or yellow moves
     * @return a column number for a winning move or -1 if there is no winning move
     */
    private int checkForDiagonalWin(boolean isRed) {
        
        Connect4Column[] currentColumns = new Connect4Column[super.myGame.getColumnCount()];
        for (int col = 0; col < super.myGame.getColumnCount(); col++) {  // get copy of game columns
            currentColumns[col] = super.myGame.getColumn(col);
        }
        
        int pieceCount = 0;  // count of same color pieces in a diagonal
        int blankIndex = -1;  // index of blank space adjacent to same color pieces
        boolean blankInDiag = false;  // flag for blank in row of three pieces
        int row = 0;
        int col = 0;
        // Check forward diagonals
        for (int startRow = super.myGame.getRowCount() - 1; startRow >= 3; startRow--) {
            for (int startCol = 0; startCol < super.myGame.getColumnCount() - 3; startCol++) {
                row = startRow;
                col = startCol;
                while ((row >= 0) && (col < super.myGame.getColumnCount())) {
                    Connect4Slot currentSlot = currentColumns[col].getSlot(row);
                    if (currentSlot.getIsFilled()) {  // only check colors if a piece is in the slot
                        if (currentSlot.getIsRed() == isRed) {  // the piece is the color we are checking
                            pieceCount++;  // add to the count of same color pieces
                        } else {  // the piece is not the color we are looking for
                            pieceCount = 0;  // reset the piece count
                            blankIndex = -1; // reset the blank index
                            blankInDiag = false; // reset blank in row flag
                        }
                    } else {  // current slot is empty
                        if (blankInDiag && (blankIndex == (col - 1))) { // two blanks between the three same color pieces, so reset
                            blankIndex = col;   // set this as a new end blank
                            blankInDiag = false; // clear the blank in the diag flag
                        } else if (blankInDiag) {
                            pieceCount = col - (blankIndex + 1);  // calculate how many pieces are between the last blank and this
                            blankIndex = col;  // reset the blank index
                        } else if (row < super.myGame.getRowCount() - 1) {  // only check slot below if this isn't the bottom row
                            Connect4Slot slotBelow = currentColumns[col].getSlot(row + 1);
                            if (slotBelow.getIsFilled()) {
                                blankIndex = col;  // if there is a piece below the blank space, choose it
                                if (pieceCount > 0) {
                                    blankInDiag = true;  // if we've already seen one piece, this blank is in the diagonal
                                }
                            } else {
                                blankIndex = -1;    // if there is a blank below, this could be a losing move
                                blankInDiag = false; // clear blank in diagonal flag
                                pieceCount = 0;     // clear the count of the pieces
                            }
                        } else {  // if this is the bottom row we can safely select the blank slot
                            blankIndex = col;
                            if (pieceCount > 0) {
                                blankInDiag = true;
                            }
                        }
                    }
                    if ((pieceCount == 3) && (blankIndex >= 0)) {
                        System.out.println("Diagonal 3 in a row block found in row " + row + " col " + blankIndex);
                        return blankIndex;  // diagonal three in a row found
                    }
                    row--;
                    col++;
                }
                pieceCount = 0;      // reset all variables for next diagonal
                blankIndex = -1;
                blankInDiag = false;
            }
        }
        // Check backward diagonals
        for (int startRow = super.myGame.getRowCount() - 1; startRow >= 3; startRow--) {
            for (int startCol = super.myGame.getColumnCount() - 1; startCol >= 3; startCol--) {
                row = startRow;
                col = startCol;
                while ((row >= 0) && (col >= 0)) {
                    Connect4Slot currentSlot = currentColumns[col].getSlot(row);
                    if (currentSlot.getIsFilled()) {  // only check colors if a piece is in the slot
                        if (currentSlot.getIsRed() == isRed) {  // the piece is the color we are checking
                            pieceCount++;  // add to the count of same color pieces
                        } else {  // the piece is not the color we are looking for
                            pieceCount = 0;  // reset the piece count
                            blankIndex = -1; // reset the blank index
                            blankInDiag = false; // reset blank in row flag
                        }
                    } else {  // current slot is empty
                        if (blankInDiag && (blankIndex == (col - 1))) { // two blanks between the three same color pieces, so reset
                            blankIndex = col;   // set this as a new end blank
                            blankInDiag = false; // clear the blank in the diag flag
                        } else if (blankInDiag) {
                            pieceCount = col - (blankIndex + 1);  // calculate how many pieces are between the last blank and this
                            blankIndex = col;  // reset the blank index
                        } else if (row < super.myGame.getRowCount() - 1) {  // only check slot below if this isn't the bottom row
                            Connect4Slot slotBelow = currentColumns[col].getSlot(row + 1);
                            if (slotBelow.getIsFilled()) {
                                blankIndex = col;  // if there is a piece below the blank space, choose it
                                if (pieceCount > 0) {
                                    blankInDiag = true;  // if we've already seen one piece, this blank is in the diagonal
                                }
                            } else {
                                blankIndex = -1;    // if there is a blank below, this could be a losing move
                                blankInDiag = false; // clear blank in diagonal flag
                                pieceCount = 0;     // clear the count of the pieces
                            }
                        } else {  // if this is the bottom row we can safely select the blank slot
                            blankIndex = col;
                            if (pieceCount > 0) {
                                blankInDiag = true;
                            }
                        }
                    }
                    if ((pieceCount == 3) && (blankIndex >= 0)) {
                        System.out.println("Diagonal 3 in a row block found in row " + row + " col " + blankIndex);
                        return blankIndex;  // diagonal three in a row found
                    }
                    row--;
                    col--;
                }
                pieceCount = 0;      // reset all variables for next diagonal
                blankIndex = -1;
                blankInDiag = false;
            }
        }
       
        return -1;  // no diagonal three in a row found
        
    }
    
    /**
     * Returns the column that would allow the opponent to win.
     * 
     * This is a somewhat naive approach in this iteration. It just looks for three pieces that form a 
     * vertical, horizontal, or diagonal row with a blank on either end.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin()
    {
        int winningCol = -1;
        if ((winningCol = checkForVerticalWin(!super.iAmRed)) >= 0) {
            return winningCol;
        } else if ((winningCol = checkForHorizontalWin(!super.iAmRed)) >= 0) {
            return winningCol;
        } else if ((winningCol = checkForDiagonalWin(!super.iAmRed)) >= 0) {
            return winningCol;
        } else {
            return -1;
        }
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "My Defensive Agent";
    }
}
