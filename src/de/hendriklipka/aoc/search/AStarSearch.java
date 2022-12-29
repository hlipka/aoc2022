package de.hendriklipka.aoc.search;

/**
 * Class for a generic A* search in an array-based world
 */
public class AStarSearch
{
    private final ArrayWorld world;
    private final int endX;
    private final int endY;
    private final int[][] path;

    boolean foundTarget=false;

    public AStarSearch(ArrayWorld world)
    {
        this.world = world;
        endX = world.getEndX();
        endY = world.getEndY();
        path = new int[world.getWidth()][world.getHeight()];
        for (int x = 0; x < world.getWidth(); x++)
        {
            for (int y = 0; y < world.getHeight(); y++)
            {
                path[x][y] = Integer.MAX_VALUE;
            }
        }
    }

    public int findPath()
    {
        doFindPath(world.getStartX(), world.getStartY(), 0);
        return path[endX][endY];
    }

    private void doFindPath(int x, int y, int length)
    {
        int oldLength = path[x][y];
        if (length < oldLength)
        {
            path[x][y] = length;
        }
        else
        {
            return;
        }
        if (x == endX && y == endY)
        {
            foundTarget=true;
            return;
        }

        if (world.canMoveTo(x, y, x - 1, y))
        {
            doFindPath(x - 1, y, length + 1);
        }
        if (world.canMoveTo(x, y, x + 1, y))
        {
            doFindPath(x + 1, y, length + 1);
        }
        if (world.canMoveTo(x, y, x, y - 1))
        {
            doFindPath(x, y - 1, length + 1);
        }
        if (world.canMoveTo(x, y, x, y + 1))
        {
            doFindPath(x, y + 1, length + 1);
        }
    }

    public boolean didFoundTarget()
    {
        return foundTarget;
    }
}
