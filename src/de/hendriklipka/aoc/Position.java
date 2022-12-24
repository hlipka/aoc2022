package de.hendriklipka.aoc;

import de.hendriklipka.aoc2022.day24.Day242;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * User: hli
 * Date: 25.12.22
 * Time: 18:36
 */
public class Position
{
    public int row, col;

    public Position(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Position pos = (Position) o;

        return new EqualsBuilder().append(row, pos.row).append(col, pos.col).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(row).append(col).toHashCode();
    }

    @Override
    public String toString()
    {
        return "Pos{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
