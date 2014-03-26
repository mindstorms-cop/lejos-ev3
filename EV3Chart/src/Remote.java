

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import lejos.ev3.tools.MapApplicationUI;
import lejos.ev3.tools.PCNavigationModel;
import lejos.robotics.mapping.NavigationModel.NavEvent;

public class Remote extends JFrame implements MapApplicationUI, MouseListener {
	private static final long serialVersionUID = 1L;
	protected PCNavigationModel model = new PCNavigationModel(this);
	JButton forward = new JButton("Forwards");
	JButton backward = new JButton("Backwards");
	JButton left = new JButton("Left");
	JButton right = new JButton("Right");
	
	public Remote() {
		model.connect("192.168.0.9");
		model.setDifferentialPilotParams(3.5f, 20.0f, 1, 2, false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("EV3Remote");
		Container buttonPanel = getContentPane();

		buttonPanel.add(forward,BorderLayout.NORTH);
		buttonPanel.add(backward,BorderLayout.SOUTH);
		buttonPanel.add(left,BorderLayout.WEST);
		buttonPanel.add(right,BorderLayout.EAST);
		
		forward.addMouseListener(this);
		backward.addMouseListener(this);
		left.addMouseListener(this);
		right.addMouseListener(this);
		
		pack();
		setVisible(true);
	}

	@Override
	public void log(String message) {
		System.out.println(message);	
	}

	@Override
	public void error(String message) {
		System.err.println(message);
	}

	@Override
	public void fatal(String message) {
		System.err.println("** FATAL ** " + message);
		System.exit(1);
	}

	@Override
	public void repaint() {
		// Nothing	
	}

	@Override
	public void eventReceived(NavEvent navEvent) {
		System.out.println("Event: " + navEvent);
	}

	@Override
	public void whenConnected() {
		System.out.println("Connected");	
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {	
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == forward) {
			log("Forwards");
			model.travel(100);
		} else if (e.getSource() == backward) {
			log("Backwards");
			model.travel(-100);
		} else if (e.getSource() == left) {
			log("left");
			model.rotate(90);
		} else if (e.getSource() == right) {
			log("right");
			model.rotate(-90);
		}	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		log("Stop");
		model.stop();
	}
	
	public static void main(String[] args) {
		new Remote();
	}
}
