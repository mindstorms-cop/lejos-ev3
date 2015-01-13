package lejos.ev3.newstartup;

import lejos.ev3.newstartup.gui.DetailMenu;
import lejos.ev3.newstartup.gui.Submenu;

public class Startup {
	
	public static void main( String[] args ) {
		DetailMenu menu = new DetailMenu();
		menu.setItems( new Submenu[] {
			new WiFiMenu(),
			new BluetoothMenu(),
			new SystemMenu()
		});
		menu.start();
	}
	
}
