import com.ergotech.brickpi.sensors.I2CSensor;

public class I2CTest {
    public static void main(String[] args) throws Exception {
        BrickPi brickPi = BrickPi.getBrickPi();
        // Add a Mindsensors acceleration sensor in tilt mode
        I2CSensor s = new I2CSensor(1, 10, false, false, new byte[] {0x42}, 3);
        brickPi.setSensor(s, 0);
        brickPi.setupSensors();

        for (int i=0;i<10;i++) {
            Thread.sleep(200); 
            System.out.println("Tilt: X:" + s.getValue(0,0) + " Y:" + s.getValue(0,1) + " Z:" + s.getValue(0,2));
        }
        
        // Change to acceleration
        s.setData(0, new byte[] {0x45}, 3);
        for (int i=0;i<10;i++) {
            Thread.sleep(200); 
            System.out.println("Acceleration: X:" + s.getValue(0,0) + " Y:" + s.getValue(0,1) + " Z:" + s.getValue(0,2));
        }
    }
}
