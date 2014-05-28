package lejos.internal.brickpi;

import java.io.IOException;

import com.ergotech.brickpi.motion.Motor;

import lejos.hardware.motor.MotorRegulator;
import lejos.hardware.port.TachoMotorPort;

public class BrickPiMotorPort extends BrickPiIOPort implements TachoMotorPort {
	Motor motor;
	
	public boolean open(int typ, int port, BrickPiPort ref) {
		if (!super.open(typ, port, ref)) return false;
		motor = new Motor();
		System.out.println("Setting motor on port " + port);
		com.ergotech.brickpi.BrickPi.getBrickPi().setMotor(motor, port);
		motor.setCommandedOutput(0);
		motor.setEnabled(true);
        try {
            // configure the sensors
        	com.ergotech.brickpi.BrickPi.getBrickPi().setupSensors();
        } catch (IOException ex) {
        	System.err.println("Failed to configure sensors");
        }
        return true;
	}

	@Override
	public void controlMotor(int power, int mode) {
		if (mode == BACKWARD) power = -power;
		else if (mode == STOP || mode == FLOAT) power = 0;
		motor.setCommandedOutput((int) (2.55 * power));	
	}

	@Override
	public void setPWMMode(int mode) {
		// Not implemented		
	}

	@Override
	public void close() {
        motor.setCommandedOutput(0);
        motor.setEnabled(false);
	}

	@Override
	public int getTachoCount() {
		return motor.getCurrentEncoderValue();
	}

	@Override
	public void resetTachoCount() {
		motor.resetEncoder();
	}

	@Override
	public MotorRegulator getRegulator() {
		throw new UnsupportedOperationException("Regulator not supported");
	}
}
