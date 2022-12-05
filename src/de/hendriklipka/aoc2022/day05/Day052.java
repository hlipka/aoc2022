package de.hendriklipka.aoc2022.day05;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day052
{
    public static void main(String[] args)
    {
        try
        {
            List<List<String>> blocks = AocParseUtils.getStringBlocks("day05");
            List<String> initialStacks = blocks.get(0);
            initialStacks.remove(initialStacks.size()-1);
            int len = initialStacks.stream().mapToInt(String::length).max().orElseThrow();
            int stackCount = (len+1) / 4 ; // the last stack does not have a space after it
            List<List<String>> stacks = parseStacks(initialStacks, stackCount);
            List<Move> moves = blocks.get(1).stream().map(Move::new).collect(Collectors.toList());

            moveCrates(stacks, moves);
            List<String> tops = stacks.stream().map(Day052::getTopCreate).collect(Collectors.toList());
            String message = StringUtils.join(tops,"");
            System.out.println(message);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void moveCrates(List<List<String>> stacks, List<Move> moves)
    {
        for (Move move: moves)
        {
            List<String> fromStack = stacks.get(move.getFrom()-1);
            List<String> toStack = stacks.get(move.getTo()-1);
            List<String> crates = fromStack.subList(fromStack.size()-move.getCount(), fromStack.size());
            toStack.addAll(crates);
            stacks.set(move.getFrom()-1, fromStack.subList(0, fromStack.size()-move.getCount()));
        }
    }

    private static String getTopCreate(List<String> crates)
    {
        return crates.get(crates.size()-1);
    }

    private static List<List<String>> parseStacks(List<String> initialStacks, int stackCount)
    {
        List<List<String>> stacks = new ArrayList<>();
        for (int i=0;i<stackCount;i++)
        {
            stacks.add(new ArrayList<>());
        }
        for(String stackLine:initialStacks)
        {
            for (int i=0;i<stackCount;i++)
            {
                int pos = i*4+1;
                if (pos<stackLine.length())
                {
                    char c = stackLine.charAt(pos);
                    if (c!=' ')
                    {
                        stacks.get(i).add(0, Character.toString(c));
                    }
                }
            }
        }
        return stacks;
    }

    private static class Move
    {
        int from;
        int to;
        int count;

        public Move(String line)
        {
            String[] parts = line.split(" ");
            count = Integer.parseInt(parts[1]);
            from = Integer.parseInt(parts[3]);
            to = Integer.parseInt(parts[5]);
        }

        public int getFrom()
        {
            return from;
        }

        public int getTo()
        {
            return to;
        }

        public int getCount()
        {
            return count;
        }

        @Override
        public String toString()
        {
            return "Move{" +
                    "count=" + count +
                    ", from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
}
