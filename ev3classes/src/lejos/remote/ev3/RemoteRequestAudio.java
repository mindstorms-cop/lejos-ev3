package lejos.remote.ev3;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.Audio;

public class RemoteRequestAudio implements Audio {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	
	public RemoteRequestAudio(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}
	
	@Override
	public void systemSound(int aCode) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.SYSTEM_SOUND;
		req.intValue = aCode;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playTone(int aFrequency, int aDuration, int aVolume) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PLAY_TONE_VOL;
		req.intValue = aFrequency;
		req.intValue2 = aDuration;
		req.intValue3 = aVolume;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playTone(int freq, int duration) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PLAY_TONE;
		req.intValue = freq;
		req.intValue2 = duration;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int playSample(File file, int vol) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PLAY_SAMPLE_VOL;
		req.replyRequired = true;
		req.intValue = vol;
		req.file = file;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int playSample(File file) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PLAY_SAMPLE;
		req.replyRequired = true;
		req.file = file;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int playSample(byte[] data, int offset, int len, int freq, int vol) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PLAY_SAMPLE_DATA;
		req.replyRequired = true;
		req.byteData = data;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void playNote(int[] inst, int freq, int len) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PLAY_NOTE;
		req.intData = inst;
		req.intValue = freq;
		req.intValue2 = len;
		try {
			os.writeObject(req);
		} catch (Exception e) {
		}	
	}

	@Override
	public void setVolume(int vol) {	
	}

	@Override
	public int getVolume() {
		return 0;
	}

	@Override
	public void loadSettings() {		
	}
}
