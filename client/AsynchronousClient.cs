using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Collections.Generic;

namespace client
{
    public class SynchronousClient
    {
        // The port number for the remote device.
        private int port;
        private string IP;
        private Socket client;

        // The response from the remote device.
        private List<byte> response = new List<byte>();

        public SynchronousClient(int port, string IP)
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

                client.Connect(remoteEP);

                // length
                client.Send(BitConverter.GetBytes(Encoding.ASCII.GetByteCount(resolution)));
                // data
                client.Send(Encoding.ASCII.GetBytes(resolution));
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        public byte[] Receive()
        {
            // length
            byte[] lengthByte = new byte[4];
            client.Receive(lengthByte);
            int length = BitConverter.ToInt32(lengthByte, 0);

            // send ok
            // length
            client.Send(BitConverter.GetBytes(Encoding.ASCII.GetByteCount("OK")));
            // data
            client.Send(Encoding.ASCII.GetBytes("OK"));

            // data
            byte[] data = new byte[length];
            client.Receive(data);

            // send ok
            client.Send(BitConverter.GetBytes(Encoding.ASCII.GetByteCount("OK")));
            client.Send(Encoding.ASCII.GetBytes("OK"));

            return data;
        }

        public void Disconnect()
        {
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }
    }
}