package com.mindsortlabs.biddingtictactoe.ai;


import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Computes AI next move returning place where to move
 * Implemented based on min-max algorithm
 */
public class NormalTicTacAi {

    /**
     * Save the solution to return to main activity
     */
    private Pair<Integer, Integer> solution = null;
    private int first;


    public NormalTicTacAi() {
    }

    /**
     * Checks which player wins from board configuration
     * Returns 0,1 or 2 depending on parameter player
     * 0 -> continue playing no one is winning
     * 1 -> player in parameter wins
     * 2 -> Other player wins or match is Drawn
     * Here draw is as bad as loss
     */
    private int matchWinner(int player, int board[][]) {

        // Check if AI is winning
        for (int i = 0; i < 3; i++) {
            if (board[i][0] + board[i][1] + board[i][2] == 3 * player) {
                return 1;
            }

            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return 1;
            }
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return 1;
        }

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return 1;
        }

        int flag = 1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0)
                    flag = 0;
            }
        }

        if (flag == 1) return 2;

        return 0;
    }

    /**
     * Reverse the player 3 <-> 5
     */
    private int rev(int player) {
        if (player == 3)
            return 5;
        else
            return 3;

    }

    /**
     * Implemented according min-max algorithm to calculate next move of CPU
     */
    private Pair<Integer, Integer> nextMove(int player, int board[][], int depth) {

        Pair<Integer, Integer> sol;
        ArrayList<Pair<Integer, Integer>> solutions = new ArrayList<>();

        // Check if game is over or not
        // Returns value according to lose or win
        int p = matchWinner(rev(player), board);
        if (p > 0) {
            if (p == 2) {
                return Pair.create(0, depth);
            }

            if (p == 1 && player == first)
                return Pair.create(-100, depth);
            else
                return Pair.create(100, depth);
        }

        int max_cost = (player == first) ? -200 : 200;
        int max_depth = 10;

        // Try all empty possible and try to move there
        // Here depth is used to choose the solution to win in smallest steps
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (board[i][j] == 0) {

                    // Found empty place make next move
                    // Mark the board
                    board[i][j] = player;
                    Pair<Integer, Integer> pair = nextMove(rev(player), board, depth + 1);
                    // Un-mark the board
                    board[i][j] = 0;

                    int costForThisMove = pair.first;
                    int depthForThisMove = pair.second;


                    if (player == first) {
                        if (max_cost <= costForThisMove) {
                            if (max_cost == costForThisMove) {
                                sol = Pair.create(i, j);
                                solutions.add(sol);
                                max_cost = costForThisMove;
                                max_depth = depthForThisMove;
                            } else if (max_cost < costForThisMove) {
                                sol = Pair.create(i, j);
                                solutions.clear();
                                solutions.add(sol);
                                max_cost = costForThisMove;
                                max_depth = depthForThisMove;
                            }
                        }
                    } else {
                        if (max_cost >= costForThisMove) {
                            if (max_cost == costForThisMove) {
                                sol = Pair.create(i, j);
                                solutions.add(sol);
                                max_cost = costForThisMove;

                                max_depth = depthForThisMove;
                            } else if (max_cost > costForThisMove) {
                                sol = Pair.create(i, j);
                                solutions.clear();
                                solutions.add(sol);
                                max_cost = costForThisMove;
                                max_depth = depthForThisMove;
                            }
                        }
                    }
                }
            }
        }
        if (depth == 0) {
            // Randomize the 1st move of AI on where to move
            Random r = new Random();
            int temp = r.nextInt(solutions.size());
            solution = solutions.get(temp);
        }
        return Pair.create(max_cost, max_depth);

    }

    /**
     * Get a Blocking Position if Winning Other Person Winning
     */
    private Pair<Integer, Integer> getBlockingPosition(int player, int board[][]){

        Integer winningPos[][][]= {
                { {0,0} , {0,1} , {0,2} },
                { {1,0} , {1,1} , {1,2} },
                { {2,0} , {2,1} , {2,2} },
                { {0,0} , {1,1} , {2,2} },
                { {0,2} , {1,1} , {2,0} }
        };

        for (int i=0; i<5; i++){

            int first =  board[  winningPos[i][0][0] ][  winningPos[i][0][1] ];
            int second = board[  winningPos[i][1][0] ][  winningPos[i][1][1] ];
            int third =  board[  winningPos[i][2][0] ][  winningPos[i][2][1] ];

            if( first + second + third == 2*rev(player) ){
                if(first==0){
                    return Pair.create( winningPos[i][0][0] ,  winningPos[i][0][1] );
                }else if(second==0){
                    return Pair.create( winningPos[i][1][0] ,  winningPos[i][1][1] );
                }else{
                    return Pair.create( winningPos[i][2][0] ,  winningPos[i][2][1] );
                }
            }
        }

        return null;
    }

    /**
     * Returns position to place the mark in Normal Tic Tac Toe
     */
    public Pair<Integer, Integer> getSolution(Vector<String> board, char player,int level) {

        int boards[][] = new int[3][3];

        int noOfMovesPlayed = 0  ;
        //If player is X, I'm the first player.
        //If player is O, I'm the second player.

        ArrayList<Pair<Integer, Integer>> positionFree = new ArrayList<>();
        //Read the board now. The board is a 3x3 array filled with X, O or _
        // Convert it to X-5 ,O-3 ,_-0

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {

                if (board.get(i).charAt(j) == '_') {
                    boards[i][j] = 0;
                    positionFree.add(Pair.create(i,j));
                } else if (board.get(i).charAt(j) == player) {
                    boards[i][j] = 5;
                    noOfMovesPlayed++;
                } else {
                    boards[i][j] = 3;
                    noOfMovesPlayed++;
                }
            }
        }
        //firstMove
        if (noOfMovesPlayed==0){
            Random r = new Random();
            return Pair.create( r.nextInt(3) ,r.nextInt(3) );
        }

        //stubmove
        if (noOfMovesPlayed==9){
            Random r = new Random();
            return Pair.create( r.nextInt(3) ,r.nextInt(3) );
        }


        first = 5;

        if (level == 0){
            //EASY LEVEL
            Random r = new Random();
            int temp = r.nextInt(positionFree.size());
            solution = positionFree.get(temp);
            return solution;

        }else if(level==1){
            //MEDIUM LEVEL
            // only Block User Winning Positions
            solution = getBlockingPosition(5, boards);
            if(solution==null){
                Random r = new Random();
                int temp = r.nextInt(positionFree.size());
                solution = positionFree.get(temp);
            }
            return solution;
        }
        // HARD LEVEL
        // Use min-max Algorithm

        // Better to call function each time .Pre-computing all states takes extra memory
        nextMove(5, boards, 0);

        return solution;
    }

}
