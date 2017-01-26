package external_ip_monitor_server;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MainWindow {

	private JFrame frame;
	private JTextField txtPort;
	private JTextField txtOutput;
	private JRadioButton left;
	private JRadioButton right;

	private int port;
	private Double resolutionWidth;
	private Double resolutionHeight;
	private Double remoteResolutionWidth;
	private Double fullResolutionHeight;
	private Double fullResolutionWidth;
	private boolean interrupt = false;
	private final String SSNAME = "ss.jpg";
	private JavaSocket javaSocket = null;
	private byte[] previousScreenshot = null;
	private byte[] actualScreenshot = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 250, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(4, 2, 0, 0));

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Runnable myRunnable = new Runnable() {
					public void run() {
						port = Integer.parseInt(txtPort.getText());
						GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
						resolutionWidth = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds().getWidth();
						resolutionHeight = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds()
								.getHeight();

						try {
							if (javaSocket == null)
								javaSocket = new JavaSocket(port);

							javaSocket.connect();

							String remoteResolution = javaSocket.getRemoteResolution();
							String[] remoteWidthHeight = remoteResolution.split("x");

							if (remoteWidthHeight.length == 2) {
								remoteResolutionWidth = Double.parseDouble(remoteWidthHeight[0]);
							} else {
								JOptionPane.showMessageDialog(null,
										"Error: " + "wrong resolution format from remote device.");
								javaSocket.disconnectClient();
								return;
							}

							calculateSetExtendedResolution();

						} catch (IOException exception) {
							JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
							javaSocket.disconnectClient();
							return;
						}

						while (true) {
							try {
								if (interrupt) {
									resetResolution();
									javaSocket.disconnectClient();
									return;
								}

								actualScreenshot = generateScreenshot();

								javaSocket.send(actualScreenshot);

//								if (previousScreenshot == null)
//									javaSocket.send(actualScreenshot);
//								else {
//									String toSend = computeDiff(previousScreenshot, actualScreenshot);
//
//									javaSocket.send(toSend.getBytes());
//								}
//								
//								previousScreenshot = actualScreenshot.clone();
							} catch (IOException e) {
								System.out.println("Error: " + e.getMessage());
								javaSocket.disconnectClient();
								return;
							}
						}
					};
				};

				Thread thread = new Thread(myRunnable);
				thread.start();
			}
		});

		JLabel lblPort = new JLabel("Port");
		lblPort.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblPort);

		txtPort = new JTextField();
		txtPort.setText("4343");
		txtPort.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		JLabel lblOutput = new JLabel("Output [xrandr]");
		lblOutput.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblOutput);

		txtOutput = new JTextField();
		txtOutput.setText("LVDS-1");
		txtOutput.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(txtOutput);
		txtOutput.setColumns(10);
		
		frame.getContentPane().add(btnConnect);

		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				interrupt = true;
			}
		});
		frame.getContentPane().add(btnDisconnect);

		ButtonGroup btnGroup = new ButtonGroup();
		left = new JRadioButton("left");
		left.setHorizontalAlignment(JRadioButton.RIGHT);
		right = new JRadioButton("right");
		right.setHorizontalAlignment(JRadioButton.LEFT);
		btnGroup.add(left);
		btnGroup.add(right);
		right.setSelected(true);
		frame.getContentPane().add(left);
		frame.getContentPane().add(right);
	}

	private void calculateSetExtendedResolution() throws IOException {
		fullResolutionHeight = resolutionHeight;
		fullResolutionWidth = resolutionWidth + remoteResolutionWidth;

		try {
			if (right.isSelected()) {
				String[] xrandr = new String[] { "xrandr", "--fb",
						fullResolutionWidth.intValue() + "x" + fullResolutionHeight.intValue(), "--output", txtOutput.getText(),
						"--panning", fullResolutionWidth.intValue() + "x" + fullResolutionHeight.intValue() + "+0+0" };
				ProcessBuilder pbXrandr = new ProcessBuilder(xrandr);
				Process pXrandr = pbXrandr.start();
				pXrandr.waitFor();

				Thread.sleep(300);

				xrandr = new String[] { "xrandr", "--fb",
						fullResolutionWidth.intValue() + "x" + fullResolutionHeight.intValue(), "--output", txtOutput.getText(),
						"--panning", resolutionWidth.intValue() + "x" + resolutionHeight.intValue() + "+0+0" };
				pbXrandr = new ProcessBuilder(xrandr);
				pXrandr = pbXrandr.start();
				pXrandr.waitFor();
			} else {
				String[] xrandr = new String[] { "xrandr", "--fb",
						fullResolutionWidth.intValue() + "x" + fullResolutionHeight.intValue(), "--output", txtOutput.getText(),
						"--panning", fullResolutionWidth.intValue() + "x" + fullResolutionHeight.intValue() + "+0+0" };
				ProcessBuilder pbXrandr = new ProcessBuilder(xrandr);
				Process pXrandr = pbXrandr.start();
				pXrandr.waitFor();

				Thread.sleep(300);

				xrandr = new String[] { "xrandr", "--fb",
						fullResolutionWidth.intValue() + "x" + fullResolutionHeight.intValue(), "--output", txtOutput.getText(),
						"--panning", resolutionWidth.intValue() + "x" + resolutionHeight.intValue() + "+"
								+ remoteResolutionWidth.intValue() + "+0" };
				pbXrandr = new ProcessBuilder(xrandr);
				pXrandr = pbXrandr.start();
				pXrandr.waitFor();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void resetResolution() throws IOException {
		String[] xrandr = new String[] { "xrandr", "-s",
				resolutionWidth.intValue() + "x" + resolutionHeight.intValue() };
		new ProcessBuilder(xrandr).start();
	}

	private byte[] generateScreenshot() throws IOException {
		if (right.isSelected()) {
			String[] shutter = new String[] { "maim", "--geometry=" + remoteResolutionWidth.intValue() + "x"
					+ resolutionHeight.intValue() + "+" + resolutionWidth.intValue() + "+0", SSNAME };

			try {
				ProcessBuilder pbShutter = new ProcessBuilder(shutter);
				Process pShutter = pbShutter.start();
				pShutter.waitFor();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return new byte[0];
			}
		} else {
			String[] shutter = new String[] { "maim",
					"--geometry=" + remoteResolutionWidth.intValue() + "x" + resolutionHeight.intValue() + "+0+0",
					SSNAME };

			try {
				ProcessBuilder pbShutter = new ProcessBuilder(shutter);
				Process pShutter = pbShutter.start();
				pShutter.waitFor();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return new byte[0];
			}
		}

		return Files.readAllBytes(new File(SSNAME).toPath());
	}

	private String computeDiff(byte[] previous, byte[] actual) {
		StringBuilder sb = new StringBuilder();
		int length = 0;

		if (previous.length > actual.length)
			length = actual.length;
		else
			length = previous.length;

		for (int i = 0; i < length; i++) {
			if (previous[i] != actual[i])
				sb.append(actual[i] + " " + i + "\n");
		}

		if (actual.length > previous.length) {
			for (int i = previous.length; i < actual.length; i++) {
				sb.append(actual[i] + " " + i + "\n");
			}
		}

		return sb.toString();
	}
}
