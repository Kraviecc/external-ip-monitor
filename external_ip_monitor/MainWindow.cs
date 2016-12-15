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
		//Rozszerzanie ekranu - obydwie linie potrzebne bo bez pierwszej rozjezdza sie ekran
		//xrandr --fb 3840x1080 --output VGA-1 --paning 3840x1080+0+0
		//xrandr --fb 3840x1080 --output VGA-1 --paning 1920x1080+0+0
		//Powrot
		//xrandr -s 1920x1080
		Bitmap bmpScreenshot = new Bitmap(1920, 1080);
		Graphics g = Graphics.FromImage(bmpScreenshot);
		g.CopyFromScreen(1920, 0, 0, 0, bmpScreenshot.Size);

		bmpScreenshot.Save("test.jpg", System.Drawing.Imaging.ImageFormat.Jpeg);
		//external_ip_monitor.Server.StartListening();
	}
}
