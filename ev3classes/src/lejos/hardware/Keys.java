package lejos.hardware;

public interface Keys {

	public void discardEvents();
	
	public int waitForAnyEvent();
	
	public int waitForAnyEvent(int timeout);
	
	public int waitForAnyPress(int timeout);
	
	public int waitForAnyPress();
	
	public int getButtons();
	
	public int readButtons();
	
	public void setKeyClickVolume(int vol);
	
	public int getKeyClickVolume();
	
	public void setKeyClickLength(int len);
	
	public int getKeyClickLength();
	
	public void setKeyClickTone(int key, int freq);
	
	public int getKeyClickTone(int key);
	
}
