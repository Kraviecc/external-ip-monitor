
// This file has been generated by the GUI designer. Do not modify.

public partial class MainWindow
{
	private global::Gtk.Fixed fixed2;

	private global::Gtk.Button btnStart;

	protected virtual void Build()
	{
		global::Stetic.Gui.Initialize(this);
		// Widget MainWindow
		this.Name = "MainWindow";
		this.Title = global::Mono.Unix.Catalog.GetString("Client");
		this.WindowPosition = ((global::Gtk.WindowPosition)(4));
		// Container child MainWindow.Gtk.Container+ContainerChild
		this.fixed2 = new global::Gtk.Fixed();
		this.fixed2.Name = "fixed2";
		this.fixed2.HasWindow = false;
		// Container child fixed2.Gtk.Fixed+FixedChild
		this.btnStart = new global::Gtk.Button();
		this.btnStart.CanFocus = true;
		this.btnStart.Name = "btnStart";
		this.btnStart.UseUnderline = true;
		this.btnStart.Label = global::Mono.Unix.Catalog.GetString("Start");
		this.fixed2.Add(this.btnStart);
		global::Gtk.Fixed.FixedChild w1 = ((global::Gtk.Fixed.FixedChild)(this.fixed2[this.btnStart]));
		w1.X = 150;
		w1.Y = 102;
		this.Add(this.fixed2);
		if ((this.Child != null))
		{
			this.Child.ShowAll();
		}
		this.DefaultWidth = 400;
		this.DefaultHeight = 300;
		this.Show();
		this.DeleteEvent += new global::Gtk.DeleteEventHandler(this.OnDeleteEvent);
		this.btnStart.Clicked += new global::System.EventHandler(this.OnBtnStartClicked);
	}
}