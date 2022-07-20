package Minesweeper;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;

public class Board extends JPanel {
	
	private final int NUM_OFIMAGES = 13;
	private final int SIZE_OFBLOCK = 15; //Size of the actual images is 15x15 pixels
	
	private final int COVER_OFBLOCK = 10;
	private final int MARK_FORBLOCK = 10;
	private final int EMPTY_BLOCK = 0;
	private final int MINE_BLOCK = 9; //Maximum number of mines that can be present around a cell

	//Basic variables for unmarked and marked mine blocks:
	private final int UNMARKED_MINEBLOCK = MINE_BLOCK + COVER_OFBLOCK; //This will be used for a field that is covered and contains a mine
	private final int MARKED_BLOCK = UNMARKED_MINEBLOCK + MARK_FORBLOCK; //This will be used as a field that is covered but the user has marked

	//These constants will be used as a way to draw, cover, mark, or draw a wrong marking on an empty cell
	private final int DRAW_MINE = 9;
	private final int DRAW_COVERMINE = 10;
	private final int DRAW_MARKING = 11;
	private final int DRAW_WRONG_MARKING = 12;

	//Values based off of window size value but currently since there will only be one window that may or may not be  later on 
	private final int NUMBER_MINES = 40;
	private final int NUMBER_ROWS = 16;
	private final int NUMBER_COLLUMS = 16;

	//Setting window:
	private final int BOARD_WIDTH = NUMBER_COLLUMS * SIZE_OFBLOCK + 1;
	private final int BOARD_HEIGHT = NUMBER_ROWS * SIZE_OFBLOCK + 1;

	private boolean runningGame; //Whether we are in game or not

	private int[] fields; //<- very important to game function. The field is an array of numbers. Each cell in the field has a specific number. For instance, a mine cell has number 9. A cell with number 2 means it is adjacent to two mines. The numbers are added.
	private Image[] images; //Luckily all the images are just numbers so we don't need a set variable
	private int numOfMinesLeft; //How many mines were left?
	private int allCellsPresent;
	private final JLabel status;




	public Board(JLabel status) {

	    this.status = status;
	    initBoard();
	}

	public void initBoard() {

	    setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

	    images = new Image[NUM_OFIMAGES];

	    for (int i = 0; i < NUM_OFIMAGES; i++) { //This is a very unique way of grabbing images in java, this would only apply if the PNG files were named in numbers rather than individual names

	        var path = "src/resources/" + i + ".png";
	        images[i] = (new ImageIcon(path)).getImage();
	    }
	    addMouseListener(new MinesAdapter());
	    newGame(); //This will initiate the game and will be present in most methods	
	}

