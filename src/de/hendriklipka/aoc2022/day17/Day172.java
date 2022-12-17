package de.hendriklipka.aoc2022.day17;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IDEA: whenever we placed a rock, we check the last N rows whether they form a continuous line (closing off the top)
 * if so, we can remove all but the top 10 rows (and just track the offset between the list and the actual row numbers)
 *
 * NOTE: this is too slow, it takes about 4.5 days to complete. See Day172b for a better solution
 */
public class Day172
{
    private static final int WIDTH = 7;
//    private static final long ROUNDS = 2022L;
    private static final long ROUNDS = 1000000000000L;
    private static Tile[] tiles;
    private static List<String> jets;

    private static int currentJet=0;

    private static long topRock=-1L;

    private static long currentOffset = 0;

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

        long start = System.currentTimeMillis();
        try
        {
            jets = AocParseUtils.getLinesAsChars("day17").get(0);
            // the chamber grows upwards, the first element is the bottom
            List<char[]> chamber = new ArrayList<>();
            for (long i = 0; i< ROUNDS; i++)
            {
                if (i>0 && 0==(i%100000000))
                {
                    System.out.println(i+" - "+chamber.size()+" (r)");
                    long now = System.currentTimeMillis();
                    long diff=now-start;
                    System.out.println("time done="+diff/1000+"s");

                    System.out.println("time left="+ ((ROUNDS - i) / i) * diff/1000+"s");
//                    dumpChamberTop(chamber);
                }
                Tile tile = getNextTile(i);
                // make sure the chamber has enough rows to fit the tile
                while ((long)chamber.size() < (topRock+4L+tile.height-currentOffset))
                {
                    final char[] row = new char[7];
                    Arrays.fill(row, ' ');
                    chamber.add(row);
                }
                simulateTile(tile, chamber, topRock+4L);
                final int size = chamber.size();
                if ( size >100000 && isSolidTop(chamber))
                {
                    // we have a solid top somewhere in the top 20 rows, so we keep just them
                    ArrayList<char[]> newChamber = new ArrayList<>(110000);
                    newChamber.addAll(chamber.subList(size-20, size));
                    chamber=newChamber;
                    currentOffset += size - 20;
                }
            }
            System.out.println(topRock+1); // these are zero-based
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * check for a solid top
     * We check four once at once. At least one needs to have a rock in it. The worst case is when this alternates
     * between the top-most and bottom-most rows. This leaves a gap of two rows. But neither the 2x2 not the 1x4 piece
     * can run side-ways fast enough to fit into such a hole, so we can treat this as solid.
     */
    private static boolean isSolidTop(List<char[]> chamber)
    {
        int size=chamber.size();
        for (int row=0; row<10; row++)
        {
            int cRow = size-15+row;
            char[] rowUpTwo=chamber.get(cRow+1);
            char[] rowUp=chamber.get(cRow+1);
            char[] rowCurrent=chamber.get(cRow);
            char[] rowDown=chamber.get(cRow-1);
            boolean isSolid=true;
            for (int x=0;x<WIDTH;x++)
            {
                if (rowUpTwo[x]==' ' && rowUp[x] == ' ' && rowCurrent[x]==' ' && rowDown[x]==' ')
                {
                    isSolid= false;
                    break;
                }
            }
            if (isSolid)
            {
//                System.out.println("solid top found at "+cRow);
                return true;
            }
        }
        return false;
    }

    private static void simulateTile(Tile tile, List<char[]> chamber, long startRow)
    {
        long row=startRow;
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
                long tileTop=row+tile.height-1; // we are zero-based
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

    private static void dumpChamberTop(List<char[]> chamber)
    {
        int l = chamber.size();
        System.out.println("size="+l);
        System.out.println("top rock="+topRock);
        for (int i = 0; i < 100; i++)
        {
            char[] row = chamber.get(l - i - 1);
            System.out.print('#');
            for (int x = 0; x < WIDTH; x++)
                System.out.print(row[x]);
            System.out.println('#');
        }
        System.out.println("---------");
        System.out.println();
    }


    private static Tile getNextTile(long i)
    {
        return tiles[(int)(i%5)];
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

        public void placeRock(List<char[]> chamber, long row, int col)
        {
            for (Pair<Integer, Integer> rock : rocks)
            {
                long rRow = row + rock.getRight();
                int rCol = col + rock.getLeft();
                chamber.get((int)(rRow-currentOffset))[rCol] = '#';
            }
        }

        public boolean canFallTo(List<char[]> chamber, long row, int col)
        {
            if (row<0)
                return false;
            return fitsTo(chamber, row, col);
        }

        public int moveRight(List<char[]> chamber, long row, int col)
        {
            if (WIDTH == col + width)
                return col;
            if (fitsTo(chamber, row, col + 1))
                return col + 1;
            return col;
        }

        public int moveLeft(List<char[]> chamber, long row, int col)
        {
            if (col==0)
                return 0;
            if (fitsTo(chamber, row, col-1))
                return col-1;
            return col;
        }

        private boolean fitsTo(List<char[]> chamber, long row, int col)
        {
            for (Pair<Integer, Integer> rock: rocks)
            {
                long rRow=row+rock.getRight();
                int rCol=col+rock.getLeft(); // will always fit into the chamber, we have tested that for move left/right
                if (rRow>topRock)
                {
                    continue; // fits
                }
                if (chamber.get((int)(rRow-currentOffset))[rCol]!=' ')
                    return false;
            }
            return true;
        }
    }
}
