package de.hendriklipka.aoc2022.day13;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 12.12.22
 * Time: 22:22
 */
public class Day132
{
    private static int OPEN=-1;
    private static int CLOSE=-2;

    public static void main(String[] args)
    {
        int result=0;
        try
        {
            List<List<Integer>> packets = AocParseUtils.getLines("day13").stream().map(Day132::parseList).collect(
                    Collectors.toList());
            List<Integer> divider1 = parseList("[[2]]");
            List<Integer> divider2 = parseList("[[6]]");
            packets.add(divider1);
            packets.add(divider2);

            packets.sort(Day132::comparePackets);
            for (List<Integer> packet: packets)
            {
                System.out.println(packet);
            }
            int pos1 = packets.indexOf(divider1)+1;
            int pos2 = packets.indexOf(divider2)+1;
            System.out.println(pos1);
            System.out.println(pos2);
            System.out.println(pos1*pos2);


        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int comparePackets(List<Integer> leftListIn, List<Integer> rightListIn)
    {
        List<Integer> leftList = new ArrayList<>(leftListIn);
        List<Integer> rightList = new ArrayList<>(rightListIn);
        int leftPos=0;
        int rightPos=0;
        while (leftPos<leftList.size() && rightPos<rightList.size())
        {
            int left=leftList.get(leftPos++);
            int right=rightList.get(rightPos++);
            if (left==OPEN && right != OPEN)
            {
                rightList.add(rightPos, CLOSE); // promote right side to list
                rightList.add(rightPos-1, OPEN); // promote right side to list
            }
            else if (left != OPEN && right == OPEN)
            {
                leftList.add(leftPos, CLOSE); // promote left side to list
                leftList.add(leftPos-1, OPEN); // promote left side to list
            }
            if (left != OPEN && left !=CLOSE && right != OPEN & right != CLOSE)
            {
                if (left<right)
                {
                    return -1;
                }
                else if (left > right)
                {
                    return 1;
                }
            }
            else if (left == CLOSE && right != CLOSE)
            {
                return -1;
            }
            else if (left != CLOSE && right == CLOSE)
            {
                return 1;
            }
        }
        final int result = Integer.compare(leftList.size(), rightList.size());
        return result;
    }

    private static List<Integer> parseList(String leftStr)
    {
        List<Integer> result= new ArrayList<>();
        List<String> parts = leftStr.chars().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toList());
        String numStr="";
        for (String part: parts)
        {
            if (part.equals("["))
            {
                if (!numStr.isBlank())
                {
                    result.add(Integer.parseInt(numStr));
                }
                numStr="";
                result.add(OPEN);
            }
            else if (part.equals("]"))
            {
                if (!numStr.isBlank())
                {
                    result.add(Integer.parseInt(numStr));
                }
                numStr = "";
                result.add(CLOSE);
            }
            else if (part.equals(","))
            {
                if (!numStr.isBlank())
                {
                    result.add(Integer.parseInt(numStr));
                }
                numStr = "";
            }
            else
            {
                numStr+=part;
            }
        }
        return result;
    }

}
