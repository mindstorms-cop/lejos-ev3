package lejos.internal.brickpi;

import java.io.File;

import javax.sound.sampled.*;

import lejos.hardware.Audio;
import lejos.utility.Delay;

public class BrickPiAudio implements Audio {

  public static float SAMPLE_RATE = 4000f;
  
  public final static int BEEP = 0;
  public final static int DOUBLE_BEEP = 1;
  public final static int ASCENDING = 2;
  public final static int DESCENDING = 3;
  public final static int BUZZ = 4;
  private int volume = 100;
  
  private static int C2 = 523;

  @Override
  public void playTone(int freq, int duration) {
	  playTone(freq, duration, volume);
  }

  @Override
  public void playTone(int hz, int duration, int volume) {
	double vol = volume / 100;
    byte[] buf = new byte[1];
    AudioFormat af = 
        new AudioFormat(
            SAMPLE_RATE, // sampleRate
            8,           // sampleSizeInBits
            1,           // channels
            true,        // signed
            false);      // bigEndian
    SourceDataLine sdl;
	try {
		sdl = AudioSystem.getSourceDataLine(af); 
	    sdl.open(af);
	    sdl.start();
	    for (int i=0; i < duration*4; i++) {
	      double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
	      buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
	      sdl.write(buf,0,1);
	    }
	    sdl.drain();
	    sdl.stop();
	    sdl.close();
	} catch (LineUnavailableException e) {
		// Ignore
	}
  }
  
  @Override
  public void systemSound(int aCode)
  {
      if (aCode == BEEP)
          playTone(600, 200);
      else if (aCode == DOUBLE_BEEP)
      {
          playTone(600, 150);
          Delay.msDelay(50);
          playTone(600, 150);
      }
      else if (aCode == ASCENDING)// C major arpeggio
          for (int i = 4; i < 8; i++)
          {
              playTone(C2 * i / 4, 100);
          }
      else if (aCode == DESCENDING)
          for (int i = 7; i > 3; i--)
          {
              playTone(C2 * i / 4, 100);
          }
      else if (aCode == BUZZ)
      {
          playTone(100, 500);
      }
  }
  
  public static void main(String[] args) throws Exception {
	Audio audio = new BrickPiAudio();
	audio.playTone(784,200);
	Thread.sleep(100);
	audio.playTone(784,200);
	Thread.sleep(100);
	audio.playTone(784,200);
	Thread.sleep(100);
	audio.playTone(622,1000);
	
	for(int i=0;i<5;i++) {
		audio.systemSound(i);
		Delay.msDelay(1000);
	}
  }

	@Override
	public int playSample(File file, int vol) {
		throw new UnsupportedOperationException("playSample not implemented");
	}
	
	@Override
	public int playSample(File file) {
		throw new UnsupportedOperationException("playSample not implemented");
	}
	
	@Override
	public int playSample(byte[] data, int offset, int len, int freq, int vol) {
		throw new UnsupportedOperationException("playSample not implemented");
	}
	
	@Override
	public void playNote(int[] inst, int freq, int len) {
		throw new UnsupportedOperationException("playNote not implemented");
	}
	
	@Override
	public void setVolume(int vol) {
		volume = vol;
	}
	
	@Override
	public int getVolume() {
		return volume;
	}
	
	@Override
	public void loadSettings() {
		// Not yet implemented
	}

}
