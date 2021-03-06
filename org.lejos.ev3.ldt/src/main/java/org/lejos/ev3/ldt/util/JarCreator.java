package org.lejos.ev3.ldt.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarCreator {
	private String inputDirectory;
	private String outputFile;
	private  String mainClass;
	private boolean debug = false;
	private ArrayList<String> libs;
	
	private String classPath = "/home/root/lejos/lib/ev3classes.jar /home/root/lejos/lib/opencv-300.jar /home/root/lejos/lib/dbusjava.jar /home/root/lejos/libjna/usr/share/java/jna.jar";
	
	public JarCreator(String inputDirectory, String outputFile, String mainClass, ArrayList<String> libs) {
		this.inputDirectory = inputDirectory.replace("\\", "/");
		this.outputFile = outputFile;
		this.mainClass = mainClass;
		this.libs = libs;
		if (debug) LeJOSEV3Util.message("Input Directory is " + this.inputDirectory);
	}

	public void run() throws IOException {
	  Manifest manifest = new Manifest();
	  Attributes attributes = manifest.getMainAttributes();
	  attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
	  attributes.put(Attributes.Name.MAIN_CLASS, mainClass);
	  
	  for (String path: libs) {
		  File f = new File(path);
		  classPath += " /home/lejos/lib/" + f.getName();
	  }

	  
	  if (debug) LeJOSEV3Util.message("Classpath is " + classPath);
	  
	  attributes.put(Attributes.Name.CLASS_PATH, classPath);
	  JarOutputStream target = new JarOutputStream(new FileOutputStream(outputFile), manifest);
	  add(new File(inputDirectory), target);
	  target.close();
	}

	private void add(File source, JarOutputStream target) throws IOException {
	  BufferedInputStream in = null;
	  try {
	    if (source.isDirectory()) {
	      String name = source.getPath().replace("\\", "/").replace(inputDirectory,"");
	      if (!name.isEmpty()) {
	        if (!name.endsWith("/")) name += "/";
	        if (debug) LeJOSEV3Util.message("Adding directory " + name);
	        JarEntry entry = new JarEntry(name);
	        entry.setTime(source.lastModified());
	        target.putNextEntry(entry);
	        target.closeEntry();
	      }
	      
	      for (File nestedFile: source.listFiles()) {
	    	if (debug) LeJOSEV3Util.message("Adding " + nestedFile.getAbsolutePath());
	        add(nestedFile, target);
	      }
	      return;
	    }
	    
	    JarEntry entry = new JarEntry(source.getPath().replace("\\", "/").replace(inputDirectory + "/",""));
	    entry.setTime(source.lastModified());
	    if (debug) LeJOSEV3Util.message("Putting entry " + entry.getName());
	    target.putNextEntry(entry);
	    in = new BufferedInputStream(new FileInputStream(source));

	    byte[] buffer = new byte[1024];
	    while (true) {
	      int count = in.read(buffer);
	      if (count == -1) break;
	      target.write(buffer, 0, count);
	    }
	    target.closeEntry();
	  }
	  finally {
	    if (in != null) in.close();
	  }
	}
}
