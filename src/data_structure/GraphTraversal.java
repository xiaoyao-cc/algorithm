package data_structure;

import java.util.*;

/**
 * @title GraphTraversal
 * @description 邻接表遍历图
 * @author 陈杨志
 * @version 2025
 * @date 2025年12月19日  9:22
 */
public class GraphTraversal {
    // 顶点数
    private final int V;
    // 邻接表
    private final Map<Integer,List<Integer>> adj;

    
    public GraphTraversal(int V) {
        this.V = V;
        //初始化生成一个邻接表
        adj = new HashMap<>();
        for(int i = 0; i < V; i++){
            List<Integer> List = new ArrayList<>();
            adj.put(i,List);
        }
    }

    public void addEdge(int src,int dest){
       adj.get(src).add(dest);
       adj.get(dest).add(src);
    }

    //深度优先遍历
    public void dfs(int start){
        boolean[] visited = new boolean[V];
        dfsService(start,visited);
    }

    private void dfsService(int node,boolean[] visited){
        //先标记为已访问
        visited[node] = true;
        //接下来就是可以对节点进行一些操作，这里就是简单打印一下
        System.out.println("访问了节点："+node);
        //递归继续遍历节点的相邻节点
        for (Integer i : adj.get(node)) {
            if(!visited[i]){
                dfsService(i,visited);
            }
        }
    }

    //广度优先遍历
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
            for (Integer i : adj.get(poll)) {
                if(!visited[i]){
                    queue.add(i);
                    visited[i] = true;
                }
            }
        }
    }
}
