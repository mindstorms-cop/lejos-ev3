package org.lejos.android.sample.ev3androidrotate;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RotationControl extends ImageView  {
  
  private float angle = 0f, oldTouchAngle = 0f;
  private RotationListener listener;
  
  public interface RotationListener {
    public void onAngleChanged(int angle);
  }
  
  public void setRotationListener(RotationListener l) {
    listener = l;
  }
  
  public RotationControl(Context context) {
    super(context);
    initialize();
  }
  
  public RotationControl(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }
  
  public RotationControl(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize();
  }
  
  private float getTouchAngle(float x, float y) {
    float sx = x - (getWidth() / 2.0f);
    float sy = y - (getHeight() / 2.0f);
    
    float length = (float) Math.sqrt(sx*sx + sy*sy);
    float nx = sx / length;
    float ny = sy / length;
    
    return (float) ((Math.atan2(ny, nx) * (180.0/Math.PI)));
  }
  
  public void initialize() {
    setImageResource(R.drawable.rotate);
    setOnTouchListener(new OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
           float x = event.getX(0);
           float y = event.getY(0);
           float newTouchAngle = getTouchAngle(x,y);
           float delta = newTouchAngle - oldTouchAngle;
           oldTouchAngle = newTouchAngle;
        
           switch(event.getAction() & MotionEvent.ACTION_MASK) {
           case MotionEvent.ACTION_DOWN:
        	 oldTouchAngle = newTouchAngle;
        	 break;
           case MotionEvent.ACTION_MOVE:
             invalidate();
             angle += delta;
             break;
           case MotionEvent.ACTION_UP:
        	 notifyListener((int) angle);
        	 break;
           }
           return true;
         }
      });
  }
  
  private void notifyListener(int e) {
    if (listener!=null) listener.onAngleChanged(e);
  }
  
  protected void onDraw(Canvas c) {
    c.rotate(angle, getWidth()/2, getHeight()/2);   
    super.onDraw(c);
  }
} 
