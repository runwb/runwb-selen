package org.runwb.lib.selen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class Play extends JFrame {

	public static final String PAUSE = "Pause";
	public static final String RESUME = "Resume";
	public static final String STEP = "Step";
	public static final String KILL = "Kill";
	static final int XL = 150, YL = 50; 
	public AtomicBoolean debug = new AtomicBoolean(false);
	JButton run = new JButton(PAUSE);
	JButton step = new JButton(STEP);
	JButton kill = new JButton(KILL);
	Thread daemon = new Thread(){
		{
			setName(Play.class.getSimpleName() + " daemon thread");
		}
		public void run() {
			synchronized (in) {
				while (true) {
					try {
						if (paused) {
							synchronized (ex) {
								if (debug.get()) System.out.println("daemon holding");
								in.wait();
								if (debug.get()) System.out.println("daemon notified");
								ex.notify();
								if (debug.get()) System.out.println("daemon releasing");
							}
							Thread.sleep(1);
						}
						else {
							if (debug.get()) System.out.println("daemon not holding");
							in.wait();
							if (debug.get()) System.out.println("daemon notified");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		}
	};
	ActionListener action = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(run)) {
				if (run.getText().equals(PAUSE)) {
					run.setEnabled(false);
					run.setText(RESUME);
					pause();
					run.setEnabled(true);
					step.setEnabled(true);
				}
				else if (run.getText().equals(RESUME)) {
					step.setEnabled(false);
					run.setEnabled(false);
					run.setText(PAUSE);
					resume();
					run.setEnabled(true);
				}
			}
			else if (e.getSource().equals(step)) {
				run.setEnabled(false);
				step();
				run.setEnabled(true);
			}
			else if (e.getSource().equals(kill)) {
				System.exit(-1);
			}
		}
	};
	static class Ex {}
	static class In {}
	Object ex = new Ex();
	Object in = new In();
	boolean paused = false;
	public void proceed() {
		if (debug.get()) System.out.println("waiting to proceed...");
		synchronized (ex) {
			if (debug.get()) System.out.println("proceeding!");
		}
	}
	public boolean paused() {
		synchronized (in) {
			return paused;
		}
	}

	public void pause() {
		synchronized (in) {
			if (debug.get()) System.out.println("pausing issuing...");
			if (paused)
				return;
			paused = true;
			if (debug.get()) System.out.println("pausing issued");
			in.notify();
		}
	}
	public void resume() {
		synchronized (in) {
			if (debug.get()) System.out.println("resume issuing...");
			if (!paused)
				return;
			paused = false;
			if (debug.get()) System.out.println("resume issued");
			in.notify();
		}
	}
	public void step() {
		synchronized (in) {
			if (debug.get()) System.out.println("step issuing...");
			if (!paused)
				return;
			in.notify();
			if (debug.get()) System.out.println("step given");
		}
	}
	{
		Point loc = new Point(50, 50);
		File config = new File("batchControlWin.txt");
		if (config.exists())
			try {
				String[] xy = FileUtils.readFileToString(config).split(",");
				loc.x = Integer.parseInt(xy[0].trim());
				loc.y = Integer.parseInt(xy[1].trim());
			} catch (Exception e) {
			}
		
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
	    boolean visible = false;
	    for (GraphicsDevice graphicsDevice : graphicsDevices) {
	    	if (graphicsDevice.getDefaultConfiguration().getBounds().contains(loc.x, loc.y)) {
	    		visible = true;
	    		break;
	    	}
	    }		
	    if (!visible) {
	    	Rectangle bound = graphicsDevices[0].getDefaultConfiguration().getBounds();
	    	loc.x = bound.x + 50;
	    	loc.y = bound.y + 50;
	    }
		
		run.setPreferredSize(new Dimension(XL, YL));
		run.addActionListener(action);
		step.setPreferredSize(new Dimension(XL, YL));
		step.addActionListener(action);
		step.setEnabled(false);
//		kill.setPreferredSize(new Dimension(XL / 2, YL / 2));
		kill.addActionListener(action);

		setTitle("RWB Task - Batch Run");
		setResizable(false);
		JPanel buttons = new JPanel();
		add(buttons);
		buttons.add(run);
		buttons.add(step);
		add(kill, BorderLayout.NORTH);
		
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		addComponentListener(new ComponentAdapter(){
			@Override public void componentMoved(ComponentEvent e) {
				try {
					Point loc = getLocation();
					FileUtils.write(config, loc.x + ", " + loc.y);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		daemon.setDaemon(true);
		daemon.start();
		setLocation(loc);
		setVisible(true);
		setAlwaysOnTop(true);
		try {
			FileUtils.write(config, loc.x + ", " + loc.y);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Play();
	}
}
