package org.lejos.ev3.menutools.ircontrol;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.*;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;

public class IRControl {

	protected GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	private EV3IRSensor ir = new EV3IRSensor(SensorPort.S4);
	// Unregulated motors:
	private NXTMotor motorA = new NXTMotor(MotorPort.A);
	private NXTMotor motorB = new NXTMotor(MotorPort.B);
	private NXTMotor motorC = new NXTMotor(MotorPort.C);
	private NXTMotor motorD = new NXTMotor(MotorPort.D);
	
	// GLOBAL GUI WIDGET PARAMETERS:
	int width = 20; // width of each button and spacer
	int height = 32; // height of buttons
	int b_left_x = 60; // x pos of left buttons
	int b_right_x = 120; // x pos of right buttons
	int b_top_y = 2; // y pos of both top buttons
	int b_bottom_y = b_top_y+ height + width/2; // y pos of both bottom buttons
	int circleX = (b_left_x + b_right_x)/2 + 4;
	int circleY = b_top_y + 10;
	
	public static void main(String[] args) {
		Sound.beep();
		
		IRControl tool = new IRControl();
		tool.initScreen();
        tool.monitorIR();
		
		Sound.beepSequence();
	}
	
	/**
	 * Loop to continually monitor IR input and update motors and GUI
	 */
	public void monitorIR() {

		boolean keep_looping = true;
		int old_val_1 = 0;
		int old_val_2 = 0;
		int old_val_3 = 0;
		int old_val_4 = 0;
		
		while(keep_looping) {
			Delay.msDelay(100);
			
			int channel1 = ir.getRemoteCommand(0);
			if(channel1 != old_val_1) {
				// TODO: Use binary math to allow buttons held at same time?
				switch (channel1) {
					case 0:	motorB.setPower(0);
							motorC.setPower(0);
							break;
					case 1:	motorB.forward();
							motorB.setPower(100);
							break;
					case 2:	motorB.backward();
							motorB.setPower(100);
							break;
					case 3:	motorC.forward();
							motorC.setPower(100);
							break;
					case 4:	motorC.backward();
							motorC.setPower(100);
							break;
				}
				// Redraw old and new buttons:
				redrawButtons(channel1, old_val_1);
				old_val_1 = channel1;
				drawChannelCircle(circleX, circleY, '1');
				redrawMotors("B", "C");
				continue;
			}
			
			int channel2 = ir.getRemoteCommand(1);
			if(channel2 != old_val_2) {
				switch (channel2) {
					case 0:	motorA.setPower(0);
							motorD.setPower(0);
							break;
					case 1:	
							motorA.forward();
							motorA.setPower(100);
							break;
					case 2:	motorA.backward();
							motorA.setPower(100);
							break;
					case 3:
							motorD.forward();
							motorD.setPower(100);
							break;
					case 4:	motorD.backward();
							motorD.setPower(100);
							break;
				}
				// Redraw old and new buttons:
				redrawButtons(channel2, old_val_2);
				old_val_2 = channel2;
				drawChannelCircle(circleX, circleY, '2');
				redrawMotors("A", "D");
				continue;
			}
			
			int channel3 = ir.getRemoteCommand(2);
			int channel4 = ir.getRemoteCommand(3);
						
			if (Button.ESCAPE.isDown()) keep_looping = false;
		}
	}
	
	/**
	 * Initial drawing of the screen UI.
	 */
	public void initScreen() {
		
		g.setFont(Font.getLargeFont());
		g.drawString("IR", b_top_y, b_top_y, 0);
		
		redrawMotors("B", "C");
		
		// Center IR Port message near bottom:
		String ir_str ="IR Sensor in Port 4";
		int str_width = Font.getSmallFont().stringWidth(ir_str);
		int box_border = 4; // spacing for rectangles drawn around text
		g.setFont(Font.getSmallFont()); // can also get specific size using Font.getFont()
		int str_x_offset = LCD.SCREEN_WIDTH/2 - str_width/2;
		int str_y_offset = LCD.SCREEN_HEIGHT - Font.getSmallFont().getHeight() - box_border*2;
		g.drawString(ir_str, str_x_offset, str_y_offset, 0);
		
		// Draw box around IR port message:
		g.drawRect(str_x_offset - box_border, str_y_offset - box_border, str_width + box_border*2, Font.getSmallFont().getHeight() + box_border*2);
		
		// Quit indicator:
		int y_quit = b_top_y * 2 + Font.getLargeFont().height;
		int width_quit = 45;
		int height_quit = width_quit/2;
		g.drawString("QUIT", 7, y_quit+7, 0);
		g.drawLine(0, y_quit,  45, y_quit); // top line
		g.drawLine(0, y_quit,  0, y_quit+height_quit); // left line
		g.drawLine(width_quit, y_quit,  width_quit, y_quit+height_quit/2); // right line
		g.drawLine(0, y_quit+height_quit,  width_quit-10, y_quit+height_quit); // bottom line
		g.drawLine(width_quit-10, y_quit+height_quit, width_quit, y_quit+height_quit/2); // diagonal
		
		// Draw top 2 buttons:
		drawButton(b_left_x, b_top_y, width, height);
		drawButton(b_right_x, b_top_y, width, height);
		
		// Draw red and blue spacers:
		g.drawRect(b_left_x, b_top_y+height+2, width, width/4);
		g.fillRect(b_right_x, b_top_y+height+2, width, width/4);
		
		// Draw bottom 2 buttons:
		drawButton(b_left_x, b_bottom_y, width, height, true, false);
		drawButton(b_right_x, b_bottom_y, width, height, true, false);
		
		// Draw Middle Circle with Number:
		drawChannelCircle(circleX, circleY, '1');
	}
	
