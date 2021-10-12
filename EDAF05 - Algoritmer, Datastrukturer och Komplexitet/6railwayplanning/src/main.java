import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int N = scan.nextInt(); //Nodes
        int M = scan.nextInt(); //Edges
        int C = scan.nextInt(); //Students
        int P = scan.nextInt(); //Routes

        Node[] nodes = new Node[N]; //Node array with amount of nodes as size
        //Create nodes in node with the size of N
        for(int i = 0; i < N; i++) {
            nodes[i] = new Node(i);
        }

        /* M is for the amount of edges and 3 is final because that the
        input is always 3 ints long in the columns. Inserts into this matrix
        as the input is formed: Node, Node, Capacity*/
        int[][] routes = new int[M][3];
        //Scans the three ints that where u,v are nodes and c is the capacity (maximum flow)
        for(int i = 0; i < M; i++) {
            int u = scan.nextInt(), v = scan.nextInt(), c = scan.nextInt();
            routes[i][0]= u;
            routes[i][1]= v;
            routes[i][2]= c;
            //Creates an edge with the maximum flow and flow = 0 as it has not been accessed yet
            Edge edge = new Edge(c,0);
            /* Inserts the u and v and sets them as neighbours,
               this is done both for u -> v and v -> u as the graph is undirected */
            nodes[u].neighbour.put(nodes[v], edge);
            nodes[v].neighbour.put(nodes[u], edge);
        }

        //Creates an array with size of amount of routes, inserts the routes to be deleted
        int[] routeDelete = new int[P];
        for(int i = 0; i < P; i++) {
            routeDelete[i] = scan.nextInt();
            int[] route = routes[routeDelete[i]];
            /* Erases the routes to be deleted in both directions */
            nodes[route[0]].neighbour.remove(nodes[route[1]]);
            nodes[route[1]].neighbour.remove(nodes[route[0]]);
        }

        //Printer for the output of the validator files
        System.out.println(flow(routeDelete, routes, nodes, N, C));

    }

    /* This method of BFS follows the pseudo code quite closely, with some modifications to get the flow */
    public static int bfs(Node[] nodes, int N){
        int start = 0;
        int end = N - 1; //Condition from lab instruction

        //Array to check if a node has been visited from 0 -> N
        boolean[] visited = new boolean[nodes.length];
        visited[start] = true;

        //Queue for the nodes to be checked
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(nodes[start]);

        //Checks every node in the queue until it is empty
        while(!queue.isEmpty()){
            Node node = queue.poll();

            //Goes through every neighbour to the current node we check above
            for (Node nodeNeighbour : node.neighbour.keySet()) {
                Edge edge = node.neighbour.get(nodeNeighbour); //Retrieves the edge of the current node
                /* Checks if the current neighbour node is visited or not, and also if the max capacity of the edge
                 is bigger than the current flow of the edge, this is because of that if a edge capacity is equals or
                 flow is bigger then the edges capacity has been used 100%, and we must choose another node */
                if(!visited[nodeNeighbour.index] && edge.capacity > edge.flow){
                    visited[nodeNeighbour.index] = true;
                    queue.add(nodeNeighbour);
                    //We add predecessors because of the backtracking in the flow algorithm
                    nodeNeighbour.setPredecessor(node);

                    //When the current neighbour node is at the end we do the flow calculation for best flow route
                    if(nodeNeighbour.index == end){
                        int delta = Integer.MAX_VALUE;
                        return bfsFlow(nodeNeighbour, start, end, delta, nodes);
                    }
                }
            }
        }
        return 0; //If not 0 then the program will go in an infinite loop
    }

    /* Ford-Fulkerson algorithm part 2 where the lowest delta is decided and updates the flow for all edges */
    public static int bfsFlow(Node nodeNeighbour, int start, int end, int delta, Node[] nodes){
        /* This while-loop goes backwards from end to start and tries to fins the minimum flow of the current route */
        while(nodeNeighbour.index != start){
            //Retrieves the edge of the current neighbour node to be checked
            Edge temp = nodeNeighbour.predecessor.neighbour.get(nodeNeighbour);
            //Checks for the lowest flow of them
            if (temp.capacity - temp.flow < delta){
                delta = temp.capacity - temp.flow;
            }
            //Goes back one step towards the start to determine the lowest flow in this route
            nodeNeighbour = nodeNeighbour.predecessor;
        }

       /* In this segment we do the same as above but updates the flow for the nodes in this route */
        nodeNeighbour = nodes[end];
        while(nodeNeighbour.index != start){
            Edge temp = nodeNeighbour.predecessor.neighbour.get(nodeNeighbour);
            temp.setFlow(temp.flow + delta);
            nodeNeighbour = nodeNeighbour.predecessor;
        }

        //Return the lowest flow value for the route
        return delta;
    }

    /* Ford-Fulkerson algorithm part 1, determines the maximum flow for the input */
    public static String flow(int[] routeDelete, int[][] routes, Node[] nodes, int N, int C){
        int flow = 0;
        //Goes backwards with the flow, forward didn't work
        for (int j = routeDelete.length - 1; j >= 0; j--) {
            //Takes the current route from the matrix of routes where the route deleted is
            int[] route = routes[routeDelete[j]];
            /* Goes backwards through the routes until the maximum flow where flow >= C is met,
               adds the routes again which was deleted until the condition is met (flow, C) */
            nodes[route[0]].neighbour.put(nodes[route[1]], new Edge(route[2],0));
            nodes[route[1]].neighbour.put(nodes[route[0]], new Edge(route[2],0));
            int flowPart = Integer.MIN_VALUE;

            //Checks with bfs for the lowest flow of the current route, when diff is 0 then the bfs is done and adds it to flow
            while(flowPart != 0){
                flowPart = bfs(nodes,N);
                flow += flowPart;
            }

            //Requirement from the lab
            if (flow >= C){
                return (j + " " + flow);
            }
        }
        return "Couldn't find a flow below the requirement!";
    }

    static class Edge {
        private final int capacity;
        private int flow;

        public Edge(int capacity, int flow){
            this.capacity = capacity;
            this.flow = flow;
        }

        public void setFlow(int newFlow){
            flow = newFlow;
        }
    }


    static class Node{
        private final HashMap<Node, Edge> neighbour;
        private final int index;
        private Node predecessor;

        public Node(int index){
            this.index = index;
            neighbour = new HashMap<Node, Edge>();
        }

        public void setPredecessor(Node node) {
            predecessor = node;
        }
    }
}