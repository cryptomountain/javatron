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
 * $Id: PriorityQueue.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.util.*;

/**
 * This class implements a 'PriorityQueue'.  This is not a priority queue
 * in the classic sense as usually implemented by a heap.  It is rather a 
 * simple structure that allows an external user to add an item to a one
 * queue in a list of prioritized queues.
 * <p>
 * There are n prioritized queue's, initialized at creation.  The
 * PriorityQueue will remove all items from its internal prioritized queue
 * number 0 before removing items from number 1, and then from number 2 and 
 * so on.
 * <p>
 * There are no means for changing the priority of items in the queue, so
 * items placed in queues higher than 0 could be starved.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $  
 */
public class PriorityQueue
{
  Vector[] queues;
  Vector listeners;
    
  /**
   * Construct a PriorityQueue with <i>number</i> internal prioritized queues.
   *
   * @param number the number of internal queues to hold
   */  
  public PriorityQueue(int number) {
    listeners = new Vector();
    queues = new Vector[number];
    
    for (int i = 0; i < number; i++) {
      queues[i] = new Vector();
    }   
  }
  
  /**
   * Interface to list for items inserted
   */
  public interface ElementInsertionListener
  {
    public void elementInserted(Object item, int priority);
  }  

  public class PrioritizedElement               
  {    
    public Object element;
    public int priority;
    
    public PrioritizedElement(Object element_, int priority_)
    {
      element = element_;
      priority = priority_;
    }    
  }
  
  /**
   * Add an ElementInsertionListener
   */
  public void addElementInsertionListener(ElementInsertionListener listener)
  {
    listeners.addElement(listener);
  }
  
  /**
   * REmove an ElementInsertionListener
   */
  public void removeElementInsertionListener(ElementInsertionListener listener)
  {
    listeners.removeElement(listener);
  }
  
  /**
   * Add an item to the queue using the specified priority.
   *
   * @param item the item to add
   * @param priority the prioritized queue to add the item to where 
   *                 0 <= priority < number where number is the initial
   *                 number of prioritized queues that were created in
   *                 the constructor.
   * 
   * @throws ArrayOutOfBoundsException if !(0 <= priority < number)
   */
  public void add(Object item, int priority)
  {   
    queues[priority].add(item);
    for (Enumeration e = listeners.elements(); e.hasMoreElements(); )
    {
      ElementInsertionListener listener = 
        (ElementInsertionListener) e.nextElement();
      listener.elementInserted(item, priority);
    }
  }
  
  /**
   * Removes the specified element from the queue
   *
   * @param element the element to remove
   */
  public void remove(Object element)
  {
    for (int i = 0; i < queues.length; i++) {
      queues[i].removeElement(element);
    }
  }
  
  /**
   * Removes the next item from the prioritized queues in order of the 
   * queues.  All items are removed from 0, then 1, then 2 and so on.
   *
   * @return remove the object removed, null if the queue is empty.
   */
  public PrioritizedElement remove()
  {
    for (int i = 0; i < queues.length; i++) {
      if (!queues[i].isEmpty()) {
        Object ret = queues[i].elementAt(0);
        queues[i].removeElementAt(0);
        return new PrioritizedElement(ret, i);
      }
    }
    
    return null;     
  }
  
  /**
   * Returns true if all prioritized queues are empty.
   *
   * @return true if the queue is empty, false if not.
   */
  public boolean isEmpty() {
    for (int i = 0; i < queues.length; i++) {
      if (!queues[i].isEmpty()) {
        return false;
      }
    }
    
    return true;
  }
}