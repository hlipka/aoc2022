package de.hendriklipka.aoc2022.day20;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Day201
{
    public static void main(String[] args)
    {
        try
        {
            List<Node> nodes = AocParseUtils.getLines("day20")
                                              .stream().map(Day201::getNode)
                                              .collect(Collectors.toList());
            /*
            So actually we don't need the special start node, and the special handling down below when doing the list traversal.
            I misread the task - we count the 1000 from the '0', not from the start, so it does not matter where the
            original start of the list is.
            But I figured it out only afterwards, and then all the logic was working already, and it didn't matter otherwise.
             */
            Node start=new Node();
            Node current=start;
            for (Node node: nodes)
            {
                node.prev=current;
                current.next=node;
                current=node;
            }
            current.next=start;
            start.prev=current;

            System.out.println(nodes);
            dumpNodes(start.next);
            for (Node node: nodes)
            {
                moveNode(node);
//                dumpNodes(start.next);
            }
            Node zero=findZero(start.next);
            System.out.println(zero);
            Node one=skipNodes(zero, 1000);
            Node two=skipNodes(one, 1000);
            Node three=skipNodes(two, 1000);
            System.out.println(one.num);
            System.out.println(two.num);
            System.out.println(three.num);
            System.out.println(one.num+two.num+three.num);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Node findZero(Node node)
    {
        while (node.num!=0)
            node=node.next;
        return node;
    }

    private static Node skipNodes(Node node, int num)
    {
        for (int i=0;i<num;i++)
        {
            node=node.next;
            if (node.start)
                node=node.next;
        }
        return node;
    }

    private static void dumpNodes(Node node)
    {
        while (!node.start)
        {
            System.out.print(node.num+" ("+node.prev.num+","+node.next.num+") ");
            node=node.next;
        }
        System.out.println();
    }

    private static void moveNode(Node node)
    {
        System.out.println("move node "+node.num);
        if (node.num==0){
            return;
        }
        if (node.num>0)
        {
            moveForward(node);
        }
        else
        {
            moveBackwards(node);
        }
    }

    private static void moveForward(Node node)
    {
        // move the node out of the list first
        Node p=node.prev;
        Node n=node.next;
        p.next=n;
        n.prev=p;
        // move node forward 'num' times
        Node current=node;
        for (int i=0;i<node.num;i++)
        {
            current=current.next;
            // skip over the start node
            if (current.start)
                current=current.next;
        }
        // insert node back in, after the current one
        current.next.prev=node; // the one after current now points to us
        node.next=current.next; // we point to the next node
        current.next=node; // the older pointer now points to us
        node.prev=current; // and we point to the current one
    }

    private static void moveBackwards(Node node)
    {
        // move the node out of the list first
        Node p=node.prev;
        Node n=node.next;
        p.next=n;
        n.prev=p;
        // move node backwards 'num' times
        Node current=node;
        for (int i=0;i<-node.num;i++)
        {
            current=current.prev;
            // skip over the start node
            if (current.start)
                current=current.prev;
        }
        if (current.prev.start)
            current=current.prev;
        // insert node back in, before the current one
        current.prev.next=node;
        node.prev=current.prev;
        current.prev=node;
        node.next=current;
    }

    private static Node getNode(String s)
    {
        return new Node(Integer.parseInt(s));
    }

    private static class Node
    {
        private boolean start=false;
        private int num;
        private Node prev;
        private Node next;

        public Node(int i)
        {
            num = i;
        }

        public Node()
        {
            start=true;
        }

        @Override
        public String toString()
        {
            if (start)
            {
                return "Node{" +
                        "start"+
                        ", prev=" + prev +
                        ", next=" + next +
                        '}';
            }
            else
            {
                return "Node{" +
                        "num=" + num +
                        ", prev=" + (prev.start?"start":prev.num) +
                        ", next=" + (next.start?"start":next.num) +
                        '}';
            }
        }
    }

}
