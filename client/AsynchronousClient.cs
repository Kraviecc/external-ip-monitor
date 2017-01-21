using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Collections.Generic;
using System.Windows;

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
            IPAddress[] IPs = Dns.GetHostAddresses(IP);
            IPHostEntry ipHostInfo = Dns.GetHostEntry(IP);

            IPAddress ipAddress = null;

            if (ipHostInfo.AddressList.Length > 0)
                ipAddress = ipHostInfo.AddressList[0];
            else if (IPs.Length > 0)
                ipAddress = IPs[0];
            else
                throw new Exception("Nie odnaleziono serwera o adresie: " + IP);

            IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);

            client = new Socket(ipAddress.AddressFamily,
                SocketType.Stream, ProtocolType.Tcp);

            client.Connect(remoteEP);

            client.ReceiveTimeout = 10000; // 10sec

            // length
            client.Send(BitConverter.GetBytes(Encoding.ASCII.GetByteCount(resolution)));
            // data
            client.Send(Encoding.ASCII.GetBytes(resolution));
        }

        public byte[] Receive()
        {
            // length
            byte[] lengthByte = new byte[4];
            client.Receive(lengthByte, 4, SocketFlags.None);
            int length = BitConverter.ToInt32(lengthByte, 0);

            // send ok
            //client.Send(BitConverter.GetBytes(Encoding.ASCII.GetByteCount("OK")));
            //client.Send(Encoding.ASCII.GetBytes("OK"));

            // data
            byte[] data = new byte[0];
            try
            {
                int receivedBytes = 0;
                int bytesPosition = 0;
                data = new byte[length];

                while (bytesPosition != length)
                {
                    receivedBytes = client.Receive(data, bytesPosition, length - bytesPosition, SocketFlags.None);
                    bytesPosition += receivedBytes;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Receive length: " + length);
                Console.WriteLine("Receive ss: " + ex.Message);
            }
            // send ok
            client.Send(BitConverter.GetBytes(Encoding.ASCII.GetByteCount("OK")));
            client.Send(Encoding.ASCII.GetBytes("OK"));

            return data;
        }

        public void Disconnect()
        {
            try
            {
                client.Shutdown(SocketShutdown.Both);
                client.Close();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}