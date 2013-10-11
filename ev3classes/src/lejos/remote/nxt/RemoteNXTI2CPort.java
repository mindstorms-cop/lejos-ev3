package lejos.remote.nxt;

import java.io.IOException;

import lejos.hardware.port.I2CPort;

public class RemoteNXTI2CPort extends RemoteNXTIOPort implements I2CPort
{
	
    public RemoteNXTI2CPort(NXTCommand nxtCommand) {
		super(nxtCommand);
	}

    protected boolean getChanged()
    {
        return false;
    }
    
    protected byte getStatus()
    {
        return 0;
    }

    protected void reset()
    {
    	// Do nothing
    }

    protected void setOperatingMode(int typ, int mode)
    {
    	// Do nothing
    }
    
    protected boolean initSensor()
    {
        return true;
    }
    
    /**
     * allow access to the specified port
     * @param p port number to open
     */
    public boolean open(int t, int p, RemoteNXTPort r)
    {
        if (!super.open(t, p, r))
            return false;
        if (!initSensor())
        {
            super.close();
            return false;
        }
        return true;
    }
    
    /**
     * High level i2c interface. Perform a complete i2c transaction and return
     * the results. Writes the specified data to the device and then reads the
     * requested bytes from it.
     * @param deviceAddress The I2C device address.
     * @param writeBuf The buffer containing data to be written to the device.
     * @param writeOffset The offset of the data within the write buffer
     * @param writeLen The number of bytes to write.
     * @param readBuf The buffer to use for the transaction results
     * @param readOffset Location to write the results to
     * @param readLen The length of the read
     * @return < 0 error otherwise the number of bytes read
     */
    public synchronized int i2cTransaction(int deviceAddress, byte[]writeBuf,
            int writeOffset, int writeLen, byte[] readBuf, int readOffset,
            int readLen)
    {
    	System.out.println("Remote I2C transaction on port: " + port + " , address: " + deviceAddress);
		byte [] txData = new byte[writeLen + 1];
	    txData[0] =(byte) deviceAddress;
	    System.arraycopy(writeBuf, writeOffset, txData, 1, writeLen);
		int status;
		try {
			nxtCommand.LSWrite((byte) port, txData, (byte) readLen);
		} catch (IOException ioe) {
			return -1;
		}
		
		do {
			try {
				byte[] ret = nxtCommand.LSGetStatus((byte) port);
				if (ret == null || ret.length < 1) return -1;
				status = (int) ret[0];
			} catch (IOException e) {
				return -1;
			}
			
		} while (status == ErrorMessages.PENDING_COMMUNICATION_TRANSACTION_IN_PROGRESS || 
				 status == ErrorMessages.SPECIFIED_CHANNEL_CONNECTION_NOT_CONFIGURED_OR_BUSY);

		try {
			byte [] ret = nxtCommand.LSRead((byte) port);
            if (ret == null) return -1;
            if (readLen > ret.length) readLen = ret.length;
            if (readLen > 0)
                System.arraycopy(ret, 0, readBuf, readOffset, readLen);
		} catch (IOException ioe) {
			return -1;
		}

		return readLen;
    }  
}
