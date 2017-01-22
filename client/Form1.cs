using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace client
{
    public partial class Form1 : Form
    {
        private ImageForm imageForm;
        private SynchronousClient asynchronousClient;
        private byte[] previousScreenshot;
        private byte[] actualScreenshot;

        public Form1()
        {
            InitializeComponent();
        }

        private void btnConnect_Click(object sender, EventArgs e)
        {
            asynchronousClient = new SynchronousClient(Convert.ToInt32(txtPort.Text), txtAddress.Text);

            string resolution = System.Windows.SystemParameters.PrimaryScreenWidth.ToString()
                        + "x" + System.Windows.SystemParameters.PrimaryScreenHeight.ToString();
            try
            {
                asynchronousClient.StartClient(resolution);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error: " + ex.Message);
                return;
            }
            BackgroundWorker bw = new BackgroundWorker();
            bw.DoWork += Bw_DoWork;

            imageForm = new ImageForm();
            imageForm.Show();

            bw.RunWorkerAsync();
        }

        private void Bw_DoWork(object sender, DoWorkEventArgs e)
        {
            while (true)
            {
                try
                {
                    actualScreenshot = asynchronousClient.Receive();

                    if (previousScreenshot == null)
                    {
                        previousScreenshot = new byte[actualScreenshot.Length];
                        actualScreenshot.CopyTo(previousScreenshot, 0);
                        imageForm.pictureBox.Image = Image.FromStream(new MemoryStream(previousScreenshot));
                    }
                    else
                    {
                        if (actualScreenshot.Length > 0)
                            previousScreenshot = mergeScreenshots(previousScreenshot, actualScreenshot);

                        imageForm.pictureBox.Image = Image.FromStream(new MemoryStream(previousScreenshot));
                    }

                }
                catch (Exception ex)
                {
                    MessageBox.Show("Error: " + ex.Message);
                    asynchronousClient.Disconnect();
                    Invoke((MethodInvoker)delegate
                    {
                        imageForm.Close();
                    });
                    return;
                }
            }
        }

        private byte[] mergeScreenshots(byte[] previous, byte[] actual)
        {
            byte[] mergedScreenshot;
            var valuesAndPositions = Encoding.Default.GetString(actual);
            var lines = valuesAndPositions.Split('\n');
            var length = Convert.ToInt32(lines.ElementAt(lines.Length - 2).Split(' ')[1]);
            var positions = valuesAndPositions.Split('\n').ToList();

            if (length > previous.Length)
                mergedScreenshot = new byte[length];
            else
                mergedScreenshot = new byte[previous.Length];

            previous.CopyTo(mergedScreenshot, 0);

            foreach (var pos in positions)
            {
                if (pos.Split(' ').Length == 2)
                {
                    unchecked
                    {
                        mergedScreenshot[Convert.ToInt32(pos.Split(' ')[1])] = (byte)Convert.ToSByte(pos.Split(' ')[0]);
                    }
                }
            }
            return mergedScreenshot;
        }
    }
}
