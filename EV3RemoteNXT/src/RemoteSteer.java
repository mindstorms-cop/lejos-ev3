import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.hardware.Bluetooth;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.remote.nxt.NXTConnection;
import lejos.robotics.navigation.DifferentialPilot;

public class RemoteSteer {

  public static void main(String[] args) throws Exception {
    DifferentialPilot robot = new DifferentialPilot(2.1f,6f,Motor.A, Motor.C,true);
    String connected = "Connected";
        String waiting = "Waiting";
    LCD.drawString(waiting,0,0);
    LCD.refresh();

    NXTConnection btc = Bluetooth.getNXTCommConnector().waitForConnection(0, NXTConnection.PACKET);
        
    LCD.clear();
    LCD.drawString(connected,0,0);
    LCD.refresh();  

    DataInputStream dis = new DataInputStream(btc.openInputStream());
    DataOutputStream dos = new DataOutputStream(btc.openOutputStream());

    while (true) {
      
      int x = dis.readByte();
      int y = dis.readByte() & 0xFF;
      int z = dis.readByte() & 0xFF;
      
      dos.writeByte((byte) 0xFF); // Ack
      dos.flush();
          
      LCD.drawInt(x,4, 0,1);
      LCD.drawInt(y,4, 0,2);
      LCD.drawInt(z,4, 0,3);
      
      int speed = (180 - y) * 20;
      
      LCD.drawInt(speed, 5, 0, 4);
      LCD.refresh();
      
      int ax = (x < 0 ? -x : x);
      
      if (speed > 0) {
        robot.setTravelSpeed(speed);
        System.out.println("forward");
        if (ax < 5) robot.forward();
        else robot.steer(-x);
      } else {
        speed = - speed;
        robot.setTravelSpeed(speed);
        System.out.println("Backward");
        robot.backward();
      }
    }
  }
}

