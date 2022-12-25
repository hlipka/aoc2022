package de.hendriklipka.aoc2022.day22;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day222
{
    private static int width;
    private static int height;
    private static int cubeSize;

    private static final Map<String, Pair<Direction, PosMapper>> cubeMap = new HashMap<>();

    public static void main(String[] args)
    {
        try
        {
            List<List<String>> blocks = AocParseUtils.getStringBlocks("day22");
            List<String> mazeData = blocks.get(0);
            String cmdData = blocks.get(1).get(0);

            // parse the
            width = mazeData.stream().mapToInt(String::length).max().orElseThrow();
            height = mazeData.size();
            // we don't know the shape, but at least we can get the cube size
            // the shape is either 3x4 or 4x3
            cubeSize = Math.min(width, height) / 3;

            char[][] maze = new char[width][height];
            for (int row = 0; row < height; row++)
            {
                String rowData = mazeData.get(row);
                rowData = StringUtils.rightPad(rowData, width, ' ');
                for (int col = 0; col < width; col++)
                {
                    maze[col][row] = rowData.charAt(col);
                }
            }

            LinkedList<String> commands = parseCommands(cmdData);
            // position is zero-based so it matches the maze array
            Position startPos = null;
            for (int col = 0; col < width; col++)
            {
                if (maze[col][0] != ' ')
                {
                    startPos = new Position(0, col);
                    break;
                }
            }
            if (null == startPos)
            {
                throw new IllegalStateException("no start pos found");
            }

            System.out.println("cube size: " + cubeSize);
            // we hardcode the cube structure (or rather: how we move from one side to the other)
            // so we have a map: tile_num+dir->new_dir+pos-mapper
            // tile_num gets calculated from the position, so it ranges from 0-15
            // as col/cube_size + 4*row/cube_size  (and not all are used)
            // we exclude the ones which stay in the maze area without wrapping
            // the mapper calculate the new position when moving from one tile to another
            if (width > height)
            {
                // test data
                cubeMap.put("2U", new ImmutablePair<>(Direction.D,
                        p -> new Position(cubeSize, cubeSize - 1 - (p.col - 2 * cubeSize))));
                cubeMap.put("2L", new ImmutablePair<>(Direction.D,
                        p -> new Position(cubeSize, p.col + cubeSize)));
                cubeMap.put("2R", new ImmutablePair<>(Direction.L,
                        p -> new Position((cubeSize - p.row - 1) + 2 * cubeSize, cubeSize * 4 - 1)));

                cubeMap.put("4U", new ImmutablePair<>(Direction.D,
                        p -> new Position(0, 3 * cubeSize - 1 - p.col)));
                cubeMap.put("4D", new ImmutablePair<>(Direction.U,
                        p -> new Position(3 * cubeSize - 1, 3 * cubeSize - 1 - p.col)));
                cubeMap.put("4L", new ImmutablePair<>(Direction.U,
                        p -> new Position(3 * cubeSize - 1, (2 * cubeSize - p.col - 1) + 3 * cubeSize)));

                cubeMap.put("5U", new ImmutablePair<>(Direction.R,
                        p -> new Position(p.col - cubeSize, 2 * cubeSize)));
                cubeMap.put("5D", new ImmutablePair<>(Direction.R,
                        p -> new Position(p.col - cubeSize - 1 + 2 * cubeSize, 2 * cubeSize)));

                cubeMap.put("6R", new ImmutablePair<>(Direction.D,
                        p -> new Position(2 * cubeSize, 3 * cubeSize + (2 * cubeSize - 1 - p.row))));

                cubeMap.put("10D", new ImmutablePair<>(Direction.U,
                        p -> new Position(2 * cubeSize - 1, (3 * cubeSize - p.col - 1))));
                cubeMap.put("10L", new ImmutablePair<>(Direction.U,
                        p -> new Position(2 * cubeSize - 1, (3 * cubeSize - 1 - p.row) + cubeSize + 1)));

                cubeMap.put("11U", new ImmutablePair<>(Direction.L,
                        p -> new Position((4 * cubeSize - 1 - p.col) + cubeSize + 1, 3 * cubeSize - 1)));
                cubeMap.put("11D", new ImmutablePair<>(Direction.R,
                        p -> new Position((4 * cubeSize - 1 - p.col) + cubeSize + 1, 0)));
                cubeMap.put("11R", new ImmutablePair<>(Direction.L,
                        p -> new Position(3 * cubeSize - 1 - p.row, 3 * cubeSize - 1)));

            }
            else
            {
                // real data has a different shape
                cubeMap.put("1U", new ImmutablePair<>(Direction.R,
                        p -> new Position(p.col + 2 * cubeSize, 0)));
                cubeMap.put("1L", new ImmutablePair<>(Direction.R,
                        p -> new Position(cubeSize - 1 - p.row + 2 * cubeSize, 0)));

                cubeMap.put("2U", new ImmutablePair<>(Direction.U,
                        p -> new Position(cubeSize * 4 - 1, p.col - 2 * cubeSize)));
                cubeMap.put("2D", new ImmutablePair<>(Direction.L,
                        p -> new Position(p.col - cubeSize, cubeSize * 2 - 1)));
                cubeMap.put("2R", new ImmutablePair<>(Direction.L,
                        p -> new Position(cubeSize - 1 - p.row + 2 * cubeSize, cubeSize * 2 - 1)));

                cubeMap.put("5L", new ImmutablePair<>(Direction.D,
                        p -> new Position(cubeSize * 2, p.row - cubeSize)));
                cubeMap.put("5R", new ImmutablePair<>(Direction.U,
                        p -> new Position(cubeSize - 1, p.row + cubeSize)));

                cubeMap.put("8U", new ImmutablePair<>(Direction.R,
                        p -> new Position(p.col + cubeSize, cubeSize)));
                cubeMap.put("8L", new ImmutablePair<>(Direction.R,
                        p -> new Position(3 * cubeSize - 1 - p.row, cubeSize)));

                cubeMap.put("9D", new ImmutablePair<>(Direction.L,
                        p -> new Position( p.col + 2 * cubeSize, cubeSize - 1)));
                cubeMap.put("9R", new ImmutablePair<>(Direction.L,
                        p -> new Position(3 * cubeSize - 1 - p.row, cubeSize * 3 - 1)));

                cubeMap.put("12D", new ImmutablePair<>(Direction.D,
                        p -> new Position(0, p.col + 2 * cubeSize)));
                cubeMap.put("12L", new ImmutablePair<>(Direction.D,
                        p -> new Position(0, p.row - 2 * cubeSize)));
                cubeMap.put("12R", new ImmutablePair<>(Direction.U,
                        p -> new Position(cubeSize * 3 - 1, p.row - 2 * cubeSize)));
            }

            Pair<Direction, Position> current = Pair.of(Direction.R, startPos);

            System.out.println("starting");
            System.out.println(startPos);
            int steps = Integer.parseInt(commands.getFirst());
            commands.removeFirst();
            System.out.println(steps);
            current = takeSteps(maze, current.getRight(), current.getLeft(), steps);
            System.out.println(startPos + " -> " + current.getLeft().name());
            while (!commands.isEmpty())
            {
                System.out.println("--new command--");
                String turn = commands.getFirst();
                System.out.println("turning " + turn);
                current = Pair.of(turn(current.getLeft(), turn), current.getRight());
                System.out.println("new dir=" + current.getLeft().name());
                commands.removeFirst();
                steps = Integer.parseInt(commands.getFirst());
                System.out.println("taking " + steps + " steps");
                commands.removeFirst();
                current = takeSteps(maze, current.getRight(), current.getLeft(), steps);
                maze[current.getRight().col][current.getRight().row] = 'x';
                System.out.println("new state=" + current.getRight() + " -> " + current.getLeft().name());
                System.out.println("result is");
                // dump only the test maze
                if (cubeSize == 4)
                    dumpMaze(maze);
            }

            //139273 is too high
            System.out.println(
                    (current.getRight().row + 1) * 1000 + (current.getRight().col + 1) * 4 + current.getLeft()
                                                                                                    .ordinal());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static LinkedList<String> parseCommands(String cmdData)
    {
        LinkedList<String> commands = new LinkedList<>();

        Pattern numP = Pattern.compile("(\\d+)");
        Pattern dirP = Pattern.compile("([RL]\\d+)");
        Matcher matcher = numP.matcher(cmdData);
        matcher.find();
        String first = matcher.group(1);
        commands.add(first); // first number
        cmdData = cmdData.substring(first.length());
        while (cmdData.length() > 0)
        {
            matcher = dirP.matcher(cmdData);
            matcher.find();
            String group = matcher.group(1);
            commands.add(group.substring(0, 1));
            commands.add(group.substring(1));
            cmdData = cmdData.substring(group.length());
        }
        return commands;
    }

    private static Direction turn(Direction currentDir, String newDir)
    {
        int dir = currentDir.ordinal();
        if (newDir.equals("R"))
        {
            dir++;
            if (dir == 4)
            {
                dir = 0;
            }
        }
        else
        {
            dir--;
            if (dir == -1)
            {
                dir = 3;
            }
        }
        return Direction.values()[dir];
    }

    private static Pair<Direction, Position> takeSteps(char[][] maze, Position currentPos, Direction currentDir,
                                                       int steps)
    {
        for (int step = 0; step < steps; step++)
        {
            Pair<Direction, Position> target = getNewPos(maze, currentPos, currentDir);
            Position targetPos = target.getRight();
            if (maze[targetPos.col][targetPos.row] != '#')
            {
                // when we can move to the new position, we also might change our direction (and only then)
                switch (currentDir)
                {

                    case R:
                        maze[currentPos.col][currentPos.row] = '>';
                        break;
                    case D:
                        maze[currentPos.col][currentPos.row] = 'v';
                        break;
                    case L:
                        maze[currentPos.col][currentPos.row] = '<';
                        break;
                    case U:
                        maze[currentPos.col][currentPos.row] = '^';
                        break;
                }
                currentPos = targetPos;
                currentDir = target.getLeft();
            }
            else
            {
                // as soon as we hit a wall, stop movement and return where we are
                break;
            }
        }
        return Pair.of(currentDir, currentPos);
    }

    private static Pair<Direction, Position> getNewPos(char[][] maze, Position currentPos, Direction currentDir)
    {
        switch (currentDir)
        {
            case R:
                return movePosRight(maze, currentPos, currentDir);
            case D:
                return movePosDown(maze, currentPos, currentDir);
            case L:
                return movePosLeft(maze, currentPos, currentDir);
            case U:
                return movePosUp(maze, currentPos, currentDir);
        }
        throw new IllegalStateException("unknown current dir");
    }

    private static Pair<Direction, Position> movePosRight(char[][] maze, Position currentPos, Direction currentDir)
    {
        int col = currentPos.col;
        int row = currentPos.row;
        // need to be in the field, and the target must not be void
        if (col + 1 < width && maze[col + 1][row] != ' ')
        {
            // direction does not change
            return Pair.of(currentDir, new Position(row, col + 1));
        }
        return mapToNewTile(currentPos, "R");
    }

    private static Pair<Direction, Position> movePosDown(char[][] maze, Position currentPos, Direction currentDir)
    {
        int col = currentPos.col;
        int row = currentPos.row;
        if (row + 1 < height && maze[col][row + 1] != ' ')
        {
            return Pair.of(currentDir, new Position(row + 1, col));
        }
        return mapToNewTile(currentPos, "D");
    }

    private static Pair<Direction, Position> movePosLeft(char[][] maze, Position currentPos, Direction currentDir)
    {
        int col = currentPos.col;
        int row = currentPos.row;
        // need to be in the field, and the target must not be void
        if (col - 1 >= 0 && maze[col - 1][row] != ' ')
        {
            return Pair.of(currentDir, new Position(row, col - 1));
        }
        return mapToNewTile(currentPos, "L");
    }

    private static Pair<Direction, Position> movePosUp(char[][] maze, Position currentPos, Direction currentDir)
    {
        int col = currentPos.col;
        int row = currentPos.row;
        if (row - 1 >= 0 && maze[col][row - 1] != ' ')
        {
            return Pair.of(currentDir, new Position(row - 1, col));
        }
        return mapToNewTile(currentPos, "U");
    }

    // this maps the _border_ position in the old tile to the border in the new tile - so we have already moved, there is no need for calculation afterwards
    private static Pair<Direction, Position> mapToNewTile(Position currentPos, String dirStr)
    {
        int tileNum = getTile(currentPos);
        System.out.println("changing tile, current tile at " + currentPos + " is " + tileNum + ", dir=" + dirStr);
        String key = "" + tileNum + dirStr;
        Pair<Direction, PosMapper> mapper = cubeMap.get(key);

        if (null == mapper)
            throw new IllegalStateException(
                    "did not find other side during wrap-around, pos was " + currentPos + ", key was " + key);

        // we return the new direction, but do not set it
        final Position newPos = mapper.getRight().map(currentPos);
        System.out.println("new position is now "+newPos+", -> "+mapper.getLeft().name());
        return Pair.of(mapper.getLeft(), newPos);
    }

    private static int getTile(Position currentPos)
    {
        return currentPos.col / cubeSize + (currentPos.row / cubeSize) * 4;
    }

    private static void dumpMaze(char[][] maze)
    {
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                System.out.print(maze[col][row]);
            }
            System.out.println();
        }
    }

    private enum Direction
    {
        R, D, L, U
    }

    private static class Position
    {
        int row, col;

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

    private interface PosMapper
    {
        Position map(Position oldPos);
    }
}
