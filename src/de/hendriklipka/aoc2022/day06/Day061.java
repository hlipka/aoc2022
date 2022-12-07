package de.hendriklipka.aoc2022.day06;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;

public class Day061
{
    public static void main(String[] args)
    {
        try
        {
            String line = AocParseUtils.getLines("day06").get(0);
            int pos=0;
            while (pos<line.length()-4)
            {
                String part = line.substring(pos, pos+4);
                if (isMarker(part))
                {
                    break;
                }
                pos++;
            }
            System.out.println(pos+4);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isMarker(String part)
    {
        char[] chars = part.toCharArray();
        return (chars[0]!=chars[1])
                &&(chars[0]!=chars[2])
                &&(chars[0]!=chars[3])
                &&(chars[1]!=chars[2])
                &&(chars[1]!=chars[3])
                &&(chars[2]!=chars[3])
                ;
    }
}
