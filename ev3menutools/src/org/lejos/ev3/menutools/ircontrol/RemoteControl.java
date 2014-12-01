package org.lejos.ev3.menutools.ircontrol;

import lejos.hardware.*;
import lejos.hardware.device.DeviceIdentifier;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.*;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3SensorConstants;
import lejos.utility.Delay;

public class RemoteControl {

	protected GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	private EV3IRSensor ir;
	
	// Unregulated motors:
	private UnregulatedMotor motorA = new UnregulatedMotor(MotorPort.A);
	private UnregulatedMotor motorB = new UnregulatedMotor(MotorPort.B);
	private UnregulatedMotor motorC = new UnregulatedMotor(MotorPort.C);
	private UnregulatedMotor motorD = new UnregulatedMotor(MotorPort.D);
	
	// Will autodetect port with IR sensor
	Port [] sensorPorts = {SensorPort.S1, SensorPort.S2,SensorPort.S3, SensorPort.S4}; 
		
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
		
		LCD.drawString("Wait...", 0, 0);
		LCD.refresh();
		
		RemoteControl tool = new RemoteControl();
		Sound.beep();
		tool.g.setAutoRefresh(false); // TODO: RELOCATE
		tool.autodetectIRSensor();
		tool.initScreen();
        tool.monitorIR();
		
