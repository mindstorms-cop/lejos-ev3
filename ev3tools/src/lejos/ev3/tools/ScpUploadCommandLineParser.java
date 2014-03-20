package lejos.ev3.tools;

import java.io.File;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class ScpUploadCommandLineParser extends AbstractCommandLineParser
{
	private String from;
	private String to;
	private String host;
	private boolean run;
	private boolean help;
	private boolean debug;
	
	public ScpUploadCommandLineParser(Class<?> caller, String params)
	{
		super(caller, params);
		
		options.addOption("h", "help", false, "help");

		Option runOption = new Option("r", "run", false, "run program after upload");
		runOption.setArgName("run");
		options.addOption(runOption);
		
		Option debugOption = new Option("d", "debug", false, "run program in debug mode after upload");
		debugOption.setArgName("debug");
		options.addOption(debugOption);

		Option nameOption = new Option("n", "name", true, "connect to EV3 with this hostname");
		nameOption.setArgName("name");
		options.addOption(nameOption);
	}

	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws ParseException
	 */
	public void parse(String[] args) throws ParseException
	{
		result = new GnuParser().parse(options, args);
		String[] files = result.getArgs();

		this.help = result.hasOption("h");
		if (this.help)
			return;

		this.run = result.hasOption("r");
		this.debug = result.hasOption("d");
		this.host = result.getOptionValue("n");
		
		if (host == null) 
			throw new ParseException("No host name specified");
		
		if (files.length > 2)
				throw new ParseException("Too many files");
		
		if (files.length == 1)
			throw new ParseException("Musst specify local and remote file");
		
		if (files.length == 0)
			throw new ParseException("No files specified");
		
		from = files[0];
		to = files[1];

		testFile(new File(from));
	}
	
	private void testFile(File file) throws ParseException
	{
		if (file != null && !file.exists())
			throw new ParseException("Local file does not exist: " + file);
	}

	public boolean isHelp()
	{
		return help;
	}
	
	public boolean isRun()
	{
		return run;
	}
	
	public boolean isDebug()
	{
		return debug;
	}
	
	public String getHost() {
		return host;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
}