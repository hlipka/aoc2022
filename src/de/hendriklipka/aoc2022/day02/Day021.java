package de.hendriklipka.aoc2022.day02;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

public class Day021
{

    public static void main(String[] args) {
        try {
            List<List<String>> guide = AocParseUtils.getLineWords("day02");
            int score = guide.stream().mapToInt(p->getScore(p)).sum();
            System.out.println(score);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static int getScore(final List<String> p)
    {
        int score=0;
        final String oppMove = p.get(0);
        final String ownMove = p.get(1);
        if ((oppMove.charAt(0)+23) == ownMove.charAt(0))
        {
            score+=3;
        }
        else
        {
            switch (oppMove)
            {
                case "A":
                    if (ownMove.equals("Y"))
                        score += 6;
                    break;
                case "B":
                    if (ownMove.equals("Z"))
                        score += 6;
                    break;
                case "C":
                    if (ownMove.equals("X"))
                        score += 6;
                    break;
            }
        }

        switch(ownMove)
        {
            case "X": score+=1;break;
            case "Y": score+=2;break;
            case "Z": score+=3;break;
        }
        return score;
    }

}
