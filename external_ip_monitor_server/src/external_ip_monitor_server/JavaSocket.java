package external_ip_monitor_server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaSocket {
	private ServerSocket clientSocket;
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	public JavaSocket(int port) throws IOException{
		clientSocket = new ServerSocket(port, 10);
	}
	
	public void connect() throws IOException{
		socket = clientSocket.accept();
		
		is = socket.getInputStream();
        os = socket.getOutputStream();
	}
	
	public String getRemoteResolution() throws IOException{
        byte[] lenBytes = new byte[4];
        is.read(lenBytes, 0, 4);
        int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
        byte[] receivedBytes = new byte[len];
        is.read(receivedBytes, 0, len);
        String received = new String(receivedBytes, 0, len);
        
        //System.out.println("Received: " + received);
        
        return received;
	}
	
	public void send(byte[] data) throws IOException{
      int toSendLen = data.length;
      byte[] toSendLenBytes = new byte[4];
      
      toSendLenBytes[0] = (byte)(toSendLen & 0xff);
      toSendLenBytes[1] = (byte)((toSendLen >> 8) & 0xff);
      toSendLenBytes[2] = (byte)((toSendLen >> 16) & 0xff);
      toSendLenBytes[3] = (byte)((toSendLen >> 24) & 0xff);
      
      //System.out.println("Sent size");
      os.write(toSendLenBytes);
      //getRemoteResolution();
      //System.out.println("Sent data: " + data.length);
      os.write(data);
      //getRemoteResolution(); 
	}
	
	public void disconnectClient(){
		try {
			socket.close();
			is.close();
			os.close();
		} catch (IOException e) {
			System.out.println("Error while disconnecting.");
		}
	}
	
	public void disconnectSocket(){
		try {
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Error while disconnecting.");
		}
	}
}
