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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MainWindow {

	private JFrame frame;
	private JTextField txtPort;

	private int port;
	private double resolutionWidth;
	private double resolutionHeight;
	private double remoteResolutionHeight;
	private double remoteResolutionWidth;
	private double fullResolutionHeight;
	private double fullResolutionWidth;
	private boolean interrupt = false;
	private final String SSNAME = "ss.jpg";

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(3, 2, 0, 0));

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				port = Integer.parseInt(txtPort.getText());

				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				resolutionWidth = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds().getWidth();
				resolutionHeight = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds().getHeight();

				try {
					JavaSocket.connect(port);

					String remoteResolution = JavaSocket.getRemoteResolution();
					String[] remoteWidthHeight = remoteResolution.split("x");

					if (remoteWidthHeight.length == 2) {
						remoteResolutionWidth = Double.parseDouble(remoteWidthHeight[0]);
						remoteResolutionHeight = Double.parseDouble(remoteWidthHeight[1]);
					} else {
						JOptionPane.showMessageDialog(null, "Error: " + "wrong resolution format from remote device.");
						JavaSocket.disconnect();
						return;
					}

					calculateSetExtendedResolution();

					Runnable myRunnable = new Runnable() {

						public void run() {
							while (true) {
								try {
									if (interrupt) {
										resetResolution();
										JavaSocket.disconnect();
										break;
									}

									byte[] screenshot = generateScreenshot();

									JavaSocket.send(screenshot);
									JavaSocket.getRemoteResolution();
									
									Thread.sleep(70);
								} catch (IOException | InterruptedException e) {
									System.out.println("Error: " + e.getMessage());
								}
							}
						};
					};

					Thread thread = new Thread(myRunnable);
					thread.start();

				} catch (IOException exception) {
					JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
					JavaSocket.disconnect();
					return;
				}
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
		frame.getContentPane().add(btnConnect);

		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				interrupt = true;
			}
		});
		frame.getContentPane().add(btnDisconnect);
	}

	private void calculateSetExtendedResolution() throws IOException {
		fullResolutionHeight = resolutionHeight;
		fullResolutionWidth = resolutionWidth + remoteResolutionWidth;

		// xrandr --fb 3840x1080 --output VGA-1 --panning 3840x1080+0+0
		String[] xrandr = new String[] { "xrandr", "--fb " + fullResolutionWidth + "x" + fullResolutionHeight,
				"--output VGA-1", "--panning " + fullResolutionWidth + "x" + fullResolutionHeight + "+0+0" };
		new ProcessBuilder(xrandr).start();

		// xrandr --fb 3840x1080 --output VGA-1 --panning 1920x1080+0+0
		xrandr = new String[] { "xrandr", "--fb " + fullResolutionWidth + "x" + fullResolutionHeight, "--output VGA-1",
				"--panning " + resolutionWidth + "x" + resolutionHeight + "+0+0" };
		new ProcessBuilder(xrandr).start();
	}

	private void resetResolution() throws IOException {
		// xrandr -s 1920x1080
		String[] xrandr = new String[] { "xrandr", "-s " + resolutionWidth + "x" + resolutionHeight };
		new ProcessBuilder(xrandr).start();
	}

	private byte[] generateScreenshot() throws IOException {
		String[] shutter = new String[] { "shutter", "--select=100,100,500,500", "--output=" + SSNAME,
				"--include_cursor", "--exit_after_capture", "--no_session" };

		new ProcessBuilder(shutter).start();
		
		return Files.readAllBytes(new File(SSNAME).toPath());
	}
}
