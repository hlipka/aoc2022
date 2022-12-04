package de.hendriklipka.aoc;

/**
 * User: hli
 * Date: 04.12.22
 * Time: 20:04
 */
public class Range
{
    long from;
    long to;


    public Range(String range)
    {
        String[] ranges = range.split("\\-");
        from = Long.parseLong(ranges[0]);
        to = Long.parseLong(ranges[1]);
    }

    public long getFrom()
    {
        return from;
    }

    public long getTo()
    {
        return to;
    }

    public boolean isInside(final Range other)
    {
        return from>=other.from && to<=other.to;
    }

    @Override
    public String toString()
    {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    public boolean isOverlap(final Range other)
    {
        return (from>=other.from && from<=other.to)||((to >= other.from && to <= other.to));
    }
}
