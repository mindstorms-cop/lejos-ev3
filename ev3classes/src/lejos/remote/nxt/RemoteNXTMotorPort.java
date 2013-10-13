package lejos.remote.nxt;

import java.io.IOException;

import lejos.hardware.port.BasicMotorPort;
import lejos.hardware.port.TachoMotorPort;

/**
 * 
 * Abstraction for an EV3 output port.
 * 
 * TODO: Sort out a better way to do this, or least clean up the magic numbers.
 *
 */
public class RemoteNXTMotorPort extends RemoteNXTIOPort implements NXTProtocol, TachoMotorPort {
    public RemoteNXTMotorPort(NXTCommand nxtCommand) {
		super(nxtCommand);
	}

    /**
     * Low-level method to control a motor. 
     * 
     * @param power power from 0-100
     * @param mode defined in <code>BasicMotorPort</code>. 1=forward, 2=backward, 3=stop, 4=float.
     * @see BasicMotorPort#FORWARD
     * @see BasicMotorPort#BACKWARD
     * @see BasicMotorPort#FLOAT
     * @see BasicMotorPort#STOP
     */
    public void controlMotor(int power, int mode)
    {
		int lcpMode = 0, lcpPower = power, runState = 0;
		
		if (mode == 1) { // forward
			lcpMode = MOTORON;
			runState = MOTOR_RUN_STATE_RUNNING;
		} else if (mode == 2) { // backward
			lcpMode = MOTORON;
			lcpPower = -lcpPower;
			runState = MOTOR_RUN_STATE_RUNNING;
		} else if (mode == 3) { // stop
			lcpPower = 0;
			lcpMode = BRAKE;
			runState = MOTOR_RUN_STATE_IDLE;
		} else { // float
			lcpPower = 0;
			lcpMode = 0;
			runState = MOTOR_RUN_STATE_IDLE;			
		}
		try {
			nxtCommand.setOutputState((byte) port, (byte) lcpPower, lcpMode, REGULATION_MODE_IDLE, 0, runState, 0);
		} catch (IOException ioe) {}
    }


    /**
     * returns tachometer count
     */
    public  int getTachoCount()
    {
		try {
			return nxtCommand.getTachoCount(port);
		} catch (IOException ioe) {
			return 0;
		}
    }
    
    /**
     *resets the tachometer count to 0;
     */ 
    public void resetTachoCount()
    {
		try {
			nxtCommand.resetMotorPosition(port, false);
		} catch (IOException ioe) { }
    }
    
    public void setPWMMode(int mode)
    {
    }
    
}
