/* Copyright (C) 2001-2002 Taylor Gautier
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: JavaTron.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

/**
 * Stuff to get OS look-n-feel
 * JSC
 */
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * Defines the starting point class for JavaTron.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1.1 $
 */
public class JavaTron extends SingleFrameApplication
{
	public static AudioTronUI sui;
  /**
     * Do Startup stuff here...
     */
    @Override
	protected void startup() {
        
    }

	/**
	 * Application getter
	 * @return A JavaTron Object
	 */
	public static JavaTron getApplication() {
        return Application.getInstance(JavaTron.class);
    }

  /**
  * Main method - start of the program
  *
  * @param args command line args: [ipaddress] [name] [password]
  */
  public static void main(String args[])
  {
    AudioTronState at;
	AudioTronUI ui;
    
    try {
      at = new AudioTronState(args[0], args[1], args[2]);
    } catch (ArrayIndexOutOfBoundsException e) {
      try { 
        at = new AudioTronState(args[0], args[1]);
      } catch (ArrayIndexOutOfBoundsException e1) {
        try {
          at = new AudioTronState(args[0]);
        } catch (ArrayIndexOutOfBoundsException e2) {
          at = new AudioTronState();
        }
      }
    }
    
    ui = new AudioTronUI(at,getApplication());

	sui=ui;
    ui.go();    
    at.start();
  }

  public AudioTronUI getUI(){
	  return(sui);

  }
}
