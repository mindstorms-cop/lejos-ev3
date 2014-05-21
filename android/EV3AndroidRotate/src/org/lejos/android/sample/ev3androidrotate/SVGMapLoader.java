package org.lejos.android.sample.ev3androidrotate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lejos.robotics.geometry.Line;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class SVGMapLoader {
	public List<Line> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readLines(parser);
		} finally {
			in.close();
		}
	}

	private List<Line> readLines(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		List<Line> entries = new ArrayList<Line>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) continue;
			if (parser.getName().equals("line")) entries.add(readLine(parser));
		}
		return entries;
	}
	
	private Line readLine(XmlPullParser parser) {
		return new Line(Integer.parseInt(parser.getAttributeValue(null, "x1")), 
				Integer.parseInt(parser.getAttributeValue(null, "y1")),
				Integer.parseInt(parser.getAttributeValue(null, "x2")),
				Integer.parseInt(parser.getAttributeValue(null, "y2")));
	}
}
