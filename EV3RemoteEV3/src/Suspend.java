import java.rmi.Naming;

import lejos.remote.ev3.RMIMenu;

public class Suspend {
	
	public static final String remoteHost = "//192.168.0.9/";

	public static void main(String[] args) throws Exception {
		RMIMenu menu = (RMIMenu)Naming.lookup(remoteHost + "RemoteMenu");
		
		System.out.println("Suspending");
		menu.suspend();
		
		Thread.sleep(10000);
		
		System.out.println("Resuming");
		menu.resume();

	}

}
