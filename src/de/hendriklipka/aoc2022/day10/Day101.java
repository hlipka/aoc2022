package de.hendriklipka.aoc2022.day10;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 10.12.22
 * Time: 22:33
 */
public class Day101
{
    static long x = 1;
    static long cycle = 1;

    static long result = 0;
    public static void main(String[] args)
    {
        try
        {
            List<String> prg = AocParseUtils.getLines("day10");
            for (String cmd: prg)
            {
                execute(cmd);
            }
            System.out.println(result);
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
            x += value;
            inc();
        }
    }

    private static void inc()
    {
        cycle++;
        if (((cycle-20)%40) == 0)
        {
            final long strength = cycle * x;
            System.out.println(cycle+" - " + strength + " - "+x);
            result += strength;
        }
    }
}
