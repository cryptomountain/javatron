
package JavaTron;

import javax.swing.JOptionPane;
/**
 *
 * @author Joe Culbreth
 */
public class JPop extends JOptionPane{
	public JPop(String title,String msg){
		super.showMessageDialog(null,msg,title,JOptionPane.INFORMATION_MESSAGE);
	}
}
