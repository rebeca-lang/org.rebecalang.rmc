//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph;

import java.io.PrintStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 */
public class Attributes {
  private Hashtable<Object, String> ht;

  public Attributes () {
    ht = new Hashtable<Object, String>();
  }

  public Attributes (Attributes a) {
    ht = new Hashtable<Object, String>();

    for (Enumeration<Object> e = a.ht.keys(); e.hasMoreElements();) {
      Object key = e.nextElement();
      ht.put(key, a.ht.get(key));
    }
  }

  public Attributes (String s) {
    ht = new Hashtable<Object, String>();

    if (s.equals("-")) {
      return;
    }

    StringTokenizer st = new StringTokenizer(s, ",");

    while (st.hasMoreTokens()) {
      String e = st.nextToken();

      int    idx = e.indexOf("=");

      String key;
      String value;

      if (idx == -1) {
        key = e;
        value = "";
      } else {
        key = e.substring(0, idx);
        value = e.substring(idx + 1);
      }

      ht.put(key, value);
    }
  }

  public void setBoolean (String name, boolean value) {
    if (value) {
      ht.put(name, "");
    } else {
      ht.remove(name);
    }
  }

  public boolean getBoolean (String name) {
    return ht.get(name) != null;
  }

  public void setInt (String name, int value) {
    ht.put(name, Integer.toString(value));
  }

  public int getInt (String name) {
    Object o = ht.get(name);

    if (o == null) {
      return 0;
    }

    try {
      return Integer.parseInt((String) o);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public void setString (String name, String value) {
    ht.put(name, value);
  }

  public String getString (String name) {
    return ht.get(name);
  }

  public synchronized void save (PrintStream out, int format) {
    switch (format) {
    //      case Graph.SM_FORMAT: save_sm(out); break;
    //      case Graph.FSP_FORMAT: save_fsp(out); break;
    case Graph.XML_FORMAT:
      save_xml(out);

      break;
    }
  }

  public String toString () {
    if (ht.size() == 0) {
      return "-";
    }

    StringBuilder sb = new StringBuilder();

    for (Enumeration<Object> e = ht.keys(); e.hasMoreElements();) {
      Object key = e.nextElement();
      String value = ht.get(key);

      sb.append(key);

      if (!value.equals("")) {
        sb.append('=');
        sb.append(value);
      }

      if (e.hasMoreElements()) {
        sb.append(',');
      }
    }

    return sb.toString();
  }

  public void unset (String name) {
    ht.remove(name);
  }

  private synchronized void save_xml (PrintStream out) {
    if (ht.size() == 0) {
      return;
    }

    for (Enumeration<Object> e = ht.keys(); e.hasMoreElements();) {
      String key = (String) e.nextElement();
      String value = ht.get(key);

      if (value == "") {
        out.println("<" + key + "/>");
      } else {
        out.println("<" + key + ">" + value + "</" + key + ">");
      }
    }
  }
}
