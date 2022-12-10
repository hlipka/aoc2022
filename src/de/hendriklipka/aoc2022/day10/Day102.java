package de.hendriklipka.aoc2022.day10;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 10.12.22
 * Time: 22:33
 */
public class Day102
{
    static int x = 1;
    static int cycle = 0;

    static char[][] screen = new char[6][40];
    public static void main(String[] args)
    {
        try
        {
            List<String> prg = AocParseUtils.getLines("day10");
            for (String cmd: prg)
            {
                execute(cmd);
            }
            for (char[] row: screen)
            {
                for (char c: row)
                {
                    System.out.print(c);
                }
                System.out.println();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void execute(String cmd)
    {
        if (cmd.equals("noop"))
        {
            inc();
        }
        else if (cmd.startsWith("addx"))
        {
            long value = Long.parseLong(cmd.substring(5));
            inc();
            inc();
            x += value;
        }
    }

    private static void inc()
    {
        int drawCol=cycle%40;
        int drawRow=cycle/40;
        final int spriteLeft = x - 1;
        final int sprintRight = x + 1;
        if (drawCol>= spriteLeft && drawCol<= sprintRight)
        {
            screen[drawRow][drawCol]='#';
        }
        else
        {
            screen[drawRow][drawCol] = '.';
        }
        cycle++;
    }
}
