package lejos.remote.ev3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import lejos.hardware.Keys;
import lejos.remote.ev3.RemoteKeys.KeysListenThread;

public class RemoteRequestKeys implements Keys {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	
	private Map<Integer,RemoteRequestKey> listeners;
	
	private static final int PRESS_EVENT_SHIFT = 0;
	private static final int RELEASE_EVENT_SHIFT = 8;
	
	public RemoteRequestKeys(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public void discardEvents() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.DISCARD_EVENTS;
		try {
			os.writeObject(req);
		} catch (Exception e) {
		}
	}

	@Override
	public int waitForAnyEvent() {
		return waitForAnyEvent(0);
	}

	@Override
	public int waitForAnyEvent(int timeout) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.WAIT_FOR_ANY_EVENT;
		req.replyRequired = true;
		req.intValue= timeout;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int waitForAnyPress(int timeout) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.WAIT_FOR_ANY_PRESS;
		req.replyRequired = true;
		req.intValue = timeout;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int waitForAnyPress() {
		return waitForAnyPress(0);
	}

	@Override
	public int getButtons() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_BUTTONS;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int readButtons() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.READ_BUTTONS;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void setKeyClickVolume(int vol) {
	}

	@Override
	public int getKeyClickVolume() {
		return 0;
	}

	@Override
	public void setKeyClickLength(int len) {
	}

	@Override
	public int getKeyClickLength() {
		return 0;
	}

	@Override
	public void setKeyClickTone(int key, int freq) {

	}

	@Override
	public int getKeyClickTone(int key) {
		return 0;
	}
	
	void addListener(int iCode,RemoteRequestKey remoteRequestKey) {
		if (listeners == null) {
			listeners = new HashMap<Integer,RemoteRequestKey>();
			new KeysListenThread().start();
		}
		listeners.put(iCode, remoteRequestKey);
	}
	
	class KeysListenThread extends Thread {
		
		public KeysListenThread() {
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while (true) {
				int state = RemoteRequestKeys.this.waitForAnyEvent();
				
				int mask  = 1;
				for (int i=0;i<NUM_KEYS;i++) {
					if ((state & (mask << PRESS_EVENT_SHIFT))  != 0 || (state & (mask << RELEASE_EVENT_SHIFT)) != 0) {;
						RemoteRequestKey key = listeners.get(mask);
						if (key != null) key.callListeners();
					}
					mask <<= 1;
				}
			}
		}
		
	}
}
