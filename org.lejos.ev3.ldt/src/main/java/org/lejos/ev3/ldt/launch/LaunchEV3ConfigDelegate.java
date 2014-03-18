package org.lejos.ev3.ldt.launch;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lejos.remote.ev3.RMIMenu;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdi.Bootstrap;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.lejos.ev3.ldt.LeJOSEV3Plugin;
import org.lejos.ev3.ldt.preferences.PreferenceConstants;
import org.lejos.ev3.ldt.util.BrickInfo;
import org.lejos.ev3.ldt.util.Discover;
import org.lejos.ev3.ldt.util.JarCreator;
import org.lejos.ev3.ldt.util.LeJOSEV3Util;
import org.lejos.ev3.ldt.util.PrefsResolver;
import org.lejos.ev3.ldt.util.ToolStarter;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.Connector.IntegerArgument;
import com.sun.jdi.connect.Connector.StringArgument;

public class LaunchEV3ConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {
	public static final String ID_TYPE = "org.lejos.ev3.ldt.LaunchType";
	
	//TODO we should make sure, that uploads to the same EV3 are executed sequentially, not in parallel
	
	private boolean resolve(PrefsResolver p, ILaunchConfiguration config,
			String defSwitch, String suffix, boolean def) throws CoreException {
		if (config.getAttribute(defSwitch, true))
			return p.getBoolean(suffix, def);
		else
			return config.getAttribute(LaunchConstants.PREFIX+suffix, def);
	}
	
	private String resolve(PrefsResolver p, ILaunchConfiguration config,
			String defSwitch, String suffix, String def) throws CoreException {
		if (config.getAttribute(defSwitch, true))
			return p.getString(suffix, def);
		else
			return config.getAttribute(LaunchConstants.PREFIX+suffix, def);
	}

