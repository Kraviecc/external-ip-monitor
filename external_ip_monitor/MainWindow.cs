using System;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using Gtk;

public partial class MainWindow : Gtk.Window
{
	public MainWindow() : base(Gtk.WindowType.Toplevel)
	{
		Build();
	}

	protected void OnDeleteEvent(object sender, DeleteEventArgs a)
	{
		Gtk.Application.Quit();
		a.RetVal = true;
	}

	protected void OnBtnStartClicked(object sender, EventArgs e)
	{
		Bitmap bmpScreenshot = new Bitmap(Screen.Width, Screen.Height);
		Graphics g = Graphics.FromImage(bmpScreenshot);
		g.CopyFromScreen(1920, 0, 0, 0, new Size(1920, 1080));

		bmpScreenshot.Save("test.jpg", System.Drawing.Imaging.ImageFormat.Jpeg);
		//external_ip_monitor.Server.StartListening();
	}
}
