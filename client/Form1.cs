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
            //AsynchronousClient.StartClient();
            
            Bitmap bmpScreenshot = new Bitmap(Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height);
            Graphics g = Graphics.FromImage(bmpScreenshot);
            g.CopyFromScreen(0, 0, 0, 0, Screen.PrimaryScreen.Bounds.Size);

            Cursor.Draw(g, new Rectangle(Cursor.Position.X, Cursor.Position.Y, Cursor.Size.Width, Cursor.Size.Height));

            MemoryStream memoryStream = new MemoryStream();
            bmpScreenshot.Save(memoryStream, System.Drawing.Imaging.ImageFormat.Jpeg);


            ImageForm imageForm = new client.ImageForm();
            imageForm.pictureBox.Image = Image.FromStream(memoryStream);

            imageForm.Show();
        }
    }
}
