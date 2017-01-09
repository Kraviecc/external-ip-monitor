using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace client
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnConnect_Click(object sender, EventArgs e)
        {
            AsynchronousClient asynchronousClient = new AsynchronousClient(Convert.ToInt32(txtPort.Text), txtAddress.Text);

            string resolution = System.Windows.SystemParameters.PrimaryScreenWidth.ToString() 
                        + "x" + System.Windows.SystemParameters.PrimaryScreenHeight.ToString();

            asynchronousClient.StartClient(resolution);

            //ImageForm imageForm = new ImageForm();
            //imageForm.pictureBox.Image = Image.FromStream(new MemoryStream()); //receive screenshot and put here

            //imageForm.Show();
        }
    }
}
