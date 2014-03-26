package lejos.ev3.tools;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

public class EV3SDCard extends JFrame {
	private static final long serialVersionUID = 2112749235155851987L;
	private static String[] drives = new String[0];
	private static File[] roots = new File[0];
	private URI uri;
	private File zipFile, jreFile;
	private JLabel cardDescription = new JLabel();

	public int run () {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("EV3 SD Card creator");
		setPreferredSize(new Dimension(500, 300));
		
		getCandidateDrives();

		// Drive panel
		JPanel drivePanel = new JPanel();
		JLabel driveLabel = new JLabel("Select SD drive: ");
		final JComboBox<String> driveDropdown = new JComboBox<String>(drives);
		drivePanel.add(driveLabel);
		drivePanel.add(driveDropdown);
		drivePanel.setBorder(BorderFactory.createEtchedBorder());
		getContentPane().add(drivePanel, BorderLayout.PAGE_START);

		// Files panel
		JPanel filesPanel = new JPanel();
		JLabel instructions = new JLabel(
				"Select the SD card image zip file from your leJOS EV3 installation");
		filesPanel.add(instructions);
		final JTextField zipFileName = new JTextField(32);
		JButton zipButton = new JButton("Zip file");
		filesPanel.add(zipFileName);
		filesPanel.add(zipButton);

		final JFileChooser zipChooser = new JFileChooser(System.getProperty("user.home") + "/Downloads");
		
		zipChooser.setAcceptAllFileFilterUsed(false);
		
		zipChooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				
				if (f.isDirectory()) return true;
				String extension = getExtension(f);
				return extension != null && extension.equals("zip");
			}

			@Override
			public String getDescription() {
				return "zip";
			}					
		});
		
		final JFileChooser gzChooser = new JFileChooser(System.getProperty("user.home") + "/Downloads");
		
		gzChooser.setAcceptAllFileFilterUsed(false);
		
		gzChooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				
				if (f.isDirectory()) return true;
				String extension = getExtension(f);
				return extension != null && extension.equals("gz");
			}

			@Override
			public String getDescription() {
				return "gz files";
			}					
		});

		JLabel instructions2 = new JLabel(
				"Download the EV3 Oracle JRE and the select the latest ejre .gz file");
		filesPanel.add(instructions2);
		final JTextField jreFileName = new JTextField(32);
		JButton jreButton = new JButton("JRE");
		filesPanel.add(jreFileName);
		filesPanel.add(jreButton);

		try {
			uri = new URI("http://java.com/legomindstorms");
		} catch (URISyntaxException e1) {
		}

		JButton getJREButton = new JButton();
		getJREButton
				.setText("<html>Click the <font color=\"#000099\"><u>link</u></font>"
						+ " to download the EV3 Oracle JRE.</html>");

		getJREButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (uri != null)
					open(uri);
			}
		});

		filesPanel.add(getJREButton);

		filesPanel.setBorder(BorderFactory.createEtchedBorder());
		getContentPane().add(filesPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		
		JButton createButton = new JButton("Create");
		
		JButton refreshButton = new JButton("Refresh");
		
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCandidateDrives();
				driveDropdown.removeAllItems();
				for(String drive: drives) {
					driveDropdown.addItem(drive);
				}
			}		
		});
		
		JButton exitButton = new JButton("Exit");
		buttonPanel.add(cardDescription);
		buttonPanel.add(createButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(exitButton);
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		driveDropdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (driveDropdown.getSelectedIndex() < 0) return;
				cardDescription.setText(drives[driveDropdown.getSelectedIndex()]);
			}
		});

		zipButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int r = zipChooser.showOpenDialog(EV3SDCard.this);
				
				if (r == JFileChooser.APPROVE_OPTION) {
					zipFile = zipChooser.getSelectedFile();
					zipFileName.setText(zipFile.getPath());
				} else {
					showMessage("Not a valid lejos SD image zip file");
				}
			}
		});

		jreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = gzChooser.showOpenDialog(EV3SDCard.this);
				if (r == JFileChooser.APPROVE_OPTION) {
					jreFile = gzChooser.getSelectedFile();
					jreFileName.setText(jreFile.getPath());
				}
			}
		});
		
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (drives.length == 0) {
					showMessage("No SD drive selected");
					return;
				}
				
				File r = roots[driveDropdown.getSelectedIndex()];
				long space =  r.getTotalSpace() / (1024*1024);
				File[] files = r.listFiles();
				
				System.out.println("Directory is " + r.getPath());
				System.out.println("Space on drive is " + space  + "Mb");
				
				if (space < 400) {
					showMessage("Insufficient space on drive");
					return;
				} else if (files.length > 0) {
					showMessage("Drive is not empty");
					return;
				} else if (zipFile == null || !zipFile.exists()) {
					showMessage("Zip file not selected");
					return;
				} else if (jreFile == null || !jreFile.exists()) {
					showMessage("JRE file not selected");
					return;
				}
				
				// Unzip the leJos image to the drive
				System.out.println("Unzipping " + zipFile.getPath() + " to " + r.getPath());
				unZip(zipFile.getPath(), r.getPath());
				
				// Copy the Oracle JRE
				System.out.println("Copying " + jreFile.getPath() + " to " + r.getPath());
				try {
					copyFile(jreFile, new File(r.getPath() + jreFile.getName()));
				} catch (IOException e1) {
					showMessage("Failed to copy the Oracle JRE: " + e1);
				}
				
				showMessage("SD card created. Now safely eject it.");
			}			
		});

		pack();
		setVisible(true);
		
		return 0;
	}
	
	private void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
	
	private void getCandidateDrives() {
		File[] roots = File.listRoots();
		ArrayList<String> driveList = new ArrayList<String>();
		ArrayList<File> rootList = new ArrayList<File>();
		for (File r : roots) {
			String s1 = FileSystemView.getFileSystemView()
					.getSystemDisplayName(r);
			String s2 = FileSystemView.getFileSystemView()
					.getSystemTypeDescription(r);
			if (!r.getPath().equals("C:\\") && r.getTotalSpace() > 0) {
				rootList.add(r);
				System.out.print("Name : " + s1);
				System.out.print(" , Description : " + s2);
				System.out.println(", Size : " + r.getTotalSpace()
						/ (1024 * 1024) + "Mb");
				driveList.add(s1);
			}
		}
		if (driveList.size() == 0) {
			System.out.println("No SD card found");
			drives = new String[0]; 
			EV3SDCard.roots = new File[0];
			cardDescription.setText("No card selected");
		} else {
			drives = driveList.toArray(new String[0]);
			EV3SDCard.roots = rootList.toArray(new File[0]);
			cardDescription.setText(drives[0]);
		}
	}
	
	/**
	 * Command line entry point
	 */
	public static void main(String args[])
	{
		ToolStarter.startSwingTool(EV3SDCard.class, args);
	}
	
	public static int start(String[] args)
	{
		return new EV3SDCard().run();
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				System.err.println("IOException getting desktop");
			}
		} else {
			System.err.println("Desktop not supported");
		}
	}

	/**
	 * Unzip
	 * 
	 * @param zipFile
	 *            input zip file
	 * @param output
	 *            zip file output folder
	 */
	public void unZip(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];

		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze = zis.getNextEntry();
			int len;

			while (ze != null) {
				if (!ze.isDirectory()) {
					String fileName = ze.getName();
					File newFile = new File(outputFolder + File.separator + fileName);
					System.out.println("Unzipping " + fileName + " to : " + newFile.getAbsoluteFile());
	
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
	
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();	
				}
				
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			System.err.println("Error unzipping files: " + ex);
		}
	}
	
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}
