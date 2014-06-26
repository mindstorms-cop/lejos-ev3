import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Serial {
	
	InputStream is;
	OutputStream os;

	public void open(String port, int rate) throws SerialPortException {
		CommPortIdentifier id;
		try {
			id = CommPortIdentifier.getPortIdentifier("COM20");
	        SerialPort serialPort = (SerialPort) id.open("BrickPi", 2000);
	        serialPort.setSerialPortParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	        
	        os = serialPort.getOutputStream();
	        is = serialPort.getInputStream();
		} catch (Exception e) {
			throw new SerialPortException(e);
		}
	}

	public void flush() throws IOException {
		os.flush();
		
	}

	public void write(byte[] toSend) throws IOException {
		os.write(toSend);
		
	}

	public int availableBytes() throws IOException {
		return is.available();
	}

	public byte read() throws IOException {
		return (byte) is.read();
	}

}
