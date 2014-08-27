package lejos.hardware.video;

public interface Video {  
    /**
     * Open the device and make it available for use, specify the desired frame size. Note that the actual
     * frame size may be adjusted to conform to the capabilities of the device.
     * @param w the desired frame width
     * @param h the desired frame height
     * @throws java.io.IOException
     */
    public void open(int w, int h) throws java.io.IOException;

    /**
     * Grab a single frame from the device and store the image into the supplied array
     * @param frame array to store the frame
     * @throws java.io.IOException
     */
    public void grabFrame(byte[] frame) throws java.io.IOException;
  
    /**
     * Close the webcam, the device will not be available after this call  
     * @throws java.io.IOException
     */
    public void close() throws java.io.IOException;
      
  

    /**
     * Create a byte array suitable for holding a single video frame
     * @return the frame array
     */
    public byte[] createFrame();
  
    /**
     * Return the frame width
     * @return width in pixels
     */
    public int getWidth();

    /**
     * return the frame height
     * @return height in pixels
     */
    public int getHeight();
    

}  