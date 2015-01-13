package lejos.ev3.newstartup;

import lejos.ev3.newstartup.gui.Submenu;
import lejos.hardware.lcd.Image;

public class SystemMenu extends Submenu {
	
	static final String ICEV3 = "\u00c0\u00ff\u00ff\u0003\u0040\u0000\u0000\u0002\u0060\u00ff\u00ff\u0006\u0060\u0001\u0080\u0006\u0060\u0001\u0080\u0006\u0060\u0001\u0080\u0006\u0060\u0003\u0080\u0006\u0060\u0001\u0080\u0006\u0060\u0003\u0080\u0006\u0060\u0005\u0080\u0006\u0060\u000b\u0080\u0006\u0060\u0055\u0080\u0006\u0060\u00ff\u00ff\u0006\u0060\u0000\u0000\u0006\u00e0\u00ff\u00ff\u0007\u00a0\u000f\u0000\u0004\u00a0\u0087\u0001\u0004\u0020\u00c0\u0003\u0004\u0020\u00e0\u0007\u0004\u0020\u0040\u0002\u0004\u0020\u00bc\u003d\u0004\u0020\u00bc\u003d\u0004\u0020\u0040\u0002\u0004\u0060\u00e0\u0007\u0004\u00a0\u00c0\u0003\u0004\u0060\u0080\u0001\u0004\u00a0\u0002\u0000\u0004\u0060\u0015\u0000\u0006\u00c0\u00ff\u00ff\u0003\u00c0\u00ea\u00bf\u0003\u00c0\u00f5\u007f\u0003\u00c0\u00ff\u00ff\u0003";

	@Override
	public String getTitle() {
		return "System";
	}

	@Override
	public Image getIcon() {
		return new Image(32, 32, Utils.stringToBytes8( ICEV3 ) );
	}

	@Override
	public String[] getDetails() {
		return new String[] {
			"Menu Version: " + Utils.versionToString(Config.VERSION)
		};
	}

	@Override
	public Image[] getItems() {
		return null;
	}

}
