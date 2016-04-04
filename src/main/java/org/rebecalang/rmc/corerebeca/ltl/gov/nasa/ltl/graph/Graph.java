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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * DOCUMENT ME!
 */
public class Graph {
	public static final int SM_FORMAT = 0;

	public static final int FSP_FORMAT = 1;

	public static final int XML_FORMAT = 2;

	public static final int SPIN_FORMAT = 3;

	private List<Node> nodes;

	private Node init;

	private Attributes attributes;

	public Graph(Attributes a) {
		init(a);
	}

	public Graph() {
		init(null);
	}

	public synchronized void setAttributes(Attributes a) {
		attributes = new Attributes(a);
	}

	public synchronized void setBooleanAttribute(String name, boolean value) {
		attributes.setBoolean(name, value);
	}

	public boolean getBooleanAttribute(String name) {
		return attributes.getBoolean(name);
	}

	public int getEdgeCount() {
		int count = 0;

		for (Iterator<Node> i = new LinkedList<Node>(nodes).iterator(); i.hasNext();) {
			count += i.next().getOutgoingEdgeCount();
		}

		return count;
	}

	public synchronized void setInit(Node n) {
		if (nodes.contains(n)) {
			init = n;
			number();
		}
	}

	public Node getInit() {
		return init;
	}

	public synchronized void setIntAttribute(String name, int value) {
		attributes.setInt(name, value);
	}

	public int getIntAttribute(String name) {
		return attributes.getInt(name);
	}

	public Node getNode(int id) {
		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node n = i.next();

			if (n.getId() == id) {
				return n;
			}
		}

