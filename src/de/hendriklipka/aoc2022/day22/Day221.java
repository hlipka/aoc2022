package de.hendriklipka.aoc2022.day22;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day221
{
    private static int width;
    private static int height;

    public static void main(String[] args)
    {
        try
        {
            List<List<String>> blocks = AocParseUtils.getStringBlocks("day22");
            List<String> mazeData = blocks.get(0);
            String cmdData = blocks.get(1).get(0);

            // parse the
            width = mazeData.stream().mapToInt(String::length).max().orElseThrow();
            height =mazeData.size();
            char[][] maze=new char[width][height];
            for (int row = 0; row< height; row++)
            {
                String rowData=mazeData.get(row);
                rowData = StringUtils.rightPad(rowData, width, ' ');
                for (int col=0;col<width;col++)
                {
                    maze[col][row]=rowData.charAt(col);
                }
            }
            dumpMaze(maze);

            System.out.println(cmdData);
            LinkedList<String> commands = new LinkedList<>();

            Pattern numP=Pattern.compile("(\\d+)");
            Pattern dirP=Pattern.compile("([RL]\\d+)");
            Matcher matcher = numP.matcher(cmdData);
            matcher.find();
            String first = matcher.group(1);
            commands.add(first); // first number
            cmdData = cmdData.substring(first.length());
            while (cmdData.length()>0)
            {
                matcher = dirP.matcher(cmdData);
                matcher.find();
                String group = matcher.group(1);
                commands.add(group.substring(0,1));
                commands.add(group.substring(1));
                cmdData = cmdData.substring(group.length());
            }
            Direction currentDir= Direction.R;
            // position is zero-based so it matches the maze array
            Position currentPos=new Position(0,0);
            for (int col=0;col<width;col++)
            {
                if (maze[col][0]!=' ')
                {
                    currentPos = new Position(0, col);
                    break;
                }
            }

            int steps=Integer.parseInt(commands.getFirst());
            commands.removeFirst();
            currentPos = takeSteps(maze, currentPos, currentDir, steps);
            while (!commands.isEmpty())
            {
                String turn = commands.getFirst();
                System.out.println("turning "+turn);
                currentDir = turn(currentDir, turn);
                System.out.println("dir="+currentDir.name());
                commands.removeFirst();
                steps=Integer.parseInt(commands.getFirst());
                System.out.println(steps);
                commands.removeFirst();
                currentPos = takeSteps(maze, currentPos, currentDir, steps);
                System.out.println(currentPos);
            }

            System.out.println((currentPos.row+1)*1000+(currentPos.col+1)*4+currentDir.ordinal());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Direction turn(Direction currentDir, String newDir)
    {
        int dir=currentDir.ordinal();
        if (newDir.equals("R"))
        {
            dir++;
            if (dir==4)
            {
                dir=0;
            }
        }
        else
        {
            dir--;
            if (dir==-1)
            {
                dir=3;
            }
        }
        return Direction.values()[dir];
    }

    private static Position takeSteps(char[][] maze, Position currentPos, Direction currentDir, int steps)
    {
        for (int step=0;step<steps;step++)
        {
            Position target=getNewPos(maze, currentPos, currentDir);
            if (maze[target.col][target.row]!='#')
            {
                currentPos=target;
            }
            else
            {
                // as soon as we hit a wall, stop movement and return where we are
                break;
            }
        }
        return currentPos;
    }

    private static Position getNewPos(char[][] maze, Position currentPos, Direction currentDir)
    {
        switch (currentDir)
        {

            case R:
                return movePosRight(maze, currentPos);
            case D:
                return movePosDown(maze, currentPos);
            case L:
                return movePosLeft(maze, currentPos);
            case U:
                return movePosUp(maze, currentPos);
        }
        throw new IllegalStateException("unknown current dir");
    }

    private static Position movePosRight(char[][] maze, Position currentPos)
    {
        int col=currentPos.col;
        int row = currentPos.row;
        // need to be in the field, and the target must not be void
        if (col+1<width && maze[col+1][row]!=' ')
        {
            return new Position(row, col+1);
        }
        // wrap around, so we start from the left and find the first non-empty field
        for (int i=0;i<col;i++)
        {
            if (maze[i][row]!=' ')
            {
                return new Position(row, i);
            }
        }
        throw new IllegalStateException("did not find other side during wrap-around");
    }

    private static Position movePosDown(char[][] maze, Position currentPos)
    {
        int col=currentPos.col;
        int row = currentPos.row;
        if (row+1<height && maze[col][row+1]!=' ')
        {
            return new Position(row+1, col);
        }
        // wrap around, so we start from the top and find the first non-empty field
        for (int i=0;i<row;i++)
        {
            if (maze[col][i]!=' ')
            {
                return new Position(i, col);
            }
        }
        throw new IllegalStateException("did not find other side during wrap-around");
    }

    private static Position movePosLeft(char[][] maze, Position currentPos)
    {
        int col=currentPos.col;
        int row = currentPos.row;
        // need to be in the field, and the target must not be void
        if (col-1>=0 && maze[col-1][row]!=' ')
        {
            return new Position(row, col-1);
        }
        // wrap around, so we start from the right and find the first non-empty field
        for (int i=width-1;i>col;i--)
        {
            if (maze[i][row]!=' ')
            {
                return new Position(row, i);
            }
        }
        throw new IllegalStateException("did not find other side during wrap-around");
    }

    private static Position movePosUp(char[][] maze, Position currentPos)
    {
        int col=currentPos.col;
        int row = currentPos.row;
        if (row-1>=0 && maze[col][row-1]!=' ')
        {
            return new Position(row-1, col);
        }
        // wrap around, so we start from the top and find the first non-empty field
        for (int i=height-1;i>row;i--)
        {
            if (maze[col][i]!=' ')
            {
                return new Position(i, col);
            }
        }
        throw new IllegalStateException("did not find other side during wrap-around");
    }

    private static void dumpMaze(char[][] maze)
    {
        for (int row = 0; row< height; row++)
        {
            for (int col=0;col<width;col++)
            {
                System.out.print(maze[col][row]);
            }
            System.out.println();
        }
    }

    private enum Direction
    {
        R,D,L,U;
    }

    private static class Position
    {
        int row,col;

        public Position(int row, int col)
        {
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString()
        {
            return "Position{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }
    }
}
