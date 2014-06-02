import lejos.hardware.Audio;
import lejos.hardware.brickpi.BrickPi;
import lejos.hardware.brickpi.LocalBrickPi;


public class SoundTest {

	public static void main(String[] args) {
		BrickPi pi = LocalBrickPi.get();
		Audio audio = pi.getAudio();
		
		audio.playTone(622, 1000);

	}

}
