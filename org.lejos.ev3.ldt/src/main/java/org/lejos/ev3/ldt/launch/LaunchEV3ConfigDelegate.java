package org.lejos.ev3.ldt.launch;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Naming;

import lejos.remote.ev3.RMIMenu;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.lejos.ev3.ldt.LeJOSEV3Plugin;
import org.lejos.ev3.ldt.util.BrickInfo;
import org.lejos.ev3.ldt.util.Discover;
import org.lejos.ev3.ldt.util.JarCreator;
import org.lejos.ev3.ldt.util.LeJOSEV3Util;
import org.lejos.ev3.ldt.util.PrefsResolver;

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

	public void launch(ILaunchConfiguration config, String mode,	ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		monitor.beginTask("Launching "+config.getName()+"...", 3); //$NON-NLS-1$
		
		PrefsResolver p = new PrefsResolver(LeJOSEV3Plugin.ID, null);
		
		boolean run = resolve(p, config, "", mode+LaunchConstants.SUFFIX_RUN_AFTER_UPLOAD, true);
		
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
			    
			    if (run) menu.runProgram(binary.getProjectRelativePath().toPortableString().replace(".jar", ""));
				LeJOSEV3Util.message("Program has been uploaded");
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
