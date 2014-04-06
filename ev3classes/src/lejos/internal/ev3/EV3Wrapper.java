package lejos.internal.ev3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.Font;

import lejos.hardware.lcd.LCDOutputStream;
import lejos.hardware.lcd.TextLCD;

public class EV3Wrapper implements UncaughtExceptionHandler {

	public static void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new EV3Wrapper());
		OutputStream lcdOut = new LCDOutputStream();
		System.setOut(new RedirectStream(System.out, lcdOut));
		System.setErr(new RedirectStream(System.err, lcdOut));
		invokeClass(args[0], new String[0]);
	}
	
	private static void invokeClass(String name, String[] args) 
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
	    Class<?> c = Class.forName(name);
	    Method m = c.getMethod("main", new Class[] { args.getClass() });
	    m.setAccessible(true);
	    int mods = m.getModifiers();
	    
	    if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
	        !Modifier.isPublic(mods)) {
	        throw new NoSuchMethodException("main");
	    }
	    
	    try {
	        m.invoke(null, new Object[] { args });
	    } catch (IllegalAccessException e) {
	        // This should not happen, as we have disabled access checks
	    }
	}

	@Override
	public void uncaughtException(Thread th, Throwable t) {
		Sound.buzz();
		// Get rid of invocation exception
	    if (t.getCause() != null) t = t.getCause();
	    TextLCD lcd = BrickFinder.getDefault().getTextLCD(Font.getSmallFont());
		//t.printStackTrace();
		lcd.clear();
		lcd.drawString("Uncaught exception:", 0, 0);
		lcd.drawString(t.getClass().getName(), 0, 2);
		lcd.drawString(t.getMessage(), 0, 3);		
		
		if (t.getCause() != null) {
			lcd.drawString("Caused by:", 0, 5);
			lcd.drawString(t.getCause().toString(), 0, 6);
		}
		
		StackTraceElement[] trace = t.getStackTrace();
		for(int i=0;i<7 && i < trace.length ;i++) lcd.drawString(trace[i].toString(), 0, 8+i);
		
		lcd.refresh();
		
		Button.ESCAPE.waitForPressAndRelease();
	}
	
	static class RedirectStream extends PrintStream {
		PrintStream orig, lcd;
		
		public RedirectStream(PrintStream orig, OutputStream os) {
			super(os);
			this.orig = orig;
		}
		
		@Override
		public void write(int x) {
			super.write(x);
			orig.write(x);
		}
		
		@Override
		public void write(byte[] b, int o, int l) {
			super.write(b,o,l);
			orig.write(b,o,l);
		}
		
		@Override
		public void close() {
			super.close();
			orig.close();
		}
		
		@Override
		public void flush() {
			super.flush();
			orig.flush();
		}
	}
}
