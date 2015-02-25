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
 * $Id: VectorListModel.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import javax.swing.*;
import java.util.*;

/**
 * An adapter class provides an implementation of 
 * a list using a vector.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class VectorListModel extends AbstractListModel 
{
  protected Vector contents = new Vector();
  
  /**
   * Return an enumeration of the elements in this list
   */
  public Enumeration elements() { return contents.elements(); }    

  /**
   * Add an element to the list
   *
   * @param element the element to add
   */  
  public void addElement(Object element) { addElement(element, true); }    
  
  /**
   * Add an element to the list, optionally fire the change to listeners
   *
   * @param element the element to add
   * @param update if true, fire a change off to listeners
   */
  public void addElement(Object element, boolean update)
  {
    contents.addElement(element);
    if (update) {
      fireIntervalAdded(this, contents.size() - 1, contents.size() - 1);
    }
  }
 
  /**
   * Clear this list
   */
  public void clear() { clear(true); }   
  
  /**
   * Clear this list, optionally fire the change to listeners
   *
   * @param update if true, fire a change off to listeners
   */
  public void clear(boolean update)
  {
    int size = contents.size();      
    
    if (size > 0) {
      contents.clear();      
      
      if (update) {
        fireIntervalRemoved(this, 0, size - 1);
      }
    }
  }
 
  /**
   * Remove an element from the list
   *
   * @param element the element to remove
   */
  public void removeElement(Object element)
  {
    int index = contents.indexOf(element);
    contents.removeElement(element);
    fireIntervalRemoved(this, index, index);
  }
  
  /**
   * Remove an element from the list at the specified index
   *
   * @param index the index of the element to remove
   */
   public Object removeElementAt(int index)
  {
    return removeElementAt(index, true);
  }
  
  protected Object removeElementAt(int index, boolean update)
  {
    Object element = contents.elementAt(index);
    contents.removeElementAt(index);
    if (update) {
      fireIntervalRemoved(this, index, index);
    }
    
    return element;
  }
  
  /**
   * Insert an element to the list at the specified index
   *
   * @param element the object to insert
   * @param index the index to insert the object at
   *
   * @throws ArrayIndexOutOfBoundsException if the index is < 0 || >= size
   *         of the list
   */
  public void insertElementAt(Object element, int index)
  {
    insertElementAt(element, index, true);
  }
  
  protected void insertElementAt(Object element, int index, boolean update)
  {
    contents.insertElementAt(element, index);
    if (update) {
      fireIntervalAdded(this, index, index);
    }
  }
  
  /**
   * Move an element up in the list
   *
   * @param index the index of the element to move up in the list
   *
   * @throws ArrayIndexOutOfBoundsException if the index is < 0 || >= size
   *         of the list
   */
  public void moveElementUp(int index)
  {
    moveElementDown(index - 1);
  }
  
  /**
   * Move an element down in the list
   *
   * @param index the index of the element to move down in the list
   *
   * @throws ArrayIndexOutOfBoundsException if the index is < 0 || >= size
   *         of the list
   */
  public void moveElementDown(int index)
  {
    Object element = removeElementAt(index, false);
    insertElementAt(element, index+1, false);
    fireContentsChanged(this, index, index+1);
  }
  
  /**
   * Get an element at the specified index
   *
   * @param index the index of the element to get
   */    
  public Object getElementAt(int index) { return contents.elementAt(index); }
  
  /**
   * Retrieve the size (number of elements) of this list
   */
  public int getSize() { return contents.size(); }  
  
  protected void updated(int index) { updated(index, index); }    
  protected void updated(int index1, int index2)
  {
    fireContentsChanged(this, index1, index2);
  }
} 