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
public class Day131
{
    private static int OPEN=-1;
    private static int CLOSE=-2;

    public static void main(String[] args)
    {
        int result=0;
        try
        {
            List<List<String>> pairs = AocParseUtils.getStringBlocks("day13");
            for (int i=0;i<pairs.size();i++)
            {
                if (inRightOrder(pairs.get(i).get(0), pairs.get(i).get(1)))
                {
                    System.out.println("match: "+(i+1));
                    result+=(i+1);
                }
            }
            System.out.println(result);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean inRightOrder(String leftStr, String rightStr)
    {
        List<Integer> leftList = parseList(leftStr);
        List<Integer> rightList = parseList(rightStr);
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
                    return true;
                }
                else if (left > right)
                {
                    return false;
                }
            }
            else if (left == CLOSE && right != CLOSE)
            {
                return true;
            }
            else if (left != CLOSE && right == CLOSE)
            {
                return false;
            }
        }
        return false;
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
        System.out.println(result);
        return result;
    }

}
