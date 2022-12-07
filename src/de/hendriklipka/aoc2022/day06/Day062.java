package de.hendriklipka.aoc2022.day06;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;

public class Day062
{
    public static void main(String[] args)
    {
        try
        {
            String line = AocParseUtils.getLines("day06").get(0);
            int pos=0;
            while (pos<line.length()-14)
            {
                String part = line.substring(pos, pos+14);
                if (isMarker(part))
                {
                    break;
                }
                pos++;
            }
            System.out.println(pos+14);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isMarker(String part)
    {
        char[] chars = part.toCharArray();
        for (int i=0;i<chars.length-1;i++)
        {
            for (int j=i+1;j<chars.length;j++)
            {
                if (chars[i]==chars[j])
                    return false;
            }
        }
        return true;
    }
}
