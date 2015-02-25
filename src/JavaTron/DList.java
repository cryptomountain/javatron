

package JavaTron;

/**
 *
 * @author Joe Culbreth
 */
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class DList extends JFrame
{
	Vector con   = new Vector();
	JList  list  = new DrList(con);

	public DList()
	{
		setBounds(1,1,600,400);
		getContentPane().setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter()
		{	public void windowClosing(WindowEvent ev){
				dispose();
				System.exit(0);
			}
		});

		for (int i=0; i < 100; i++) 
			con.add(" a " + i + " entry");

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10,40,10,40));
		panel.setLayout(new BorderLayout());
		JScrollPane js = new JScrollPane(panel);
		getContentPane().add("Center",js);

		panel.add("Center",list);
		setVisible(true);
	}

	public class DrList extends JList
	{
		int    from;

		public DrList(Vector v)
		{
			super(v);
			setBorder(new MatteBorder(1,1,1,1,Color.orange));
			addMouseListener(new MouseAdapter()
			{	public void mousePressed(MouseEvent m)
				{
					 from = getSelectedIndex();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter()
			{	public void mouseDragged(MouseEvent m)
				{
					int to  = getSelectedIndex();
					if (to == from) return;
					String s = (String)con.remove(from);
					con.add(to,s);
					from = to;
				}
			});
		}
	}

}
