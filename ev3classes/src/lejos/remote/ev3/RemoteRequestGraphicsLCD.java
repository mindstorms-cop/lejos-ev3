package lejos.remote.ev3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;

public class RemoteRequestGraphicsLCD implements GraphicsLCD {

	public RemoteRequestGraphicsLCD(ObjectInputStream is, ObjectOutputStream os) {
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
	public void setPixel(int x, int y, int color) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPixel(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void drawString(String str, int x, int y, int anchor,
			boolean inverted) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, int x, int y, int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawSubstring(String str, int offset, int len, int x, int y,
			int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawChar(char character, int x, int y, int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y,
			int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStrokeStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStrokeStyle(int style) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRegionRop(Image src, int sx, int sy, int w, int h, int x,
			int y, int anchor, int rop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRegionRop(Image src, int sx, int sy, int w, int h,
			int transform, int x, int y, int anchor, int rop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRegion(Image src, int sx, int sy, int w, int h,
			int transform, int x, int y, int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawImage(Image src, int x, int y, int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(int x0, int y0, int x1, int y1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRect(int x, int y, int w, int h) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyArea(int sx, int sy, int w, int h, int x, int y, int anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFont(Font f) {
		// TODO Auto-generated method stub

	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTranslateX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTranslateY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColor(int rgb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColor(int red, int green, int blue) {
		// TODO Auto-generated method stub

	}

}
