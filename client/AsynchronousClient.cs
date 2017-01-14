using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Collections.Generic;

namespace client
{
    // State object for receiving data from remote device.
    public class StateObject
    {
        // Client socket.
        public Socket workSocket = null;
        // Size of receive buffer.
        public const int BufferSize = 256;
        // Receive buffer.
        public byte[] buffer = new byte[BufferSize];
        // Received data string.
        public StringBuilder sb = new StringBuilder();
        public int ImageSize;
    }

    public class AsynchronousClient
    {
        // The port number for the remote device.
        private int port;
        private string IP;
        private Socket client;
        private static byte[] _imageBuff;
        private static int _totBytesRead = 0;

        // ManualResetEvent instances signal completion.
        private static ManualResetEvent connectDone =
            new ManualResetEvent(false);
        private static ManualResetEvent sendDone =
            new ManualResetEvent(false);
        private static ManualResetEvent receiveDone =
            new ManualResetEvent(false);

        // The response from the remote device.
        private List<byte> response = new List<byte>();

        public AsynchronousClient(int port, string IP)
        {
            this.port = port;
            this.IP = IP;
        }

        public void StartClient(string resolution)
        {
            try
            {
                IPHostEntry ipHostInfo = Dns.GetHostEntry(IP);

                IPAddress ipAddress = ipHostInfo.AddressList[0];
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);

                client = new Socket(ipAddress.AddressFamily,
                    SocketType.Stream, ProtocolType.Tcp);

                client.BeginConnect(remoteEP,
                    new AsyncCallback(ConnectCallback), client);
                connectDone.WaitOne();

                Send(client, resolution);
                sendDone.WaitOne();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        private void ConnectCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.
                Socket client = (Socket)ar.AsyncState;

                // Complete the connection.
                client.EndConnect(ar);

                Console.WriteLine("Socket connected to {0}",
                    client.RemoteEndPoint.ToString());

                // Signal that the connection has been made.
                connectDone.Set();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        private void Receive(Socket client)
        {
            try
            {
                // Create the state object.
                StateObject state = new StateObject();
                state.workSocket = client;

                // Begin receiving the data from the remote device.
                client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
                    new AsyncCallback(ReceiveHeaderCallback), state);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        public void ReceiveHeaderCallback(IAsyncResult ar)
        {
            // Retrieve the state object and the handler socket
            // from the asynchronous state object.
            StateObject state = (StateObject)ar.AsyncState;
            Socket handler = state.workSocket;

            // Read data from the client socket. 
            int bytesRead = handler.EndReceive(ar);

            //we need to add error handler here...but later
            state.ImageSize = BitConverter.ToInt32(state.buffer, 0);
            _imageBuff = new byte[state.ImageSize];
            _totBytesRead = 0;

            Send(client, "OK");

            handler.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
               new AsyncCallback(ReceiveCallback), state);
        }

        private void ReceiveCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the state object and the client socket 
                // from the asynchronous state object.
                StateObject state = (StateObject)ar.AsyncState;
                Socket client = state.workSocket;

                // Read data from the remote device.
                int bytesRead = client.EndReceive(ar);
                Console.WriteLine("Bytes read: " + bytesRead);

                if (bytesRead > 0)
                {
                    // There might be more data, so store the data received so far.
                    byte[] tempArray = new byte[bytesRead];
                    Array.Copy(state.buffer, tempArray, bytesRead);
                    response.AddRange(tempArray);

                    _totBytesRead += bytesRead;

                    if (_totBytesRead < state.ImageSize)
                    {
                        // Get the rest of the data.
                        client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
                        new AsyncCallback(ReceiveCallback), state);
                    }
                    else
                        receiveDone.Set();
                }
                else
                {
                    // Signal that all bytes have been received.
                    receiveDone.Set();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        private void Send(Socket client, string data)
        {
            var length = BitConverter.GetBytes(Encoding.ASCII.GetByteCount(data));
            // Convert the string data to byte data using ASCII encoding.
            byte[] byteData = Encoding.ASCII.GetBytes(data);

            // Send length
            client.BeginSend(BitConverter.GetBytes(Encoding.ASCII.GetByteCount(data)), 0, length.Length, 0,
                new AsyncCallback(SendCallback), client);

            // Begin sending the data to the remote device.
            client.BeginSend(byteData, 0, byteData.Length, 0,
                new AsyncCallback(SendCallback), client);
        }

        private void SendCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.
                Socket client = (Socket)ar.AsyncState;

                // Complete sending the data to the remote device.
                int bytesSent = client.EndSend(ar);
                Console.WriteLine("Sent {0} bytes to server.", bytesSent);

                // Signal that all bytes have been sent.
                sendDone.Set();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        public byte[] Receive()
        {
            Receive(client);
            receiveDone.WaitOne();

            Send(client, "OK");
            return response.ToArray();
        }

        public void Disconnect()
        {
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }
    }
}