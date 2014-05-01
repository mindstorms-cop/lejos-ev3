package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.robotics.SampleProvider;

public class RemoteRequestSampleProvider implements SampleProvider {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private int portNum;

	public RemoteRequestSampleProvider(ObjectInputStream is,
			ObjectOutputStream os, String portName, String sensorName, String modeName) {
		this.is = is;
		this.os = os;
		portNum = portName.charAt(1) - '1';
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CREATE_SAMPLE_PROVIDER;
		req.intValue = portNum;
		req.str = sensorName;
		req.str2 = portName;
		req.str3 = modeName;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int sampleSize() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.SAMPLE_SIZE;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.FETCH_SAMPLE;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			for(int i=0;i<reply.floats.length;i++) sample[offset+i] = reply.floats[i];
		} catch (Exception e) {
		}
	}
	
	public void close() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CLOSE_SENSOR;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
