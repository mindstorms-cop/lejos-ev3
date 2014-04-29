package lejos.remote.ev3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.TextLCD;

public class RemoteRequestTextLCD implements TextLCD {

	public RemoteRequestTextLCD(ObjectInputStream is, ObjectOutputStream os) {
		// TODO Auto-generated constructor stub
	}

	public RemoteRequestTextLCD(ObjectInputStream is, ObjectOutputStream os,
			Font f) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getHWDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContrast(int contrast) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bitBlt(byte[] src, int sw, int sh, int sx, int sy, int dx,
			int dy, int w, int h, int rop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bitBlt(byte[] src, int sw, int sh, int sx, int sy, byte[] dst,
			int dw, int dh, int dx, int dy, int w, int h, int rop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAutoRefresh(boolean on) {
		// TODO Auto-generated method stub

	}

	@Override
	public int setAutoRefreshPeriod(int period) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void drawChar(char c, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, int x, int y, boolean inverted) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawInt(int i, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawInt(int i, int places, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(int x, int y, int n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scroll() {
		// TODO Auto-generated method stub

	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTextWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
