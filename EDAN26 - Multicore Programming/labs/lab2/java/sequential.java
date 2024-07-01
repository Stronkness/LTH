import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;

class Graph {

	int	s;
	int	t;
	int	n;
	int	m;
	int workingThreads;
	Node	excess;		// list of nodes with excess preflow
	Node	node[];
	Edge	edge[];
	ReentrantLock mutex;
	Condition cond;

	Graph(Node node[], Edge edge[])
	{
		this.node	= node;
		this.n		= node.length;
		this.edge	= edge;
		this.m		= edge.length;
		mutex = new ReentrantLock();
		cond = mutex.newCondition();
		workingThreads = 0;
	}

	void enter_excess(Node u)
	{
		if (u != node[s] && u != node[t]) {
			mutex.lock();
			u.next = excess;
			excess = u;
			cond.signal();
			mutex.unlock();
		}
	}

	Node other(Edge a, Node u)
	{
		if (a.u == u)
			return a.v;
		else
			return a.u;
	}

	void relabel(Node u)
	{
		u.mutex.lock();
		u.h += 1;
		u.mutex.unlock();

		enter_excess(u);
	}

	void push(Node u, Node v, Edge a)
	{
		int d;

		if (u == a.u) {
			d = Math.min(u.e, a.c - a.f);
			a.f += d;
		} else {
			d = Math.min(u.e, a.c + a.f);
			a.f -= d;
		}

		u.e -= d;
		v.e += d;


		assert(d >= 0);
		assert(u.e >= 0);
		assert(Math.abs(a.f) <= a.c);

		if (u.e > 0) {
			enter_excess(u);
		}

		if (v.e == d) {
			enter_excess(v);
		}
	}

	void dispatch(Node u)
	{
		Iterator<Edge> p = u.adj.listIterator();
		Node v = null;
		Edge e = null;
		int  b = 0;

		while (p.hasNext()) {
			e = p.next();

			if (u == e.u) {
				v = e.v;
				b = 1;
			} else {
				v = e.u;
				b = -1;
			}

			if (u.i < v.i) {
				u.mutex.lock();
				v.mutex.lock();
			} else {
				v.mutex.lock();
				u.mutex.lock();
			}

			if (u.h > v.h && b * e.f < e.c) {
				break;
			} else {
				u.mutex.unlock();
				v.mutex.unlock();
				v = null;
			}
		}

		if (v != null) {
			push(u, v, e);
			u.mutex.unlock();
			v.mutex.unlock();
		} else {
			relabel(u);
		}
	}

	void push_thread() throws InterruptedException
	{
		Node u;
		while (true) {
			mutex.lock();
			u = excess;
			excess = excess == null ? excess : excess.next;
			while (u == null) {
				if (workingThreads == 0) {
					mutex.unlock();
					return;
				}

				cond.await();
				u = excess;
				excess = excess == null ? excess : excess.next;
			}
			workingThreads += 1;

			mutex.unlock();

			dispatch(u);

			mutex.lock();

			workingThreads -= 1;
			cond.signalAll();
			mutex.unlock();
		}
	}

	int preflow(int s, int t)
	{
		ListIterator<Edge>	iter;
		Edge			a;

		this.s = s;
		this.t = t;
		node[s].h = n;

		iter = node[s].adj.listIterator();
		while (iter.hasNext()) {
			a = iter.next();

			node[s].e += a.c;

			push(node[s], other(a, node[s]), a);
		}

		workingThreads = 0;
		Thread[] threads = new Thread[10];

		for (int i = 0; i < 10; i++) {
			threads[i] = new Thread(new Runnable() {
					public void run() {
						try {
							push_thread();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			);

			threads[i].start();
		}

		for (Thread t1 : threads) {
			try {
				t1.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return node[t].e;
	}
}

class Node {
	int	h;
	int	e;
	int	i;
	Node	next;
	LinkedList<Edge>	adj;
	ReentrantLock mutex;

	Node(int i)
	{
		this.i = i;
		adj = new LinkedList<Edge>();
		mutex = new ReentrantLock();
	}
}

class Edge {
	Node	u;
	Node	v;
	int	f;
	int	c;

	Edge(Node u, Node v, int c)
	{
		this.u = u;
		this.v = v;
		this.c = c;

	}
}

class Preflow {
	public static void main(String args[])
	{
		double	begin = System.currentTimeMillis();
		Scanner s = new Scanner(System.in);
		int	n;
		int	m;
		int	i;
		int	u;
		int	v;
		int	c;
		int	f;
		Graph	g;

		n = s.nextInt();
		m = s.nextInt();
		s.nextInt();
		s.nextInt();
		Node[] node = new Node[n];
		Edge[] edge = new Edge[m];

		for (i = 0; i < n; i += 1)
			node[i] = new Node(i);

		for (i = 0; i < m; i += 1) {
			u = s.nextInt();
			v = s.nextInt();
			c = s.nextInt();
			edge[i] = new Edge(node[u], node[v], c);
			node[u].adj.addLast(edge[i]);
			node[v].adj.addLast(edge[i]);
		}

		g = new Graph(node, edge);
		f = g.preflow(0, n-1);
		double	end = System.currentTimeMillis();
		System.out.println("t = " + (end - begin) / 1000.0 + " s");
		System.out.println("f = " + f);
	}
}