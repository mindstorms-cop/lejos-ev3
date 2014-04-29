package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.TextLCD;

public class RemoteRequestTextLCD implements TextLCD {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private Font font;

	public RemoteRequestTextLCD(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}

	public RemoteRequestTextLCD(ObjectInputStream is, ObjectOutputStream os,
			Font font) {
		this.is = is;
		this.os = os;
		this.font = font;
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
	public void drawChar(char c, int x, int y) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_DRAW_CHAR;
		req.ch = c;
		req.intValue = x;
		req.intValue2 = y;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawString(String str, int x, int y, boolean inverted) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_DRAW_STRING_INVERTED;
		req.str = str;
		req.intValue = x;
		req.intValue2 = y;
		req.flag = inverted;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawString(String str, int x, int y) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_DRAW_STRING;
		req.str = str;
		req.intValue = x;
		req.intValue2 = y;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawInt(int i, int x, int y) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_DRAW_INT;
		req.intValue = i;
		req.intValue2 = x;
		req.intValue3 = y;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawInt(int i, int places, int x, int y) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_DRAW_INT_PLACES;
		req.intValue = i;
		req.intValue2 = places;
		req.intValue3 = x;
		req.intValue4 = y;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear(int x, int y, int n) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_CLEAR_LINES;
		req.intValue = x;
		req.intValue2 = y;
		req.intValue3 = n;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear(int y) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_CLEAR_LINE;
		req.intValue = y;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void scroll() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_SCROLL;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Font getFont() {
		return null;
	}

	@Override
	public int getTextWidth() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_GET_TEXT_WIDTH;
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
	public int getTextHeight() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LCD_GET_TEXT_HEIGHT;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}
}