		return null;
	}

	public int getNodeCount() {
		return nodes.size();
	}

	public List<Node> getNodes() {
		return new LinkedList<Node>(nodes);
	}

	public synchronized void setStringAttribute(String name, String value) {
		attributes.setString(name, value);
	}

	public String getStringAttribute(String name) {
		return attributes.getString(name);
	}

	public static Graph load() throws IOException {
		return load(new BufferedReader(new InputStreamReader(System.in)));
	}

	public static Graph load(String fname) throws IOException {
		return load(new BufferedReader(new FileReader(fname)));
	}

	public synchronized void dfs(Visitor v) {
		if (init == null) {
			return;
		}

		forAllNodes(new EmptyVisitor() {
			public void visitNode(Node n) {
				n.setBooleanAttribute("_reached", false);
			}
		});

		dfs(init, v);

		forAllNodes(new EmptyVisitor() {
			public void visitNode(Node n) {
				n.setBooleanAttribute("_reached", false);
			}
		});
	}

	public synchronized void forAll(Visitor v) {
		for (Iterator<Node> i = new LinkedList<Node>(nodes).iterator(); i.hasNext();) {
			Node n = i.next();

			v.visitNode(n);

			n.forAllEdges(v);
		}
	}

	public synchronized void forAllEdges(Visitor v) {
		for (Iterator<Node> i = new LinkedList<Node>(nodes).iterator(); i.hasNext();) {
			Node n = i.next();

			n.forAllEdges(v);
		}
	}

	public synchronized void forAllNodes(Visitor v) {
		for (Iterator<Node> i = new LinkedList<Node>(nodes).iterator(); i.hasNext();) {
			Node n = i.next();
			v.visitNode(n);
		}
	}

	public synchronized void save(int format) {
		save(System.out, format);
	}

	public synchronized void save() {
		save(System.out, SM_FORMAT);
	}

	public synchronized void save(String fname, int format) throws IOException {
		save(new PrintStream(new FileOutputStream(fname)), format);
	}

	public synchronized void save(String fname) throws IOException {
		save(new PrintStream(new FileOutputStream(fname)), SM_FORMAT);
	}

	synchronized void addNode(Node n) {
		nodes.add(n);

		if (init == null) {
			init = n;
		}

		number();
	}

	synchronized void removeNode(Node n) {
		nodes.remove(n);

		if (init == n) {
			if (nodes.size() != 0) {
				init = nodes.get(0);
			} else {
				init = null;
			}
		}

		number();
	}

	private void init(Attributes a) {
		if (a == null) {
			attributes = new Attributes();
		} else {
			attributes = a;
		}

		nodes = new LinkedList<Node>();
		init = null;
	}

	private static Graph load(BufferedReader in) throws IOException {
		int ns = readInt(in);
		Node[] nodes = new Node[ns];

		Graph g = new Graph(readAttributes(in));

		for (int i = 0; i < ns; i++) {
			int nt = readInt(in);

			if (nodes[i] == null) {
				nodes[i] = new Node(g, readAttributes(in));
			} else {
				nodes[i].setAttributes(readAttributes(in));
			}

			for (int j = 0; j < nt; j++) {
				int nxt = readInt(in);
				String gu = readString(in);
				String ac = readString(in);

				if (nodes[nxt] == null) {
					nodes[nxt] = new Node(g);
				}

				new Edge(nodes[i], nodes[nxt], gu, ac, readAttributes(in));
			}
		}

		g.number();

		return g;
	}

	private synchronized void number() {
		int cnt;

		if (init != null) {
			init.setId(0);
			cnt = 1;
		} else {
			cnt = 0;
		}

		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node n = i.next();

			if (n != init) {
				n.setId(cnt++);
			}
		}
	}

	private static Attributes readAttributes(BufferedReader in)
			throws IOException {
		return new Attributes(readLine(in));
	}

	private static int readInt(BufferedReader in) throws IOException {
		return Integer.parseInt(readLine(in));
	}

	private static String readLine(BufferedReader in) throws IOException {
		String line;

		do {
			line = in.readLine();

			int idx = line.indexOf('#');

			if (idx != -1) {
				line = line.substring(0, idx);
			}

			line = line.trim();
		} while (line.length() == 0);

		return line;
	}

	private static String readString(BufferedReader in) throws IOException {
		return readLine(in);
	}

	private synchronized void dfs(Node n, Visitor v) {
		final Visitor visitor = v;

		if (n.getBooleanAttribute("_reached")) {
			return;
		}

		n.setBooleanAttribute("_reached", true);

		v.visitNode(n);

		n.forAllEdges(new EmptyVisitor() {
			public void visitEdge(Edge e) {
				dfs(e.getNext(), visitor);
			}
		});
	}

	// Modified by robbyjo - Jul 15, 2002
	private synchronized void save(PrintStream out, int format) {
		switch (format) {
		case SM_FORMAT:
			save_sm(out);

			break;

		case FSP_FORMAT:
			save_fsp(out);

			break;

		case XML_FORMAT:
			save_xml(out);

			break;

		case SPIN_FORMAT:
			save_spin(out);

			break;

		default:
			throw new RuntimeException("Unknown format!");
		}
	}

	// Modified by ckong - Sept 7, 2001
	private synchronized void save_fsp(PrintStream out) {
		boolean empty = false;

		if (init != null) {
			out.print("RES = S" + init.getId());
		} else {
			out.print("Empty");
			empty = true;
		}

		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			//System.out.println(",");
			out.println(",");

			Node n = i.next();
			n.save(out, FSP_FORMAT);
		}

		//System.out.println(".");
		out.println(".");

		int nsets = getIntAttribute("nsets");

		if ((nsets == 0) && !empty) {
			boolean first = true;

			//System.out.print("AS = { ");
			out.print("AS = { ");

			for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
				Node n = i.next();

				if (n.getBooleanAttribute("accepting")) {
					if (!first) {
						//System.out.print(", ");
						out.print(", ");
					} else {
						first = false;
					}

					//System.out.print("S" + n.getId());
					out.print("S" + n.getId());
				}
			}

			//System.out.println(" }");
			out.println(" }");
		} else if (!empty) { // nsets != 0

			for (int k = 0; k < nsets; k++) {
				boolean first = true;

				//System.out.print("AS"+k+" = { ");
				out.print("AS" + k + " = { ");

				for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
					Node n = i.next();

					if (n.getBooleanAttribute("acc" + k)) {
						if (!first) {
							//System.out.print(", ");
							out.print(", ");
						} else {
							first = false;
						}

						//System.out.print("S" + n.getId());
						out.print("S" + n.getId());
					}
				}

				//System.out.println(" }");
				out.println(" }");
			}
		}

		if (out != System.out) {
			out.close();
		}
	}

	private synchronized void save_sm(PrintStream out) {
		out.println(nodes.size());
		out.println(attributes);

		if (init != null) {
			init.save(out, SM_FORMAT);
		}

		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node n = i.next();

			if (n != init) {
				n.save(out, SM_FORMAT);
			}
		}
	}

	// robbyjo's contribution
	private synchronized void save_spin(PrintStream out) {
		if (init != null) {
			out.println("never {");
		} else {
			out.println("Empty");
			return;
		}

		init.save(out, SPIN_FORMAT);
		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node n = i.next();

			if (init == n) {
				continue;
			}

			n.save(out, SPIN_FORMAT);
			out.println();
		}

		out.println("}");
	}

	private synchronized void save_xml(PrintStream out) {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<graph nodes=\"" + nodes.size() + "\">");
		attributes.save(out, XML_FORMAT);

		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node n = i.next();

			if (n != init) {
				n.save(out, XML_FORMAT);
			} else {
				n.setBooleanAttribute("init", true);
				n.save(out, XML_FORMAT);
				n.setBooleanAttribute("init", false);
			}
		}

		out.println("</graph>");
	}
}
