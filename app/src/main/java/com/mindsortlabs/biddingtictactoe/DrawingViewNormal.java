package com.mindsortlabs.biddingtictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class DrawingViewNormal extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint, paint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    float leftX;
    // float rightX;
    float topY;
    // float bottomY;
    private Paint mPaint;
    DrawingPlayCPUNormalActivity activity;

    int oldRow = 0;
    int oldCol = 0;

    int newRow = 0;
    int newCol = 0;

    int univOldRow = 0;
    int univOldCol = 0;

    boolean arr[][];

    long tStart;
    long tEnd;
    long tDelta;
    int flag = 0;

    float x1,x2,x3,x4;
    float y1,y2,y3,y4;

    int turn  = 0; // 0 = O, 1 = X

    public DrawingViewNormal(Context context){
        super(context);
    }

    public DrawingViewNormal(Context context, AttributeSet attrs){
        super(context, attrs);

        View.inflate(context, R.layout.activity_drawing_play_normal, null);
    }

    public DrawingViewNormal(Context c, DrawingPlayCPUNormalActivity activity) {
        super(c);

        arr = new boolean[4][4];
        for(int i=1;i<=3;i++){
            for(int j=1;j<=3;j++){
                Log.d("TAG1234", String.valueOf(arr[i][j]));
            }
        }

        this.activity = activity;
        Log.d("TAG123","insideConstructor: ");
        context=c;
        mPath = new Path();

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(15);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(7f);

        x1 = (float) 0.08*getWidth();
        x2 = (float) (0.36*getWidth());
        x3 = (float) (0.64*getWidth());
        x4 = (float) (0.92*getWidth());

        y1 = (float) (0.24*getWidth());
        y2 = (float) (0.52*getWidth());
        y3 = (float) (0.80*getWidth());
        y4 = (float) (1.08*getWidth());


        leftX = x1;
        //   rightX = (float) (0.86*getWidth());
        topY = y1;
        //bottomY = (float) (0.96*getWidth());
        post(animateLine);

        flag = 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("TAG123","insideSizeChanged: ");
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

//        Log.d("widthTag: ",getWidth()+"");
        Log.d("TAG123","insideOnDraw: ");

//        final float HLX = (float) (0.14*getWidth());
//        final float HTY = (float) (0.48*getWidth());
//        final float HBY = (float) (0.72*getWidth());

//        Right Lines
//        float HRX = (float) (0.86*getWidth());

        //Top Lines
//        float VLX = (float) (0.38*getWidth());
//        float VRX = (float) (0.62*getWidth());
//        float VTY = (float) (0.24*getWidth());

//        Bottom Lines
//        float VBY = (float) (0.96*getWidth());



        x1 = (float) 0.08*getWidth();
        x2 = (float) (0.36*getWidth());
        x3 = (float) (0.64*getWidth());
        x4 = (float) (0.92*getWidth());

        y1 = (float) (0.24*getWidth());
        y2 = (float) (0.52*getWidth());
        y3 = (float) (0.80*getWidth());
        y4 = (float) (1.08*getWidth());

        canvas.drawLine(x1,y2,leftX,y2,paint);
        canvas.drawLine(x1,y3,leftX,y3,paint);
        canvas.drawLine(x2,y1,x2,topY,paint);
        canvas.drawLine(x3,y1,x3,topY,paint);



//            for(leftX= (float) (0.14*getWidth());leftX<0.86*getWidth();leftX=leftX+1) {
//                canvas.drawLine(HLX, HTY, leftX, HTY, paint);
//                canvas.drawLine(HLX, HBY, leftX, HBY, paint);
//            }

//            canvas.drawLine(VLX,VTY,VLX,topY,paint);
//            canvas.drawLine(VRX,VTY,VRX,topY,paint);
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private Runnable animateLine = new Runnable() {
        @Override
        public void run() {
            boolean reachedEnd = false;
            leftX = leftX + 10;
//            rightX = rightX + 10;
            topY = (float) (topY + 12);
//            bottomY = bottomY - 10;

            invalidate();

            x4 = (float) (0.92*getWidth());
            if(leftX<=x4){
                invalidate();
            } else reachedEnd = true;
            if(!reachedEnd)
                postDelayed(this,15);
        }
    };



    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        flag = 1;
        Log.d("TAG123","insideTouchStart1: ");

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        Log.d("TAG123","insideTouchStart2: ");
    }

    private void touch_move(float x, float y) {

        Log.d("TAG123","insideTouchMove: ");
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {

        Log.d("TAG123","insideTouchUp: ");
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        int width = getWidth();
        final float x1 = (float) 0.08*getWidth();
        final float x2 = (float) (0.36*getWidth());
        final float x3 = (float) (0.64*getWidth());
        final float x4 = (float) (0.92*getWidth());

        final float y1 = (float) (0.24*width);
        final float y2 = (float) (0.52*width);
        final float y3 = (float) (0.80*width);
        final float y4 = (float) (1.08*width);

        float x = event.getX();
        float y = event.getY();

        Log.d("TAG123", "insideTouchEvent1: " + "x : " + x + "  y: " + y);
        float cell  = (float) (0.28*getWidth());
        float margin = (float) (0.1*getWidth());

        float posX = (float) (x-0.08*getWidth());
        float posY = (float) (y-0.24*getWidth());
        newRow = (int) (posY/cell)+1;
        newCol = (int) (posX/cell)+1;
//        Toast.makeText(getContext(), "row = " + newRow + " col = "+newCol + " turn :  "+ turn, Toast.LENGTH_SHORT).show();
        if(flag==1&&newRow>0&&newRow<4&&newCol>0&&newCol<4) {
            Log.d("TAG123", "insideTouchEvent2: " + "x : " + x + "  y: " + y);


//            //Row 1
//            if (x > x1+margin && x < x2-margin && y > y1+margin && y < y2 - margin) {
//                newRow = 1;
//                newCol = 1;
//            }
//
//            else if (x > x2+margin && x < x3-margin && y > y1+margin && y < y2 - margin) {
//                newRow = 1;
//                newCol = 2;
//            }
//
//            else if (x > x3+margin && x < x4-margin && y > y1+margin && y < y2 - margin) {
//                newRow = 1;
//                newCol = 3;
//            }
//
//
//            //Row 2
//            else if (x > x1+margin && x < x2-margin && y > y2+margin && y < y3 - margin) {
//                newRow = 2;
//                newCol = 1;
//            }
//
//            else if (x > x2+margin && x < x3-margin && y > y2+margin && y < y3 - margin) {
//                newRow = 2;
//                newCol = 2;
//            }
//
//            else if (x > x3+margin && x < x4-margin && y > y2+margin && y < y3 - margin) {
//                newRow = 2;
//                newCol = 3;
//            }
//
//
//            //Row 3
//            else if (x > x1+margin && x < x2-margin && y > y3+margin && y < y4 - margin) {
//                newRow = 3;
//                newCol = 1;
//            }
//
//            else if (x > x2+margin && x < x3-margin && y > y3+margin && y < y4 - margin) {
//                newRow = 3;
//                newCol = 2;
//            }
//
//            else if (x > x3+margin && x < x4-margin && y > y3+margin && y < y4 - margin) {
//                newRow = 3;
//                newCol = 3;
//            }

            Log.d("TAG123","oldRow: "+oldRow + " oldCol: "+ oldCol + " newRow: "+newRow + " newCol: "+newCol);
            if ((oldRow != newRow || oldCol != newCol)&&arr[newRow][newCol]==false) {
                turn = 1-turn;
                if(turn==0){
                    mPaint.setColor(Color.RED);
                }
                else{
                    mPaint.setColor(Color.GREEN);
                }
                Log.d("TAG123","changed: ");
                activity.userTurn(newRow, newCol, turn);
                arr[newRow][newCol] = true;
                oldRow = newRow;
                oldCol = newCol;
            }

            univOldRow = newRow;
            univOldCol = newCol;
            flag = 0;
        }
//        if(flag==0){
//            tStart = System.currentTimeMillis();
//            activity.onTouch(newRow, newCol);
//            flag = 1;
//        }
//
//        tEnd = System.currentTimeMillis();
//        tDelta = tEnd - tStart;
//
//        if(tDelta>1000||newRow!=oldRow||newCol!=oldCol) {
//            activity.onTouch(newRow, newCol);
//            flag = 0;
//            oldRow = newRow;
//            oldCol = newCol;
//        }



        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG123", "Action Down : ");
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG123", "Action Move : ");
                if(newRow==univOldRow&&newCol==univOldCol) {
                    touch_move(x, y);
                    invalidate();
                }
                else{
                    //do nothing.
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG123", "Action Up : ");
                touch_up();
                invalidate();
                break;
        }

        return true;
    }

    public void compTurn(int row, int col, int turn){  //TO BE HANDLED
        Log.d("compTurn", "row: " + row + " col : " + col + " turn : " + turn);
//        Toast.makeText(context, "Computer turn = row: " + row + " col : " + col + " turn : " + turn , Toast.LENGTH_SHORT).show();
    }
}
