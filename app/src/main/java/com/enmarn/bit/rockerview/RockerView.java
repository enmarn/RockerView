package com.enmarn.bit.rockerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


/**
 * Created by why on 19/4/19.
 */

public class RockerView extends RelativeLayout {
    
    //自定义的属性
    //背景圆的圆心的XY坐标与半径
    private int X = 50;
    private int Y = 50;
    private int backCircleRadius = 100;
    private Button backCircle;
    //中心球的圆心的XY坐标与半径
    private int centerCircleX = 0;
    private int centerCircleY = 0;
    private int centerCircleRadius = 50;
    private Button frontCircle;

    public RockerView(Context context, AttributeSet attrs) {
        super(context,attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RockerView);
        if(typedArray!=null){
            backCircleRadius = typedArray.getInt(R.styleable.RockerView_back_clrcle_radius, 100);
            X = backCircleRadius;
            Y = backCircleRadius;
            centerCircleRadius = typedArray.getInt(R.styleable.RockerView_center_circle_radius, 50);
            typedArray.recycle();
        }
        View v = LayoutInflater.from(context).inflate(R.layout.rocker_view, this, true);
        backCircle = v.findViewById(R.id.back_circle);
        frontCircle = v.findViewById(R.id.center_circle);
        backCircle.getLayoutParams().width=backCircleRadius*2;
        backCircle.getLayoutParams().height=backCircleRadius*2;
        frontCircle.getLayoutParams().width=centerCircleRadius*2;
        frontCircle.getLayoutParams().height=centerCircleRadius*2;
        setCenterCircleXY(0,0);
    }
    public interface onTouchListener{
        /**
         * @param event
         */
        public void onTouch(RockerEvent event);
    }
    public class RockerEvent {
        public static final int ACTION_DOWN = MotionEvent.ACTION_DOWN;
        public static final int ACTION_UP = MotionEvent.ACTION_UP;
        public static final int ACTION_MOVE = MotionEvent.ACTION_MOVE;

        public int type;
        public double rad;
        public double offset;

        public RockerEvent(int type, double rad, double offset) {
            this.type = type;
            this.rad = rad;
            this.offset = offset;
        }
    }
    public void setOnTouchListener(final onTouchListener onTouch){
        backCircle.setOnClickListener(null);
        frontCircle.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_MOVE||event.getAction()==MotionEvent.ACTION_DOWN){
                    int[] location = new  int[2] ;
                    //获得大圆的绝对位置
                    backCircle.getLocationOnScreen(location);
                    //使用getXY()会发生抖动,getRawXY()为事件的绝对位置
                    //xy为向量<大圆心,触摸点>
                    int x = (int) (event.getRawX() - location[0]);
                    int y = (int) (event.getRawY() - location[1]);
                    double offset = Math.sqrt(Math.pow((X - x), 2) + Math.pow((Y - y), 2));
                    double rad = getRad(X, Y, x, y);
                    offset /= (backCircleRadius-centerCircleRadius);
                    if ( offset>= 1) {
                        setCenterCircleXY(rad,(double) 1);
                        // 回调事件
                        onTouch.onTouch(new RockerEvent(event.getAction(),-rad,1));
                    }else {//范围内触摸
                        setCenterCircleXY(rad,offset);
                        // 回调事件
                        onTouch.onTouch(new RockerEvent(event.getAction(),-rad,offset));
                    }
                } else if (event.getAction()==MotionEvent.ACTION_UP){
                    setCenterCircleXY(0,0);
                    // 回调事件
                    onTouch.onTouch(new RockerEvent(event.getAction(),0,0));
                }
                return true;
            }
        });
    }
    /**
     * 设置中心的操作球的XY坐标(以背景圆圆心为坐标原点),若超界则被边界阻挡
     * @param rad 弧度
     * @param offset 两圆心的偏移量【0-1】
     */
    public void setCenterCircleXY(double rad, double offset){
        offset = offset>=1?1:offset;
        centerCircleX = (int) ((backCircleRadius-centerCircleRadius) * offset * Math.cos(rad) );
        centerCircleY = (int) ((backCircleRadius-centerCircleRadius) * offset * Math.sin(rad) );
        frontCircle.setX((float)(backCircleRadius + centerCircleX - centerCircleRadius));
        frontCircle.setY((float)(backCircleRadius + centerCircleY - centerCircleRadius));
    }
    /**
     * 设置中心的操作球的XY坐标(以背景圆圆心为坐标原点),若超界则被边界阻挡
     * @param X
     * @param Y
     */
    public void setCenterCircleXY(int X, int Y){
        if(X*X+Y*Y<=centerCircleRadius*centerCircleRadius) {
            //界内，直接设置XY坐标
            centerCircleX = X;
            centerCircleY = Y;
            frontCircle.setX((float)(backCircleRadius + X - centerCircleRadius));
            frontCircle.setY((float)(backCircleRadius + Y - centerCircleRadius));
        } else {
            float rad = getRad(0, 0, X, Y);
            centerCircleX = (int) ((backCircleRadius-centerCircleRadius) * Math.cos(rad) );
            centerCircleY = (int) ((backCircleRadius-centerCircleRadius) * Math.sin(rad) );
            frontCircle.setX((float)(backCircleRadius + centerCircleX - centerCircleRadius));
            frontCircle.setY((float)(backCircleRadius + centerCircleY - centerCircleRadius));
        }
    }


    /**
     * 计算向量P1P2与X轴之间的弧度
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return -pai~pai
     */
    private float getRad(float x1, float y1, float x2, float y2){
        float x = x2 - x1;
        float y = y2 - y1;
        float z = (float) Math.sqrt(x*x+y*y);
        float cos = x / z;
        float rad = (float) Math.acos(cos);
        return y>0?rad:-rad;
    }

}
