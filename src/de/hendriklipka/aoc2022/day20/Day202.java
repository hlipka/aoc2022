package de.hendriklipka.aoc2022.day20;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Day202
{
    static final long KEY = 811589153;

    static long listLength;

    public static void main(String[] args)
    {
        try
        {
            List<Node> nodes = AocParseUtils.getLines("day20")
                                            .stream().map(Day202::getNode)
                                            .collect(Collectors.toList());
            listLength=nodes.size();
            Node start = new Node(); // see other class for why this should be removed
            Node current = start;
            for (Node node : nodes)
            {
                node.prev = current;
                current.next = node;
                current = node;
            }
            current.next = start;
            start.prev = current;

            System.out.println(nodes);
            dumpNodes(start.next);
            for (long i=0;i<10;i++)
            {
                System.out.println(i);
                for (Node node : nodes)
                {
                    moveNode(node);
                }
                dumpNodes(start.next);
            }
            Node zero = findZero(start.next);
            System.out.println(zero);
            Node one = skipNodes(zero, 1000);
            Node two = skipNodes(one, 1000);
            Node three = skipNodes(two, 1000);
            System.out.println(one.num);
            System.out.println(two.num);
            System.out.println(three.num);
            System.out.println(one.num + two.num + three.num);
            // 570068847 is too low
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Node findZero(Node node)
    {
        while (node.num != 0)
            node = node.next;
        return node;
    }

    private static Node skipNodes(Node node, long num)
    {
        for (long i = 0; i < num; i++)
        {
            node = node.next;
            if (node.start)
                node = node.next;
        }
        return node;
    }

    private static void dumpNodes(Node node)
    {
        while (!node.start)
        {
            System.out.print(node.num + " (" + node.prev.num + "," + node.next.num + ") ");
            node = node.next;
        }
        System.out.println();
    }

    private static void moveNode(Node node)
    {
        System.out.println("move node " + node.num);
        // when we remove the node form the list, listLength-1 is the number of moves until we appear back at the start
        // so we just calculate the remaining number of moves and do them
        long move = Math.abs(node.num)%(listLength-1);
        if (move == 0)
        {
            return;
        }
        if (node.num > 0)
        {
            moveForward(node, move);
        }
        else
        {
            moveBackwards(node, move);
        }
    }

    private static void moveForward(Node node, long move)
    {
        // move the node out of the list first
        Node p = node.prev;
        Node n = node.next;
        p.next = n;
        n.prev = p;
        // move node forward 'num' times
        Node current = node;
        for (long i = 0; i < move; i++)
        {
            current = current.next;
            // skip over the start node
            if (current.start)
                current = current.next;
        }
        // insert node back in, after the current one
        current.next.prev = node; // the one after current now points to us
        node.next = current.next; // we polong to the next node
        current.next = node; // the older pointer now points to us
        node.prev = current; // and we polong to the current one
    }

    private static void moveBackwards(Node node, long move)
    {
        // move the node out of the list first
        Node p = node.prev;
        Node n = node.next;
        p.next = n;
        n.prev = p;
        // move node backwards 'num' times
        Node current = node;
        for (long i = 0; i < move; i++)
        {
            current = current.prev;
            // skip over the start node
            if (current.start)
                current = current.prev;
        }
        // when we moved just before the first element, skip the start element
        if (current.prev.start)
            current = current.prev;
        // insert node back in, before the current one
        current.prev.next = node;
        node.prev = current.prev;
        current.prev = node;
        node.next = current;
    }

    private static Node getNode(String s)
    {
        return new Node(Integer.parseInt(s) * KEY);
    }

    private static class Node
    {
        private boolean start = false;
        private long num;
        private Node prev;
        private Node next;

        public Node(long i)
        {
            num = i;
        }

        public Node()
        {
            start = true;
        }

        @Override
        public String toString()
        {
            if (start)
            {
                return "Node{" +
                        "start" +
                        ", prev=" + prev +
                        ", next=" + next +
                        '}';
            }
            else
            {
                return "Node{" +
                        "num=" + num +
                        ", prev=" + (prev.start ? "start" : prev.num) +
                        ", next=" + (next.start ? "start" : next.num) +
                        '}';
            }
        }
    }

}
