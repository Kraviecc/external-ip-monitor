package external_ip_monitor_server;

import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class MainWindow {

	private JFrame frame;
	private final JTextField txtAddress = new JTextField();
	private JTextField txtPort;
	
	private String IP;
	private int port;
	private double resolutionWidth;
	private double resolutionHeight;

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
		
		JLabel lblAddres = new JLabel("IP:");
		lblAddres.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblAddres);
		txtAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtAddress.setText("127.0.0.1");
		frame.getContentPane().add(txtAddress);
		txtAddress.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				IP = txtAddress.getText();
				port = Integer.parseInt(txtPort.getText());
				
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				resolutionWidth = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds().getWidth();
				resolutionHeight = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds().getHeight();
				
				try {
				JavaSocket.connect(port, IP);
				}
				catch (IOException exception) {
					JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
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
				
			}
		});
		frame.getContentPane().add(btnDisconnect);
	}

}
