using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace client
{
    public partial class Form1 : Form
    {
        private ImageForm imageForm = new ImageForm();
        private SynchronousClient asynchronousClient;

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

            imageForm.Show();

            bw.RunWorkerAsync();
        }

        private void Bw_DoWork(object sender, DoWorkEventArgs e)
        {
            while (true)
            {
                try
                {
                    var screenshot = asynchronousClient.Receive();
                    if (screenshot.Length != 0)
                        imageForm.pictureBox.Image = Image.FromStream(new MemoryStream(screenshot));
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Error: " + ex.Message);
                }
            }
        }
    }
}
