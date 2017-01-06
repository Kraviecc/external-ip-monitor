package external_ip_monitor_server;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		//get ip of second monitor
		//get screen resolution
		//xrandr --fb 3840x1080 --output VGA-1 --panning 3840x1080+0+0
		//xrandr --fb 3840x1080 --output VGA-1 --panning 1920x1080+0+0
		//get screen resolution
		//subtract resolutions
		//loop: shutter
		//loop: send screenshot
		//go back with resolution: xrandr -s 1920x1080 (resolution here)
		System.out.println("Started");
		String[] shutter = new String[] {"shutter", "--select=100,100,500,500",
										 "--output=ss.jpg", "--include_cursor", "--exit_after_capture",
										 "--no_session"};
		try {
			new ProcessBuilder(shutter).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		double width = gd.getDefaultConfiguration().getBounds().getWidth();
		double height = gd.getDefaultConfiguration().getBounds().getHeight();
		
		double maxWidth = gd.getDefaultConfiguration().getBounds().getMaxX();
		double maxHeight = gd.getDefaultConfiguration().getBounds().getMaxY();
	}

}
