package com.mindsortlabs.biddingtictactoe.ai;


import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class NormalTicTacAi {


    Pair<Integer, Integer> solution;
    int first;


    public NormalTicTacAi() {
        solution = null;
    }

    int done(int player, int board[][]) {

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

    int rev(int player) {
        if (player == 3)
            return 5;
        else
            return 3;

    }


    Pair<Integer, Integer> nextMove(int player, int board[][], int depth) {

        Pair<Integer, Integer> sol = null;
        ArrayList<Pair<Integer, Integer>> solutions = new ArrayList<>();

        int p = done(rev(player), board);
        if (p > 0) {
            if (p == 2) {
                return Pair.create(0, depth);
            }

            if (p == 1 && player == first)
                return Pair.create(-100, depth);
            else
                return Pair.create(100, depth);
        }

        int c = player == first ? -200 : 200;
        int max_depth = 10;

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {

                if (board[i][j] == 0) {

                    board[i][j] = player;
                    Pair<Integer, Integer> pair = nextMove(rev(player), board, depth + 1);
                    int x = pair.first;
                    int depth2 = pair.second;
                    board[i][j] = 0;
                    if (depth == 0) {
                        //cout<<i<<" "<<j<<"  "<<x<<endl;
                    }
                    if (player == first) {
                        if (c <= x) {
                            if (c == x) {
                                sol = Pair.create(i, j);
                                solutions.add(sol);
                                c = x;
                                max_depth = depth2;
                            } else if (c < x) {
                                sol = Pair.create(i, j);

                                solutions.clear();
                                solutions.add(sol);
                                c = x;
                                max_depth = depth2;
                            }
                        }
                    } else {
                        if (c >= x) {
                            if (c == x) {
                                sol = Pair.create(i, j);
                                solutions.add(sol);
                                c = x;

                                max_depth = depth2;
                            } else if (c > x) {
                                sol = Pair.create(i, j);
                                solutions.clear();
                                solutions.add(sol);
                                c = x;
                                max_depth = depth2;
                            }
                        }

                    }

                }

            }

        }
        if (depth == 0) {

            Random r = new Random();
            int temp = r.nextInt(solutions.size());

            solution = solutions.get(temp);
        }
        return Pair.create(c, max_depth);

    }

    public Pair<Integer, Integer> getSolution(Vector<String> board, char player) {

        int boards[][] = new int[3][3];

        //If player is X, I'm the first player.
        //If player is O, I'm the second player.

        //Read the board now. The board is a 3x3 array filled with X, O or _
        // Convert it to X-5 ,O-3 ,_-0

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {

                if (board.get(i).charAt(j) == '_') {
                    boards[i][j] = 0;
                } else if (board.get(i).charAt(j) == player) {
                    boards[i][j] = 5;
                } else {
                    boards[i][j] = 3;
                }
            }
        }

        first = 5;

        nextMove(5, boards, 0);

        return solution;
    }

}
