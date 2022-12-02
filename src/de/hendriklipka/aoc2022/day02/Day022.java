package de.hendriklipka.aoc2022.day02;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

public class Day022
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
        final char oppMove = p.get(0).charAt(0);
        final char ownMove = getOwnMove(oppMove, p.get(1).charAt(0));
        if ((oppMove+23) == ownMove)
        {
            score+=3;
        }
        else
        {
            switch (oppMove)
            {
                case 'A':
                    if (ownMove==('Y'))
                        score += 6;
                    break;
                case 'B':
                    if (ownMove==('Z'))
                        score += 6;
                    break;
                case 'C':
                    if (ownMove==('X'))
                        score += 6;
                    break;
            }
        }

        switch(ownMove)
        {
            case 'X': score+=1;break;
            case 'Y': score+=2;break;
            case 'Z': score+=3;break;
        }
        return score;
    }

    private static char getOwnMove(final char move, final char result)
    {
        switch (result)
        {
            case 'X':
                if (move=='A')
                    return 'Z';
                return (char) (move + 22);
            case 'Y': return (char)(move+23);
            case 'Z':
                if (move == 'C')
                    return 'X';
                return (char) (move + 24);
        }
        return ' ';
    }
}
