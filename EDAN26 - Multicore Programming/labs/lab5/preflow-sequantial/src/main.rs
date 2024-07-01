#[macro_use] extern crate text_io;

use std::sync::{Mutex,Arc,}; // MutexGuard
use std::collections::LinkedList;
// use std::cmp;
// use std::thread;
use std::collections::VecDeque;

struct Node {
	i:	usize,			/* index of itself for debugging.	*/
	e:	i32,			/* excess preflow.			*/
	h:	i32,			/* height.				*/
}

struct Edge {
        u:      usize,  
        v:      usize,
        f:      i32,
        c:      i32,
}

impl Node {
	fn new(ii:usize) -> Node {
		Node { i: ii, e: 0, h: 0 }
	}

}

impl Edge {
        fn new(uu:usize, vv:usize,cc:i32) -> Edge {
                Edge { u: uu, v: vv, f: 0, c: cc }      
        }
}


fn push(u: &mut Node, v: &mut Node, e: &mut Edge, excess: &mut VecDeque<usize>) {
    let d: i32; // remaining capacity of the edge

    print!("push from {} to {}: ", u.i, v.i);
    print!("f = {}, c = {}, so ", e.f, e.c);

    if u.i == e.u {
        d = u.e.min(e.c - e.f);
        e.f += d;
    } else {
        d = u.e.min(e.c + e.f);
        e.f -= d;
    }

    println!("pushing {}\n", d);

    u.e -= d;
    v.e += d;

    // The following are always true.
    assert!(d >= 0);
    assert!(u.e >= 0);
    assert!(e.f.abs() <= e.c);

    if u.e > 0 {
        // Still some remaining, so let u push more.
        excess.push_back(u.i);
    }

    if v.e == d {
        // Since v has d excess now, it had zero before and
        // can now push.
        excess.push_back(v.i);
    }
}

fn relabel(u: &mut Node, excess: &mut VecDeque<usize>) {
	u.h += 1;
	excess.push_back(u.i);
}

fn dispatch(u: usize, node: &mut Vec<Arc<Mutex<Node>>>, edge: &mut Vec<Arc<Mutex<Edge>>>, adj_edges: &mut LinkedList<usize>, excess: &mut VecDeque<usize>) {
    let mut b: i32;

	if u == 0 || u == node.len() - 1 {
		return;
	}

    // If we can push, we must push, and only if we could not push anything,
    // we are allowed to relabel.
    // We can push to multiple nodes if we wish, but here we just push once for simplicity.

    let p = adj_edges.iter();
	let mut node_u = node[u].lock().unwrap();

	print!("selected u = {} with ", node_u.i);
	print!("h = {} and e = {}\n", node_u.h, node_u.e);

    for &e in p {
        let mut edge_e = edge[e].lock().unwrap();
		let mut other_node;

		if edge_e.u == u {
			other_node = node[edge_e.v].lock().unwrap();
			b = 1;
		} else {
			other_node = node[edge_e.u].lock().unwrap();
			b = -1;
		}

        if node_u.h > other_node.h && b * edge_e.f < edge_e.c {
			push(&mut node_u, &mut other_node, &mut edge_e, excess);
            return;
        }
    }

    relabel(&mut node_u, excess);
}



fn main() {

	let n: usize = read!();		/* n nodes.						*/
	let m: usize = read!();		/* m edges.						*/
	let _c: usize = read!();	/* underscore avoids warning about an unused variable.	*/
	let _p: usize = read!();	/* c and p are in the input from 6railwayplanning.	*/
	let mut node = vec![];
	let mut edge = vec![];
	let mut adj: Vec<LinkedList<usize>> =Vec::with_capacity(n);
	let mut excess: VecDeque<usize> = VecDeque::new();
	let debug = false;

	let s = 0;
	let t = n-1;

	println!("n = {}", n);
	println!("m = {}", m);

	for i in 0..n {
		let u:Node = Node::new(i);
		node.push(Arc::new(Mutex::new(u))); 
		adj.push(LinkedList::new());
	}

	for i in 0..m {
		let u: usize = read!();
		let v: usize = read!();
		let c: i32 = read!();
		let e:Edge = Edge::new(u,v,c);
		adj[u].push_back(i);
		adj[v].push_back(i);
		edge.push(Arc::new(Mutex::new(e))); 
	}

	if debug {
		for i in 0..n {
			print!("adj[{}] = ", i);
			let iter = adj[i].iter();

			for e in iter {
				print!("e = {}, ", e);
			}
			println!("");
		}
	}

	node[0].lock().unwrap().h = n as i32;

	println!("initial pushes");
	let iter = adj[s].iter();
	let mut node_s = node[s].lock().unwrap();

	for &e in iter {
		let mut edge_e = edge[e].lock().unwrap();
		let mut node_u = if edge_e.u == s { node[edge_e.v].lock().unwrap() } else { node[edge_e.u].lock().unwrap() };

		node_s.e += edge_e.c;

		push(&mut node_s, &mut node_u, &mut edge_e, &mut excess);
		drop(node_u);
	}

	drop(node_s);

	// but nothing is done here yet...

	while !excess.is_empty() {
		// let mut c = 0;
		let u = excess.pop_front().unwrap();

		dispatch(u, &mut node, &mut edge, &mut adj[u], &mut excess);
	}

	println!("f = {}", node[t].lock().unwrap().e);

}
