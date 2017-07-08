package com.mindsortlabs.biddingtictactoe.ai;


import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Vector;

public class BiddingTicTacToeAi {

    double F[][] ;
    double Bid[][] ;
    Pair<Integer,Integer> Pairs[][];

    public BiddingTicTacToeAi(){
        F = new double[1024][1024];
        Bid = new double[1024][1024];

        Pairs = new Pair[1024][1024];

        for(int i=0; i<1023; i++){
            for(int j=0; j<1023; j++){
                F[i][j]=-1.00;Bid[i][j]=-1.00;
            }
        }
        int board[][] = new int[3][3];
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                board[i][j]=0;
            }
        }
        nextMove(board,0,0,0);
        Log.d("TAG1245","complete.");
    }

    double bid= 0;

    int TOTAL_COINS =   200;

    Pair<Integer,Integer> favoured_child;

    int first ;


    int rev(int player){
        if(player==3)
            return 5;
        else
            return 3;

    }

    int done( int board[][]){

        for(int i=0; i<3; i++){
            if(board[i][0]+board[i][1]+board[i][2]==3*first){
                return 1;
            }

            if(board[0][i]+board[1][i]+board[2][i]==3*first){
                return 1;
            }

        }

        if(board[0][0]+board[1][1]+board[2][2]==3*first){
            return 1;
        }

        if(board[0][2]+board[1][1]+board[2][0]==3*first){
            return 1;
        }


        //check for 2nd player win

        for(int i=0; i<3; i++){
            if(board[i][0]+board[i][1]+board[i][2]==3*rev(first) ){
                return 2;
            }

            if(board[0][i]+board[1][i]+board[2][i]==3*rev(first)){
                return 2;
            }

        }

        if(board[0][0]+board[1][1]+board[2][2]==3*rev(first)){
            return 2;
        }

        if(board[0][2]+board[1][1]+board[2][0]==3*rev(first)){
            return 2;
        }

        //end




        int flag =1;

        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                if(board[i][j]==0)
                    flag=0;
            }
        }

        if(flag==1)return 2;

        return 0;
    }



    double nextMove(int board[][],int depth,int firstPlayer,int secondPlayer){

        if(Math.abs(F[firstPlayer][secondPlayer]+1.00)>0.0001)
            return F[firstPlayer][secondPlayer];

        Pair<Integer,Integer>  sol = null;


        int p = done(board) ;

    /*
        p can return 0,1

        p==0 --> continue play

        p==1 --> other player wins

        p==2 --> draw

    */
        if( p > 0 ){
            if(p==2){
                return (1.00);
            }

            if(p==1)
                return (0.00);
        }

        int max_depth=8;

        double Fmax =-1.00 ;
        double Fmin = 2.01 ;

        for(int i=0; i<3; i++){

            for(int j=0; j<3; j++){

                if(board[i][j]==0)
                {

                    board[i][j]=first;
                    double x= nextMove( board,depth + 1 , firstPlayer|getSingleValue(i,j) ,secondPlayer );
                    board[i][j]=0;

                    if(Fmin >= x ){
                        sol = Pair.create(i,j);
                        Fmin=x;
                        max_depth=depth;
                    }
                }

                if(board[i][j]==0)
                {

                    board[i][j]=rev(first);
                    double x= nextMove( board,depth + 1, firstPlayer,secondPlayer|getSingleValue(i,j)  );
                    board[i][j]=0;

                    if(Fmax <= x ){
                        //sol = Pair.create(i,j);
                        Fmax=x;
                        max_depth=depth;
                    }

                }

            }

        }
        if(depth ==0 ){
            favoured_child = sol ;
            bid = Math.abs(Fmax-Fmin)/2;
        }
        Pairs[firstPlayer][secondPlayer]=sol;
        Bid[firstPlayer][secondPlayer]=Math.abs(Fmax-Fmin)/2;
        return F[firstPlayer][secondPlayer] = Math.abs(Fmax+Fmin)/2;


    }

    int getSingleValue(int i,int j){
        return (int) Math.pow( 2 ,i*3 + j );
    }

    public Pair < Integer ,Pair<Integer, Integer> >  getSolution(Vector<String> board,int mycoins, char player) {

//        char player;
        Log.d("BOARD ::","  "+board.toString());
        Log.d("COINS ::","  "+mycoins);

        if(mycoins==0)
            return Pair.create(0,Pair.create(0,0));

        int boards[][]= new int[3][3];

        //If player is X, I'm the first player.
        //If player is O, I'm the second player.
        //cin >> player;
//        player = 'X';

        //Read the board now. The board is a 3x3 array filled with X, O or _.
        int count =0;

        int firstPLayer = 0 ,secondPlayer =0;
        for(int i=0; i<3; i++){

            for(int j=0; j<3; j++){

                if(board.get(i).charAt(j)=='_'){
                    boards[i][j]=0;count++;
                }else if(board.get(i).charAt(j)==player){
                    boards[i][j]=5;firstPLayer|=getSingleValue(i,j);
                }else{
                    boards[i][j]=3;secondPlayer|=getSingleValue(i,j);
                }


            }

        }
        //if(count==9){
        //    Pair.create(32,Pair.create(1,1));
        //}


        first = 5;



        if(Math.abs(F[firstPLayer][secondPlayer]+1.00)<0.0001)
            nextMove(boards,0,firstPLayer,secondPlayer);

        bid = Bid[firstPLayer][secondPlayer];
        favoured_child = Pairs[firstPLayer][secondPlayer];

        bid = bid*200;

        int minBid = (int)bid;

        int opponentBid = TOTAL_COINS - mycoins ;

        minBid = Math.min(minBid,opponentBid+1);
        minBid = Math.min(minBid,mycoins);

        Log.d("BIDDING ::","  "+minBid);
       // Log.d("CHILDRENS ::","  "+favoured_child.first +"    "+favoured_child.second);
        if(favoured_child==null){
            return  Pair.create(minBid,Pair.create(0,0));
        }
        return Pair.create(minBid,favoured_child);
    }
}
