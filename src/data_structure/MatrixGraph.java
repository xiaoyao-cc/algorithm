package data_structure;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author 陈杨志
 * @version 2025
 * @title MatrixGraph
 * @description 邻接矩阵存储图和遍历图
 * @date 2025年12月19日  11:51
 */
public class MatrixGraph {
    //顶点数
    private final int V;
    //邻接矩阵
    private final int[][] matrix;

    public MatrixGraph(int V) {
        this.V = V;
        matrix = new int[V][V];
    }

    public void addEdge(int src,int dest){
        matrix[src][dest] = 1;
        matrix[dest][src] = 1;
    }

    public void dfs(int start){
        boolean[] visited = new boolean[V];
        dfsService(start,visited);
    }

    private void dfsService(int node,boolean[] visited){
        //先标记为已访问
        visited[node] = true;
        //接下来就是可以对节点进行一些操作，这里就是简单打印一下
        System.out.println("访问了节点: "+ node);
        //递归继续遍历节点的相邻节点
        for (int i = 0; i < V; i++) {
            if (matrix[node][i] == 1 && !visited[i]) {
                dfsService(i,visited);
            }
        }
    }

    public void bfs(int start){
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();
        //先标记为已访问
        visited[start] = true;
        //添加起始节点
        queue.add(start);
        while (!queue.isEmpty()){
            Integer poll = queue.poll();
            //获取到这个节点后可以进行一些其他操作,这里就简单打印一下
            System.out.println("访问了节点："+poll);
            for (int i = 0; i < V; i++) {
                if (matrix[poll][i] == 1 && !visited[i]) {
                    queue.add(i);
                    visited[i] = true;
                }
            }
        }
    }
}
