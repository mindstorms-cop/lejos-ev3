package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;

public class RemoteRequestGraphicsLCD implements GraphicsLCD {
	private ObjectInputStream is;
	private ObjectOutputStream os;

	public RemoteRequestGraphicsLCD(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public void refresh() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_REFRESH;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_CLEAR;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getWidth() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_GET_WIDTH;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int getHeight() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_GET_HEIGHT;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public byte[] getDisplay() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_GET_DISPLAY;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.contents;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public byte[] getHWDisplay() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_GET_HW_DISPLAY;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.contents;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setContrast(int contrast) {
		// Not implemented
	}

	@Override
	public void bitBlt(byte[] src, int sw, int sh, int sx, int sy, int dx,
			int dy, int w, int h, int rop) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_BITBLT_1;
		req.byteData = src;
		req.intValue = sw;
		req.intValue2 = sh;
		req.intValue3 = sx;
		req.intValue4 = sy;
		req.intValue5 = dx;
		req.intValue6 = dy;
		req.intValue7 = w;
		req.intValue8 = h;
		req.intValue9 = rop;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void bitBlt(byte[] src, int sw, int sh, int sx, int sy, byte[] dst,
			int dw, int dh, int dx, int dy, int w, int h, int rop) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_BITBLT_2;
		req.byteData = src;
		req.intValue = sw;
		req.intValue2 = sh;
		req.intValue3 = sx;
		req.intValue4 = sy;
		req.byteData2 = dst;
		req.intValue5 = dw;
		req.intValue6 = dh;
		req.intValue7 = dx;
		req.intValue8 = dy;
		req.intValue9 = w;
		req.intValue10 = h;
		req.intValue11 = rop;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAutoRefresh(boolean on) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_SET_AUTO_REFRESH;
		req.flag = on;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int setAutoRefreshPeriod(int period) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_SET_AUTO_REFRESH;
		req.intValue = period;
		try {
			os.writeObject(req);
			return 0;
		} catch (IOException e) {
			return 0;
		}
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
