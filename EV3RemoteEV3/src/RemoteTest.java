import java.io.IOException;

import lejos.hardware.Audio;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;


public class RemoteTest {


	public static void main(String[] args) {
		try {
			RemoteRequestEV3 ev3 = new RemoteRequestEV3("192.168.0.9");
			Audio audio = ev3.getAudio();
			audio.systemSound(2);
			System.out.println("connected to ev3 " + ev3.getType());
			System.out.println("connected to ev3 " + ev3.getName());
			//ev3.setDefault();
			RegulatedMotor motor = ev3.createRegulatedMotor("A", 'L');
			System.out.println(motor.getMaxSpeed());
			motor.setSpeed(Math.round(motor.getMaxSpeed()));
			motor.rotate(1000);
			//motor.waitComplete();
			motor.close();
			audio.systemSound(3);
			ev3.disConnect();
			System.out.println("ende");
		} catch (IOException e) {
			System.err.println("An exception occurred: " + e);
		}

	}

}
