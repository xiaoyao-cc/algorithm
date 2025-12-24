package main;

import data_structure.GraphTraversal;

public class AdjacencyListMain {


    public static void main(String[] args) {
        GraphTraversal graph = new GraphTraversal(5);
        graph.addEdge(0,1);
        graph.addEdge(0,2);
        graph.addEdge(1,2);
        graph.addEdge(2,3);
        graph.addEdge(3,4);
        graph.addEdge(4,1);
        System.out.println("深度优先遍历：");
        graph.dfs(0);
        System.out.println("广度优先遍历：");
        graph.bfs(0);
    }
}
