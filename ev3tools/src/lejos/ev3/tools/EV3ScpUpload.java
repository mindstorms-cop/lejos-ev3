package lejos.ev3.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.ParseException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class EV3ScpUpload {
	
	private static final String JAVA_RUN_JAR = "cd /home/lejos/programs;jrun -jar ";
	private static final String JAVA_DEBUG_JAR = "cd /home/lejos/programs;jrun -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y -jar ";

	public static void main(String[] args) {
		ToolStarter.startTool(EV3ScpUpload.class, args);
	}

	public static int start(String[] args) throws IOException {
		return new EV3ScpUpload().run(args);
	}

	private int run(String[] args) throws IOException {
		ScpUploadCommandLineParser fParser = new ScpUploadCommandLineParser(
				EV3ScpUpload.class, "[options] pc-file ev3-file");
		try {
			fParser.parse(args);
		} catch (ParseException e) {
			fParser.printHelp(System.err, e);
			return 1;
		}

		if (fParser.isHelp()) {
			fParser.printHelp(System.out);
			return 0;
		}

		String host = fParser.getHost();
		String to = fParser.getTo();
		String from = fParser.getFrom();
		boolean run = fParser.isRun();
		boolean debug = fParser.isDebug();

		System.out.println("Copying to host " + host + " from " + from + " to "
				+ to + " run = " + run + " and debug = " + debug);

		JSch jsch = new JSch();
		try {
			Session session = jsch.getSession("root", host, 22);

			session.setPassword("");
			UserInfo ui = new DummyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			String command = "scp -t " + to;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				System.err.println("scp command failed");
				System.exit(1);
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = new File(from).length();
			command = "C0644 " + filesize + " ";
			if (from.lastIndexOf('/') > 0) {
				command += from.substring(from.lastIndexOf('/') + 1);
			} else {
				command += from;
			}

			command += "\n";
			out.write(command.getBytes());
			out.flush();

			if (checkAck(in) != 0) {
				System.err.println("C0644 failed");
				System.exit(1);
			}

			// send contents of local file
			FileInputStream fis = new FileInputStream(from);
			byte[] buf = new byte[1024];

			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				//System.out.println("Sending " + len + " bytes");
				out.write(buf, 0, len); // out.flush();
			}

			fis.close();
			fis = null;

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			if (checkAck(in) != 0) {
				System.err.println("send contents failed");
				System.exit(1);
			}
			out.close();
			channel.disconnect();

			System.out.println("Copied OK");

			if (run || debug) {
				command = (debug ? JAVA_DEBUG_JAR : JAVA_RUN_JAR) + to;

				System.out.println("Running program: " + command);

				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);

				out = channel.getOutputStream();
				in = channel.getInputStream();

				channel.connect();

				byte[] tmp = new byte[1024];

				while (true) {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						if (i < 0) break;
						System.out.print(new String(tmp, 0, i));
					}

					if (channel.isClosed()) {
						System.out.println("exit-status: " + channel.getExitStatus());
						break;
					}

					try {
						Thread.sleep(1000);
					} catch (Exception ee) {
					}
				}

				System.out.println("Run finished");
			}

			channel.disconnect();
			session.disconnect();

			System.exit(0);

		} catch (JSchException e) {
			System.err.println("Failed to upload or run jar file: " + e);
			return 1;
		}

		return 0;
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b <= 0) return b;
		else {
			StringBuilder sb = new StringBuilder();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			
			System.out.print(sb.toString());
			return b;
		}
	}
}
