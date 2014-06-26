import com.ergotech.brickpi.sensors.EV3UltraSonicSensor;

public class EV3Test {
    public static void main(String[] args) throws Exception {
        BrickPi brickPi = BrickPi.getBrickPi();
        brickPi.setSensor(new EV3UltraSonicSensor(), 0);
        brickPi.setupSensors();
        for (int i=0;i<10;i++) {
            Thread.sleep(200); 
            System.out.println("Distance:" + brickPi.getSensor(0).getValue());
        }
        System.exit(0);
    }
}
