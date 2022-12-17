package de.hendriklipka.aoc2022.day17;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: hli
 * Date: 17.12.22
 * Time: 14:25
 */
public class Day171
{
    private static final int WIDTH = 7;
    private static final int ROUNDS = 2022;
    private static Tile[] tiles;
    private static List<String> jets;

    private static int currentJet=0;

    private static int topRock=-1;

    public static void main(String[] args)
    {
        // the tile origin is the bottom left, and they grow upwards
        tiles = new Tile[5];
        tiles[0]=new Tile(4,1);
        tiles[0].addRock(0,0);
        tiles[0].addRock(1,0);
        tiles[0].addRock(2,0);
        tiles[0].addRock(3,0);
        tiles[1]=new Tile(3,3);
        tiles[1].addRock(1, 0);
        tiles[1].addRock(1, 1);
        tiles[1].addRock(1, 2);
        tiles[1].addRock(0, 1);
        tiles[1].addRock(2, 1);
        tiles[2]=new Tile(3,3);
        tiles[2].addRock(0, 0);
        tiles[2].addRock(1, 0);
        tiles[2].addRock(2, 0);
        tiles[2].addRock(2, 1);
        tiles[2].addRock(2, 2);
        tiles[3]=new Tile(1,4);
        tiles[3].addRock(0, 0);
        tiles[3].addRock(0, 1);
        tiles[3].addRock(0, 2);
        tiles[3].addRock(0, 3);
        tiles[4]=new Tile(2,2);
        tiles[4].addRock(0, 0);
        tiles[4].addRock(0, 1);
        tiles[4].addRock(1, 0);
        tiles[4].addRock(1, 1);

        try
        {
            jets = AocParseUtils.getLinesAsChars("day17").get(0);
            // the chamber grows upwards, the first element is the bottom
            List<char[]> chamber = new ArrayList<>();
            for (int i = 0; i< ROUNDS; i++)
            {
                Tile tile = getNextTile(i);
                // make sure the chamber has enough rows to fit the tile
                while (chamber.size() < topRock+4+tile.height)
                {
                    final char[] row = new char[7];
                    Arrays.fill(row, ' ');
                    chamber.add(row);
                }
                simulateTile(tile, chamber, topRock+4);
            }
            System.out.println(topRock+1); // these are zero-based
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void simulateTile(Tile tile, List<char[]> chamber, int startRow)
    {
        int row=startRow;
        int col=2;
        while (true)
        {
            if (leftJet())
            {
//                System.out.print("left->");
                // update the column when the tile can move to the left
                col = tile.moveLeft(chamber, row, col);
//                System.out.println(col);
            }
            else
            {
//                System.out.print("right->");
                // update the column when the tile can move to the right
                col = tile.moveRight(chamber, row, col);
//                System.out.println(col);
            }
            // check whether we can still fall one down
            if (tile.canFallTo(chamber, row-1, col))
            {
//                System.out.println("can fall");
                // if so, we move the tile one down and go to the next cycle
                row--;
            }
            else
            {
//                System.out.println("stays");
                // otherwise mark the parts of the rock in the chamber
                tile.placeRock(chamber, row, col);
                // updates the top row of the rocks
                int tileTop=row+tile.height-1; // we are zero-based
                if (tileTop>topRock)
                {
                    topRock=tileTop;
                }
                // and go to the next tile
                break;
            }
        }
//        System.out.println("new top="+topRock);
//        dumpChamber(chamber);
    }

    private static void dumpChamber(List<char[]> chamber)
    {
        int l=chamber.size();
        System.out.println(l);
        for (int i=0;i<l;i++)
        {
            char[] row=chamber.get(l-i-1);
            System.out.print('#');
            for (int x=0;x<WIDTH;x++)
                System.out.print(row[x]);
            System.out.println('#');
        }
        System.out.println("#########");
        System.out.println();
    }

    private static Tile getNextTile(int i)
    {
        return tiles[i%5];
    }

    private static boolean leftJet()
    {
        String jet=jets.get(currentJet);
        currentJet++;
        if (currentJet==jets.size())
            currentJet=0;
        return jet.equals("<");
    }

    private static class Tile
    {
        private final int width;
        private final int height;
        List<Pair<Integer, Integer>> rocks = new ArrayList<>();
        public Tile(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        public void addRock(int x, int y)
        {
            rocks.add(new ImmutablePair<>(x, y));
        }

        public void placeRock(List<char[]> chamber, int row, int col)
        {
            for (Pair<Integer, Integer> rock : rocks)
            {
                int rRow = row + rock.getRight();
                int rCol = col + rock.getLeft();
                chamber.get(rRow)[rCol] = '#';
            }
        }

        public boolean canFallTo(List<char[]> chamber, int row, int col)
        {
            if (row<0)
                return false;
            return fitsTo(chamber, row, col);
        }

        public int moveRight(List<char[]> chamber, int row, int col)
        {
            if (WIDTH == col + width)
                return col;
            if (fitsTo(chamber, row, col + 1))
                return col + 1;
            return col;
        }

        public int moveLeft(List<char[]> chamber, int row, int col)
        {
            if (col==0)
                return 0;
            if (fitsTo(chamber, row, col-1))
                return col-1;
            return col;
        }

        private boolean fitsTo(List<char[]> chamber, int row, int col)
        {
            for (Pair<Integer, Integer> rock: rocks)
            {
                int rRow=row+rock.getRight();
                int rCol=col+rock.getLeft(); // will always fit into the chamber, we have tested that for move left/right
                if (rRow>topRock)
                {
                    continue; // fits
                }
                if (chamber.get(rRow)[rCol]!=' ')
                    return false;
            }
            return true;
        }
    }
}