	public void newGame() { //This is what happens when we start up a NEW GAME
	    int cell;

	    var random = new Random();
	    runningGame = true; //Game is currently running
	    numOfMinesLeft = NUMBER_MINES;

	    allCellsPresent = NUMBER_ROWS * NUMBER_COLLUMS;
	    fields = new int[allCellsPresent]; //Make the integer array now equal to the number of all cells in the window

	    for (int i = 0; i < allCellsPresent; i++) {
	        fields[i] = COVER_OFBLOCK;
	    }
	    status.setText(Integer.toString(numOfMinesLeft));


	    int i = 0;

	    while (i < NUMBER_MINES) { //This entire while cycle will randomly place all mines
	        int position = (int)(allCellsPresent * random.nextDouble()); //Set a "position" variable of type integer and set it equal to an integer that takes in all the cells in the window and times them by a random double so that randomness allows for non repeats in this generation algorithm  

	        if ((position < allCellsPresent) && fields[position] != UNMARKED_MINEBLOCK) { //If positions is less than all the cells in window, and that fields of type position not equal to all unmarked blocks...

	            int current_loc = position % NUMBER_COLLUMS; //Current location is equal to positions as a remainder  
	            fields[position] = UNMARKED_MINEBLOCK;
	            i++; //Continue to increment i until its not equal to number of mines

	            if (current_loc > 0) { //You will notice here there are 8 total if-within an if-within an if statement and this is because each cell has a possibility of being around 8 mines, in each if statement, we account for each of the individual boxes
	                cell = position - 1 - NUMBER_COLLUMS; //All these if statements account for only 1 block adjacent!
	                if (cell >= 0) {
	                    if (fields[cell] != UNMARKED_MINEBLOCK) {
	                        fields[cell] += 1;
	                    }
	                }
	                cell = position - 1;
	                if (cell >= 0) {
	                    if (fields[cell] != UNMARKED_MINEBLOCK) {
	                        fields[cell] += 1;
	                    }
	                }

	                cell = position + NUMBER_COLLUMS - 1;
	                if (cell < allCellsPresent) {
	                    if (fields[cell] != UNMARKED_MINEBLOCK) {
	                        fields[cell] += 1;
	                    }
	                }
	            }

	            cell = position - NUMBER_COLLUMS;
	            if (cell >= 0) { //Each of the cells can be surrounded up to eight cells. (This does not apply to the border cells.) We raise the number for adjacent cells for each of the randomly placed mine.
	                if (fields[cell] != UNMARKED_MINEBLOCK) {
	                    fields[cell] += 1;
	                }
	            }

	            cell = position + NUMBER_COLLUMS;
	            if (cell < allCellsPresent) {
	                if (fields[cell] != UNMARKED_MINEBLOCK) {
	                    fields[cell] += 1;
	                }
	            }

	            if (current_loc < (NUMBER_COLLUMS - 1)) {
	                cell = position - NUMBER_COLLUMS + 1;
	                if (cell >= 0) {
	                    if (fields[cell] != UNMARKED_MINEBLOCK) {
	                        fields[cell] += 1;
	                    }
	                }
	                cell = position + NUMBER_COLLUMS + 1;
	                if (cell < allCellsPresent) {
	                    if (fields[cell] != UNMARKED_MINEBLOCK) {
	                        fields[cell] += 1;
	                    }
	                }
	                cell = position + 1;
	                if (cell < allCellsPresent) {
	                    if (fields[cell] != UNMARKED_MINEBLOCK) {
	                        fields[cell] += 1;
	                    }
	                }
	            }
	        }
	    }
	}
	public void findCells(int c) { //This mess just fucks with my head sometimes, as the title suggest we are finding empty cells,

	    int current_loc = c % NUMBER_COLLUMS; //If the player clicks on a mine cell, the game is over. If he clicks on a cell adjacent to a mine, he uncovers a number indicating how many mines the cell is adjacent to. Clicking on an empty cell leads to uncovering many other empty cells plus cells with a number that form a border around a space of empty borders. We use a recursive algorithm to find empty cells
	    int cell;

	    if (current_loc > 0) { //THIS ENTIRE THING IS JUST RECURSIVE ALGORITHM AND IT MAKES ME CRY THAT I SPENT 3 HOURS LOOKING UP HOW TO MAKE ONE UNTIL I HEARD THE WORD RECURSIVE, :(
	        cell = c - NUMBER_COLLUMS - 1;
	        if (cell >= 0) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell);
	                }
	            }
	        }

	        cell = c - 1; //You can probably understand this pattern, as you notice, almost EVERYTHING revolves around the fields array, this array is extremely important and is most likely going to be used these algorithms
	        if (cell >= 0) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell); //You must keep calling this method throughout each block of if's 
	                }
	            }
	        }

	        cell = c + NUMBER_COLLUMS - 1;
	        if (cell < allCellsPresent) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell);
	                }
	            }
	        }
	    }

	    cell = c - NUMBER_COLLUMS;
	    if (cell >= 0) {
	        if (fields[cell] > MINE_BLOCK) {
	            fields[cell] -= COVER_OFBLOCK;
	            if (fields[cell] == EMPTY_BLOCK) {
	                findCells(cell);
	            }
	        }
	    }

	    cell = c + NUMBER_COLLUMS;
	    if (cell < allCellsPresent) {
	        if (fields[cell] > MINE_BLOCK) {
	            fields[cell] -= COVER_OFBLOCK;
	            if (fields[cell] == EMPTY_BLOCK) {
	                findCells(cell);
	            }
	        }
	    }

	    if (current_loc < (NUMBER_COLLUMS - 1)) {
	        cell = c - NUMBER_COLLUMS + 1;
	        if (cell >= 0) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell);
	                }
	            }
	        }

	        cell = c + NUMBER_COLLUMS + 1;
	        if (cell < allCellsPresent) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell);
	                }
	            }
	        }
	        
	        cell = c - 1;
	        if (cell <= 0) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell);
	                }
	            }

	        cell = c + 1;
	        if (cell < allCellsPresent) {
	            if (fields[cell] > MINE_BLOCK) {
	                fields[cell] -= COVER_OFBLOCK;
	                if (fields[cell] == EMPTY_BLOCK) {
	                    findCells(cell);
	                }
	            }
	        }
	       }
	    }    

	}


	@Override
	public void paintComponent(Graphics mineGraphic) { //This method turns numbers into the images

	    int reveal = 0;

	    for (int i = 0; i < NUMBER_ROWS; i++) {

	        for (int c = 0; c < NUMBER_COLLUMS; c++) {

	            int cell = fields[(i * NUMBER_COLLUMS) + c];

	            if (runningGame && cell == MINE_BLOCK) {

	                runningGame = false;
	            }
	            if (!runningGame) { //This is what is generally done to repaint images, especially on cell-like graphics 
	                if (cell == UNMARKED_MINEBLOCK) {
	                    cell = DRAW_MINE;
	                } else if (cell == MARKED_BLOCK) {
	                    cell = DRAW_MARKING;
	                } else if (cell > UNMARKED_MINEBLOCK) {
	                    cell = DRAW_WRONG_MARKING;
	                } else if (cell > MINE_BLOCK) {
	                    cell = DRAW_COVERMINE;
	                }
	            } else {

	                if (cell > UNMARKED_MINEBLOCK) {
	                    cell = DRAW_MARKING;
	                } else if (cell > MINE_BLOCK) {
	                    cell = DRAW_COVERMINE;
	                    reveal++;
	                }
	            }

	            mineGraphic.drawImage(images[cell], (c * SIZE_OFBLOCK), //This code line draws every cell on the window
	                (i * SIZE_OFBLOCK), this);
	        }
	    }
	    if (reveal == 0 && runningGame) { //If there is nothing left to uncover, we win. If the runningGame variable was set to false, we have lost.

	        runningGame = false;
	        status.setText("Game won");

	    } else if (!runningGame) { //! is usually used to set things false or not equal to/opposite

	        status.setText("Game lost");
	    }
	}

	private class MinesAdapter extends MouseAdapter {

	    @Override
	    public void mousePressed(MouseEvent l) {
	        int x = l.getX(); //determine the x and y coordinates of the mouse pointer
	        int y = l.getY();

	        int cCol = x / SIZE_OFBLOCK; //compute the corresponding column and row of the mine field	
	        int cRow = y / SIZE_OFBLOCK;

	        boolean repainting = false;

	        if (!runningGame) {

	            newGame();
	            repaint();
	        }

	        if ((x < NUMBER_COLLUMS * SIZE_OFBLOCK) && (y < NUMBER_ROWS * SIZE_OFBLOCK)) { //We check that we are located in the area of the mine field

	            if (l.getButton() == MouseEvent.BUTTON3) { //The uncovering of the mines is done with the right mouse button

	                if (fields[(cRow * NUMBER_COLLUMS) + cCol] > MINE_BLOCK) {

	                    repainting = true;

	                    if (fields[(cRow * NUMBER_COLLUMS) + cCol] <= UNMARKED_MINEBLOCK) {

	                        if (numOfMinesLeft > 0) {
	                            fields[(cRow * NUMBER_COLLUMS) + cCol] += MARK_FORBLOCK;
	                            numOfMinesLeft--;
	                            String msg = Integer.toString(numOfMinesLeft);
	                            status.setText(msg);
	                        } else {
	                            status.setText("No marks left");
	                        }
	                    } else {

	                        fields[(cRow * NUMBER_COLLUMS) + cCol] -= MARK_FORBLOCK;
	                        numOfMinesLeft++;
	                        String msg = Integer.toString(numOfMinesLeft);
	                        status.setText(msg);
	                    }
	                }

	            } else {

	                if (fields[(cRow * NUMBER_COLLUMS) + cCol] > UNMARKED_MINEBLOCK) {

	                    return;
	                }

	                if ((fields[(cRow * NUMBER_COLLUMS) + cCol] > MINE_BLOCK) && (fields[(cRow * NUMBER_COLLUMS) + cCol] < MARKED_BLOCK)) {
	                     

	                    fields[(cRow * NUMBER_COLLUMS) + cCol] -= COVER_OFBLOCK;
	                    repainting = true;

	                    if (fields[(cRow * NUMBER_COLLUMS) + cCol] == MINE_BLOCK) {
	                        runningGame = false;
	                    }

	                    if (fields[(cRow * NUMBER_COLLUMS) + cCol] == EMPTY_BLOCK) {
	                        findCells((cRow * NUMBER_COLLUMS) + cCol);
	                    }
	                }
	            }

	            if (repainting) {
	                repaint();
	            }
	        }
	    }
	}
}