	public void launch(ILaunchConfiguration config, String mode,	ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		monitor.beginTask("Launching "+config.getName()+"...", 3); //$NON-NLS-1$
		
		PrefsResolver p = new PrefsResolver(LeJOSEV3Plugin.ID, null);
		
		boolean run = resolve(p, config, "", mode+LaunchConstants.SUFFIX_RUN_AFTER_UPLOAD, true);
		
		boolean useSsh = p.getBoolean(org.lejos.ev3.ldt.preferences.PreferenceConstants.KEY_SSH_SCP, false);
		
		if (monitor.isCanceled())
			return;
		
		try
		{
			monitor.subTask("Verifying launch configuration ..."); 
			
			String mainTypeName = this.verifyMainTypeName(config);	
			IJavaProject project = this.verifyJavaProject(config);
			
			monitor.worked(1);			
			if (monitor.isCanceled())
				return;
		
			String simpleName;
			int i = mainTypeName.lastIndexOf('.');
			if (i < 0)
				simpleName = mainTypeName;
			else
				simpleName = mainTypeName.substring(i+1);
			
			IProject project2 = project.getProject();
			IFile binary = project2.getFile(simpleName+".jar");
			String binaryPath = binary.getLocation().toOSString();
			i = binary.getLocation().toPortableString().lastIndexOf("/");
			String binDirectory = binary.getLocation().toPortableString().substring(0,i+1) + "bin";
			
			monitor.worked(1);			
			monitor.beginTask("Creating jar file and uploading " + binaryPath + " to the brick...", IProgressMonitor.UNKNOWN);
			
			LeJOSEV3Util.message("Binary path is " + binaryPath);
			LeJOSEV3Util.message("Main type name is " + mainTypeName);
			LeJOSEV3Util.message("Project relative path is " + binary.getProjectRelativePath().toPortableString());
			LeJOSEV3Util.message("Bin directory is " + binDirectory);
			
			JarCreator jc = new JarCreator(binDirectory, binaryPath, mainTypeName);
			jc.run();
			
			LeJOSEV3Util.message("Jar file has been created successfully");
				
			LeJOSEV3Util.message("Uploading ...");
			monitor.subTask("Uploading ...");	
			
			if (useSsh) {
				LeJOSEV3Util.message("Using scp for upload and ssh to execute program");
				
				String brickName = resolve(p, config, LaunchConstants.KEY_TARGET_USE_DEFAULTS,
						PreferenceConstants.KEY_TARGET_BRICK_NAME, "");
				
				// start EV3ScpUpload
				ToolStarter starter = LeJOSEV3Util.getCachedExternalStarter();
				
				ArrayList<String> args = new ArrayList<String>();
				
				if (run) args.add("-r");
				
				args.add("-n");				
				args.add(brickName);
				
				args.add(binaryPath);
				
				args.add("/home/lejos/programs/" + binary.getProjectRelativePath().toPortableString());
				
				int r = starter.invokeTool(LeJOSEV3Util.TOOL_EV3SCPUPLOAD, args);
				
				if (r == 0)
					LeJOSEV3Util.message("EV3ScpUpload has been started successfully");
				else
					LeJOSEV3Util.error("Starting EV3ScpUpload failed with exit status "+r);
			} else {
				LeJOSEV3Util.message("Using the EV3 menu for upload and to execute program");
				
				BrickInfo[] bricks = Discover.discover();
				
				if (bricks.length ==  0) {
					LeJOSEV3Util.error("No EV3 Found");					
				} else {			
					RMIMenu menu = (RMIMenu)Naming.lookup("//" + bricks[0].getIPAddress() + "/RemoteMenu");
					File f = new File(binaryPath);
					FileInputStream in = new FileInputStream(f);
					byte[] data = new byte[(int)f.length()];
				    in.read(data);
				    in.close();
				    menu.uploadFile("/home/lejos/programs/" + binary.getProjectRelativePath().toPortableString(), data);
				    
				    LeJOSEV3Util.message("Program has been uploaded");
				    
				    if (run) {
				    	if (ILaunchManager.DEBUG_MODE.equals(mode)) {
				    		LeJOSEV3Util.message("Starting program in debug mode ...");
				    		menu.debugProgram(binary.getProjectRelativePath().toPortableString().replace(".jar", ""));
				    		
				    		Thread.sleep(5000);
				    		
							LeJOSEV3Util.message("Starting debugger ...");
							monitor.subTask("Starting debugger ...");
							
							// Find the socket attach connector
							VirtualMachineManager mgr=Bootstrap.virtualMachineManager();
							
							List<?> connectors = mgr.attachingConnectors();
							
							AttachingConnector chosen=null;
							for (Iterator<?> iterator = connectors.iterator(); iterator
									.hasNext();) {
								AttachingConnector conn = (AttachingConnector) iterator.next();
								if(conn.name().contains("SocketAttach")) {
									chosen=conn;
									break;
								}
							}
							
							if(chosen == null) {
								LeJOSEV3Util.error("No suitable connector");
								menu.stopProgram();
							} else {
								Map<String, Argument> connectorArgs = chosen.defaultArguments();
								
								//for(String arg: connectorArgs.keySet()) {
								//	LeJOSEV3Util.message("arg name  is " + arg);
								//}
								Connector.IntegerArgument portArg = (IntegerArgument) connectorArgs.get("port");
								Connector.StringArgument hostArg = (StringArgument) connectorArgs.get("hostname");
								portArg.setValue(8000);
								
								//LeJOSEV3Util.message("hostArg is " + hostArg);
								hostArg.setValue(bricks[0].getIPAddress());
							
								VirtualMachine vm = chosen.attach(connectorArgs);
								LeJOSEV3Util.message("Connection established");
								
								JDIDebugModel.newDebugTarget(launch, vm, simpleName, null, true, true, true);
							}
				    	}
				    	else {
				    		LeJOSEV3Util.message("Running program ...");
				    		menu.runProgram(binary.getProjectRelativePath().toPortableString().replace(".jar", ""));
				    	}	
				    }
				}
			}
		}
		catch (Exception t)
		{
			Throwable t2 = t;
			if (t2 instanceof InvocationTargetException)
				t2 = ((InvocationTargetException)t).getTargetException();
			
			// log
			LeJOSEV3Util.error("Creating the jar file or uploading the program failed", t2);
		}
		finally
		{
			monitor.done();
		}
	}
}
