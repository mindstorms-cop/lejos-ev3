package lejos.ev3.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class ConsoleCommandLineParser extends AbstractCommandLineParser
{
	public ConsoleCommandLineParser(Class<?> caller, String params)
	{
		super(caller, params);
		
		options.addOption("h", "help", false, "help");

		Option nameOption = new Option("n", "name", true, "look for named EV3");
		nameOption.setArgName("name");
		options.addOption(nameOption);

		Option addressOption = new Option("d", "address", true, "look for EV3 with given address");
		addressOption.setArgName("address");
		options.addOption(addressOption);
	}

	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws TinyVMException
	 */
	public CommandLine parse(String[] args) throws ParseException
	{
		assert args != null : "Precondition: args != null";

		result = new GnuParser().parse(options, args);

		assert result != null : "Postconditon: result != null";
		return result;
	}
}