		Sound.beepSequence();
	}
	
	/**
	 * Loop to continually monitor IR input and update motors and GUI
	 */
	public void monitorIR() {

		boolean keep_looping = true;
		int previous_command = 0;
		int channel = 0;
		UnregulatedMotor currentLeft = motorB;
		UnregulatedMotor currentRight = motorC;
		
		while(keep_looping) {
			Delay.msDelay(25);
			
			// Get the IR commands
			byte [] cmds = new byte[4];
			ir.getRemoteCommands(cmds, 0, cmds.length);
			
			// Figure out which channel is active:
			int command = 0;
			for(int i=0;i<4;i++) {
				if(cmds[i] > 0) {
					channel = i;
					command = cmds[i];
				}
			}
			
			// Make motor and GUI changes according to active channel:
			if(channel == 0) {
				currentLeft = motorB;
				currentRight = motorC;
				drawChannelCircle(circleX, circleY, '1');
				//redrawMotors("B", "C");
			} 
			else if(channel == 1) {
				currentLeft = motorA;
				currentRight = motorD;
				drawChannelCircle(circleX, circleY, '2');
				//redrawMotors("A", "D");				
			}
			else if(channel == 2) {
				currentLeft = motorA;
				currentRight = motorB;
				drawChannelCircle(circleX, circleY, '3');
				//redrawMotors("A", "D");				
			}
			else if(channel == 3) {
				currentLeft = motorC;
				currentRight = motorD; // BC actually * See hack below
				drawChannelCircle(circleX, circleY, '4');
				//redrawMotors("A", "D");				
			}
			
			if(command != previous_command) {
				//NOTE: 10 means top-left and bottom-left. (11 too) Cancels out:
				if(command==0|command==3|command==4|command==10) {// left buttons not pressed
					currentLeft.setPower(0);
					redrawButton(1, false);
					redrawButton(2, false);
				}
				if(command==0|command==1|command==2|command==11) {// right buttons not pressed
					currentRight.setPower(0);
					redrawButton(3, false);
					redrawButton(4, false);
				}
				if(command==1|command==5|command==6) { // upper-left
					currentLeft.forward();
					currentLeft.setPower(100);
					redrawButton(1, true);
				}
				if(command==2|command==7|command==8) { // lower-left
					currentLeft.backward();
					currentLeft.setPower(100);
					redrawButton(2, true);
				}
				if(command==3|command==5|command==7) { // upper-right
					currentRight.forward();
					currentRight.setPower(100);
					redrawButton(3, true);
				}
				if(command==4|command==6|command==8) { // lower-right
					currentRight.backward();
					currentRight.setPower(100);
					redrawButton(4, true);
				}
				redrawMotors(channel);
				previous_command = command;
				g.refresh();
				
			}
						
			if (Button.ESCAPE.isDown()) keep_looping = false;
		}
		
        ir.close();
        motorA.close();
        motorB.close();
        motorC.close();
        motorD.close();
	}

	public void autodetectIRSensor() {
		int irPort;
		do {
			irPort = detectIRSensorPort();
			if(irPort >= 0) {
				LCD.drawString("Detected port " + (irPort+1), 0, 4);
				LCD.refresh();
				ir = new EV3IRSensor(sensorPorts[irPort]);
			} 
			else {
				LCD.drawString("Plug IR sensor", 0, 0);
				LCD.drawString("into any port", 0, 1);
				LCD.drawString("ENTER+DOWN Quits", 0, 3);
				LCD.refresh();
			}
				
		} while(irPort < 0);
		g.clear(); // Clears previous messages
	}
	
	private int detectIRSensorPort() {
		int detectedPort = -99;
		// run device detection in parallel to reduce detection time 
        DeviceIdentifier []ids = new DeviceIdentifier[4];
        for(int i=0; i<4; i++)
            ids[i] = new DeviceIdentifier(sensorPorts[i]);
		for(int i=0;i<4;i++) {
		    if (ids[i].getDeviceSignature(false).contains("IR-PROX"))
		        detectedPort = i;
		    ids[i].close();
		}
		return detectedPort;
	}
	
	/**
	 * Initial drawing of the screen UI.
	 */
	public void initScreen() {
		
		g.setFont(Font.getLargeFont());
		g.drawString("IR", b_top_y, b_top_y, 0);
		
		//redrawMotors("B", "C");
		redrawMotors(-99);
		
		// Center IR Port message near bottom:
		/*
		g.setFont(Font.getSmallFont()); // can also get specific size using Font.getFont()
		String ir_str ="IR Sensor in Port 4";
		int str_width = Font.getSmallFont().stringWidth(ir_str);
		int box_border = 4; // spacing for rectangles drawn around text
		int str_x_offset = LCD.SCREEN_WIDTH/2 - str_width/2;
		int str_y_offset = LCD.SCREEN_HEIGHT - Font.getSmallFont().getHeight() - box_border*2;
		g.drawString(ir_str, str_x_offset, str_y_offset, 0);
		
		// Draw box around IR port message:
		g.drawRect(str_x_offset - box_border, str_y_offset - box_border, str_width + box_border*2, Font.getSmallFont().getHeight() + box_border*2);
		*/
		
		// Quit indicator:
		g.setFont(Font.getSmallFont()); // can also get specific size using Font.getFont()
		int y_quit = b_top_y * 2 + Font.getLargeFont().height;
		int width_quit = 45;
		int height_quit = width_quit/2;
		int arc_diam = 6;
		g.drawString("QUIT", 7, y_quit+7, 0);
		g.drawLine(0, y_quit,  45, y_quit); // top line
		g.drawLine(0, y_quit,  0, y_quit+height_quit-arc_diam/2); // left line
		g.drawLine(width_quit, y_quit,  width_quit, y_quit+height_quit/2); // right line
		g.drawLine(0+arc_diam/2, y_quit+height_quit,  width_quit-10, y_quit+height_quit); // bottom line
		g.drawLine(width_quit-10, y_quit+height_quit, width_quit, y_quit+height_quit/2); // diagonal
		g.drawArc(0, y_quit+height_quit-arc_diam, arc_diam, arc_diam, 180, 90);
		
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
		
		g.refresh();
	}
	
	public void redrawMotors(int highlightChannel) {
		String [] leftMotor = {"B", "A", "A", "C"};
		String [] rightMotor = {"C", "D", "B", "D"};
		
		int y = height * 2 + width;
		
		// Clear area first:
		g.setColor(1);
		g.fillRect(b_left_x, y, 80, 45);
		g.setColor(0);
		g.drawRect(b_left_x-20, y-4, 110, 45);
		
		// Draw all motor ports:
		for(int i = 0;i<leftMotor.length;i++) {
			
			g.setFont(Font.getSmallFont());
			int str_width = g.getFont().width;//.stringWidth(leftMotor[i]);
			int x1 = b_left_x+width/2-str_width/2;
			int x2 = b_right_x+width/2-str_width/2;
			
			g.drawString("ch" + (i+1), x1-25, y, 0);
			g.drawString(leftMotor[i], x1, y, 0);
			str_width = Font.getDefaultFont().stringWidth(rightMotor[i]);
			g.drawString(rightMotor[i], x2, y, 0);
			
			if(highlightChannel == i)
				g.fillRect(x1 + 20, y+2, 30, 3);
			
			//drawChannelCircle(circleX, y, '1');
			
			// Before loop starts again, make sure next row starts at proper y:
			y = y + g.getFont().getHeight() + 2;
			 
		}
		g.refresh();
	}
	
	/*
	 * Draws only the motors for the channel last pressed on the remote.
	 * REPLACED BY METHOD ABOVE. UNUSED!
	 */
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
