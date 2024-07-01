#[macro_use]
extern crate text_io;

use std::collections::{LinkedList, VecDeque};
use std::sync::{Mutex,Arc, MutexGuard,}; // MutexGuard
// use std::cmp;
use std::thread;

struct Node {
    i: usize,
    e: i32,
    h: i32,
}

struct Edge {
    u: usize,
    v: usize,
    f: i32,
    c: i32,
}

impl Node {
    fn new(ii: usize) -> Node {
        Node { i: ii, e: 0, h: 0 }
    }
}

impl Edge {
    fn new(uu: usize, vv: usize, cc: i32) -> Edge {
        Edge {
            u: uu,
            v: vv,
            f: 0,
            c: cc,
        }
    }
}

fn push(
    u: &mut MutexGuard<'_, Node>,
    v: &mut MutexGuard<'_, Node>,
    e: &mut MutexGuard<'_, Edge>,
    excess: &mut Arc<Mutex<VecDeque<usize>>>
) {
    let d: i32;

    // print!("push from {} to {}: ", u.i, v.i);
    // print!("f = {}, c = {}, so ", e.f, e.c);

    if u.i == e.u {
        d = u.e.min(e.c - e.f);
        e.f += d;
    } else {
        d = u.e.min(e.c + e.f);
        e.f -= d;
    }
    // println!("pushing, {}", d);

    u.e -= d;
    v.e += d;

    // These are always true
    assert!(d >= 0);
    assert!(u.e >= 0);
    assert!(e.f.abs() <= e.c);

    if u.e > 0 {
        excess.lock().unwrap().push_back(u.i);
    }
    if v.e == d {
        excess.lock().unwrap().push_back(v.i);
    }
}

fn relabel(u: &mut MutexGuard<'_, Node>, excess: &mut Arc<Mutex<VecDeque<usize>>>) {
    u.h += 1;
    // println!("Relabel {} now h = {}", u.i, u.h);
    excess.lock().unwrap().push_back(u.i);
}

fn dispatch(
    u_i: usize,
    node: &mut Vec<Arc<Mutex<Node>>>,
    edge: &mut Vec<Arc<Mutex<Edge>>>,
    adj: &Vec<LinkedList<usize>>,
    excess: &mut Arc<Mutex<VecDeque<usize>>>) {

        let mut b: i32;

        // Either source or sink
        if u_i == 0 || u_i == node.len() - 1 {
            return;
        }

        let p = adj[u_i].iter();
        

        // print!("selected u = {} with ", u_i);
        // print!("h = {} and e = {}\n", u.h, u.e);

        for &e_i in p {
            let e = &mut edge[e_i].lock().unwrap();
            let v_i = if u_i == e.u { e.v } else { e.u };
            if u_i < v_i {
                let u = &mut node[u_i].lock().unwrap();
                let v = &mut node[v_i].lock().unwrap();
                
                b = if u_i == e.u { 1 } else { -1 };


                if u.h > v.h && b * e.f < e.c {
                    push(u, v, e, excess);
                    return
                }
            } else {
                let v = &mut node[v_i].lock().unwrap();
                let u = &mut node[u_i].lock().unwrap();

                b = if u_i == e.u { 1 } else { -1 };

                if u.h > v.h && b * e.f < e.c {
                    push(u, v, e, excess);
                    return
                }
            }

        }

        let u = &mut node[u_i].lock().unwrap();

        relabel(u, excess);
}


fn main() {
    let n: usize = read!();
    let m: usize = read!();
    let _c: usize = read!();
    let _p: usize = read!();
    let mut node = vec![];
    let mut edge = vec![];
    let mut adj: Vec<LinkedList<usize>> = Vec::with_capacity(n);
    let excess: Arc<Mutex<VecDeque<usize>>> = Arc::new(Mutex::new(VecDeque::new()));
    let debug = false;

    let s = 0;
    let t = n - 1;

    // println!("n = {}", n);
    // println!("m = {}", m);

    for i in 0..n {
		let u:Node = Node::new(i);
		node.push(Arc::new(Mutex::new(u))); 
		adj.push(LinkedList::new());
    }

    for i in 0..m {
        let u: usize = read!();
        let v: usize = read!();
        let c: i32 = read!();
        let e: Edge = Edge::new(u, v, c);
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

    // Set height to num nodes for source
    node[s].lock().unwrap().h = n as i32;

    // println!("initial pushes from {}", node[s].lock().unwrap().i);
    let iter = adj[s].iter();

    for &e_i in iter {
        let e = &mut edge[e_i].lock().unwrap();
        let v_i = if s == e.u { e.v } else { e.u };
        let v = &mut node[v_i].lock().unwrap();

        v.e += e.c;
        e.f = e.c;
        excess.lock().unwrap().push_back(v_i);
    }

     // Create a vector to hold thread handles
    let mut handles = vec![];
    let num_threads = 10;

    // Spawn multiple threads to run the dispatch loop concurrently
    for _ in 0..num_threads {
        let mut node_clone = node.clone();
        let mut edge_clone = edge.clone();
        let adj_clone = adj.clone();
        let mut excess_mutex_clone = excess.clone();

        let handle = thread::spawn(move || {
            loop {
                let tmp = excess_mutex_clone.lock().unwrap().pop_front();

                match tmp {
                    None => return,
                    Some(u) => { dispatch(u, &mut node_clone, &mut edge_clone, &adj_clone, &mut excess_mutex_clone); }
                }
            }
        });

        handles.push(handle);
    }

    // Wait for all threads to finish
    for handle in handles {
        handle.join().unwrap();
    }

    println!("f = {}", node[t].lock().unwrap().e);

}
