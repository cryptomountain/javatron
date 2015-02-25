/* Copyright (C) 2001 Taylor Gautier
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
 * $Id: WrappedMethod.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.lang.reflect.*;

/**
 * Class to wrap up a target, it's method, and arguments to the method
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class WrappedMethod
{
  public Object target;
  public Method m; 
  public Object [] args;

  private boolean debug = false;

  public WrappedMethod(Object target_, Method m_)
  {
    this(target_, m_, null);
  }
  
  public WrappedMethod(Object target_, Method m_, Object[] args_)
  {
    target = target_;
    m = m_;
    args = args_;
  }

  /**
   * Invoke the wrapped method on the wrapped target with the wrapped args
   *
   * @see Method#invoke
   */
  public Object invoke()
  throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
	  if(debug){System.out.println("WM: "+target+", "+args);}
	  return m.invoke(target, args);
  }
  
  /**
   * Invoke the wrapped method on the wrapped target
   *
   * @see Method#invoke
   */
  public Object invoke(Object[] args)
  throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
	  return m.invoke(target, args);
  }
  
  /**
   * Find a declared method, using the target object's inheritance path
   *
   * @param t the object to find the method for
   * @param method the name of the method
   * @param args arguments to the method
   *
   * @return the method if found, null if not found
   */   
  public static Method findDeclaredMethod(Object t, String method, Class[] args)
  { return findDeclaredMethod(t.getClass(), method, args); }

  /**
   * Find a declared method, using the target class's inheritance path
   *
   * @param c the class to find the method for
   * @param method the name of the method
   * @param args arguments to the method
   *
   * @return the method if found, null if not found
   */     
  public static Method findDeclaredMethod(Class c, String method, Class[] args)
  {
    Method m = null;
    
    while (m == null && c != null) {
      try {
        m = c.getDeclaredMethod(method, args);
      } catch (NoSuchMethodException nsme) {
        // ignore
      }
          
      c = c.getSuperclass();
    }
    
    return m;
  }
}