	public void redrawMotors(String first, String second) {
		g.setFont(Font.getDefaultFont());
		int str_width = Font.getDefaultFont().stringWidth(first);
		int x1 = b_left_x+width/2-str_width/2;
		int x2 = b_right_x+width/2-str_width/2;
		int y = height * 2 + width;
		g.setColor(1);
		g.fillRect(x1, y, Font.getDefaultFont().width, Font.getDefaultFont().height);
		g.fillRect(x2, y, Font.getDefaultFont().width, Font.getDefaultFont().height);
		g.setColor(0);
		g.drawString(first, x1, y, 0);
		str_width = Font.getDefaultFont().stringWidth(second);
		g.drawString(second, x2, y, 0);
	}
	
	public void redrawButtons(int button, int oldButton) {
		redrawButton(button, true);
		redrawButton(oldButton, false);
	}
	
	public void redrawButton(int button, boolean filled) {
		if(button == 0) return;
		int x = b_left_x;
		int y = b_top_y;
		boolean flip = false;
		if(button == 3||button == 4){
			x = b_right_x;
		}
		if(button == 2||button == 4){
			y = b_bottom_y;
			flip = true;
		}
				
		// Blank rectangle around button
		g.setColor(1); // white
		g.fillRect(x, y, width, height);
		
		// Draw button
		g.setColor(0); // black
		drawButton(x, y, width, height, flip, filled);
	}
	
	/**
	 * This method draws a circle surrounding a character. It indicates the
	 * channel the user is currently using.
	 * @param x
	 * @param y
	 * @param channel
	 */
	private void drawChannelCircle(int x, int y, char channel) {
		int diam = 12;
		g.setFont(Font.getSmallFont());
		g.setColor(1);
		g.fillRect(x, y, diam, diam);
		g.setColor(0);
		g.drawArc(x, y, diam, diam, 0, 360);
		g.drawChar(channel, x+3, y+3, 0);
	}
	
	/**
	 * A method to draw a half-round button (found on the EV3 IR remote). Boolean
	 * options to draw it upside down (flip) or fill it in (pressed). The other parameters
	 * are self explanatory. NOTE: This method does not clear pixels before drawing the button.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param flip
	 * @param fill
	 */
	private void drawButton(int x, int y, int width, int height, boolean flip, boolean fill){
		int arcHeight = (int)(width * 0.8);
		int startArcAngle = 0;
		int yLineStart = y + arcHeight/2;
		int yLineEnd = y + height;
		int yArc = y;
		int rectCornerY = yLineStart;
		if (flip) {
			startArcAngle = 180;
			yLineEnd = y;
			yLineStart = y + height - arcHeight/2;
			yArc = y + height - arcHeight;
			rectCornerY = y;
		}
		if(fill) {
			g.fillArc(x, yArc, width, arcHeight, startArcAngle, 180);  // rounded end
			g.fillRect(x, rectCornerY, width, height-arcHeight/2);
		} else {
			g.drawArc(x, yArc, width, arcHeight, startArcAngle, 180);  // rounded end
			g.drawLine(x, yLineStart, x, yLineEnd); // left
			g.drawLine(x+width, yLineStart, x+width, yLineEnd); // right
			g.drawLine(x, yLineEnd, x+width, yLineEnd); // opposite end
		}
	}
	
	private void drawButton(int x, int y, int width, int height){
		drawButton(x, y, width, height, false, false);
	}
	
}
