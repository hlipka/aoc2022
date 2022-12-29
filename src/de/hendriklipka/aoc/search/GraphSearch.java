package de.hendriklipka.aoc.search;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * generic implementation for finding the shortest paths in a graph
 * you need to add the nodes and the edges, and then can retrieve the costs, or the actual paths
 * (once retrieved these are cached, so you can get them again for cheap)
 */
public class GraphSearch
{
    Map<String, Node> nodes = new HashMap<>();
    Map<String, Integer> pathCostCache = new HashMap<>();
    Map<String, List<String>> pathCache = new HashMap<>();

    public void addNode(String name)
    {
        nodes.put(name, new Node(name));
    }

    public void addEdge(String from, String to, int cost)
    {
        Node fromNode = nodes.get(from);
        if (null == fromNode)
        {
            throw new IllegalArgumentException("unknown node " + from);
        }
        Node toNode = nodes.get(to);
        if (null == toNode)
        {
            throw new IllegalArgumentException("unknown node " + to);
        }
        fromNode.addEdge(to, cost);
        toNode.addEdge(from, cost);
    }

    public int getPathCost(String from, String to)
    {
        String key = from + "|" + to;
        Integer cost = pathCostCache.get(key);
        if (null != cost)
        {
            return cost;
        }

        for (Node node : nodes.values())
        {
            node.init();
        }
        Node start = nodes.get(from);
        if (null == start)
        {
            throw new IllegalArgumentException("unknown node " + from);
        }
        start.distance = 0;
        PriorityQueue<Node> allNodes = new PriorityQueue<>(nodes.values());
        while (!allNodes.isEmpty())
        {
            Node n = allNodes.poll();
            List<Pair<String, Integer>> edges = n.getEdges();
            for (Pair<String, Integer> edge : edges)
            {
                int newDist = n.distance + edge.getRight();
                Node other = nodes.get(edge.getLeft());
                if (allNodes.contains(other))
                {
                    if (newDist < other.distance)
                    {
                        other.distance = newDist;
                        other.pre = n.getName();
                        // we need to re-insert, since we update the value used for sorting
                        //TODO contains and remove are linear-time, so we need something better (for larger graphs)
                        // (potentially use a set of nodes, and maps for the values)
                        allNodes.remove(other);
                        allNodes.add(other);
                    }
                }
            }
        }
        Node target = nodes.get(to);
        cost = target.distance;
        pathCostCache.put(key, cost);
        List<String> path = new ArrayList<>();
        while (target != start)
        {
            path.add(0, target.name); //TODO: this might be slow with large paths
            target = nodes.get(target.pre);
        }
        path.add(0, from);
        pathCache.put(key, path);
        return cost;
    }

    public List<String> getPath(String from, String to)
    {
        getPathCost(from, to); // ensure we have calculated the path
        String key = from + "|" + to;
        return pathCache.get(key);
    }

    static class Node implements Comparable<Node>
    {
        private final List<Pair<String, Integer>> egdes = new ArrayList<>();
        private final String name;
        private int distance;
        private String pre;

        public Node(String name)
        {
            this.name = name;
        }

        void addEdge(String to, int cost)
        {
            egdes.add(Pair.of(to, cost));
        }

        List<Pair<String, Integer>> getEdges()
        {
            return egdes;
        }

        void init()
        {
            distance = Integer.MAX_VALUE;
            pre = null;
        }

        @Override
        public int compareTo(Node o)
        {
            return Integer.compare(distance, o.distance);
        }

        public String getName()
        {
            return name;
        }
    }
}
