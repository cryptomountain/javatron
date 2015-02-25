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
 * $Id: Adapter.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

/**
 * Defines an adapter class.  An adapter is designed to act as a filter
 * on the object, returning a customized toString() method.  It may
 * sometimes be necessary for users to get the underlying object, so
 * Adapter has one method, getSource().
 *
 * object's toString() method and make a 
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public interface Adapter
{
  /** 
   * Return the object that is the source object.
   */
  public Object getSource();    
}  