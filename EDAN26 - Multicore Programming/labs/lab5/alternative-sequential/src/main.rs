#[macro_use]
extern crate text_io;

use std::collections::{LinkedList, VecDeque};

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
    u_i: usize,
    v_i: usize,
    e_i: usize,
    node: &mut Vec<Node>,
    edge: &mut Vec<Edge>,
    excess: &mut VecDeque<usize>
) {
    let d: i32;

    print!("push from {} to {}: ", u_i, v_i);
    print!("f = {}, c = {}, so ", edge[e_i].f, edge[e_i].c);

    if u_i == edge[e_i].u {
        d = node[u_i].e.min(edge[e_i].c - edge[e_i].f);
        edge[e_i].f += d;
    } else {
        d = node[u_i].e.min(edge[e_i].c + edge[e_i].f);
        edge[e_i].f -= d;
    }
    println!("pushing, {}", d);

    node[u_i].e -= d;
    node[v_i].e += d;

    // These are always true
    assert!(d >= 0);
    assert!(node[u_i].e >= 0);
    assert!(edge[e_i].f.abs() <= edge[e_i].c);

    if node[u_i].e > 0 {
        excess.push_back(u_i);
    }
    if node[v_i].e == d {
        excess.push_back(v_i);
    }
}

fn relabel(u_i: usize, node: &mut Vec<Node>, excess: &mut VecDeque<usize>) {
    node[u_i].h += 1;
    excess.push_back(u_i);
}

fn dispatch(
    u_i: usize,
    node: &mut Vec<Node>,
    edge: &mut Vec<Edge>,
    adj: &Vec<LinkedList<usize>>,
    excess: &mut VecDeque<usize>) {

        let mut b: i32;

        // Either source or sink
        if u_i == 0 || u_i == node.len() - 1 {
            return;
        }

        let p = adj[u_i].iter();
        let u = &node[u_i];

        print!("selected u = {} with ", u_i);
        print!("h = {} and e = {}\n", node[u_i].h, node[u_i].e);    
        
        for &e_i in p {
            let e = &edge[e_i];
            let v_i = if u_i == e.u { e.v } else { e.u };
            let v = &node[v_i];
            b = if u_i == e.u { 1 } else { -1 };


            if u.h > v.h && b * e.f < e.c {
                push(u_i, v_i, e_i, node, edge, excess);
                return
            }
        }

        relabel(u_i, node, excess);
}

fn main() {
    let n: usize = read!();
    let m: usize = read!();
    let _c: usize = read!();
    let _p: usize = read!();
    let mut node = vec![];
    let mut edge = vec![];
    let mut adj: Vec<LinkedList<usize>> = Vec::with_capacity(n);
    let mut excess: VecDeque<usize> = VecDeque::new();
    let debug = false;

    let s = 0;
    let t = n - 1;

    println!("n = {}", n);
    println!("m = {}", m);

    for i in 0..n {
        let u: Node = Node::new(i);
        node.push(u);
        adj.push(LinkedList::new());
    }

    for i in 0..m {
        let u: usize = read!();
        let v: usize = read!();
        let c: i32 = read!();
        let e: Edge = Edge::new(u, v, c);
        adj[u].push_back(i);
        adj[v].push_back(i);
        edge.push(e);
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
    node[s].h = n as i32;

    println!("initial pushes from {}", node[s].i);
    let iter = adj[s].iter();

    for &e_i in iter {
        let e = &mut edge[e_i];
        let v_i = if s == e.u { e.v } else { e.u };
        let v = &mut node[v_i];

        v.e += e.c;
        e.f = e.c;
        excess.push_back(v_i);
    }

    while !excess.is_empty() {
        let u = excess.pop_front().unwrap();

        dispatch(u, &mut node, &mut edge, &adj, &mut excess);
    }

    println!("f = {}", node[t].e);

}
