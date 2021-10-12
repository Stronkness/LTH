import java.io.*;
import java.util.*;

public class MainProgram {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        String[] parts = line.split(" ");
        int vertices = Integer.parseInt(parts[0]);
        PriorityQueue<Edge> queue = new PriorityQueue<Edge>();

        while(scan.hasNext()){
            String newLine = scan.nextLine();
            String[] node = newLine.split(" ");
            Edge edge = new Edge(Integer.parseInt(node[0]), Integer.parseInt(node[1]), Integer.parseInt(node[2]));
            queue.add(edge);
        }
        System.out.println(kruskal(queue, vertices));
        scan.close();
    }

    public static int kruskal(PriorityQueue<Edge> pq, int vertices){
        int sum = 0;
        int[] parents = new int[vertices];
        while(!pq.isEmpty()){
            Edge edge = pq.poll();
            if(find(edge.target, parents) == find(edge.source, parents)) continue;
            parents[find(edge.target, parents) - 1] = find(edge.source, parents);
            sum += edge.weight;
        }
        return sum;
    }

    //Helper method find for kruskal algorithm, uses pseudocode from book
    public static int find(int value, int[] parents){
        int p = value;
        while(parents[p-1] != 0) p = parents[p-1];
        while(parents[value-1] != 0){
            int w = parents[value-1];
            parents[value-1] = p;
            value = w;
        }
        return p;
    }


    static class Edge implements Comparable<Edge>{
        private int source, target, weight;

        public Edge(int source, int target, int weight){
            this.weight = weight;
            this.source = source;
            this.target = target;
        }

        public int weight(){ return weight; }

        @Override
        public int compareTo(Edge o) {
            return this.weight - o.weight();
        }
    }
}