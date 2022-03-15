import tictactoe.wrappers.java.TicTacToePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test Compile Time Error TicTacToe player in Java
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TTTBotCTE extends TicTacToePlayer {

    @Override
    public String getName() {
        return "Check CTE SA";
    }

    @Override
    public void init() {
    }

    @Override
    public void execute() {
        play(generateMove());
    }

    /**
     * Generate a position to play the piece
     *
     * @return position to play the piece
     */
    int generateMove() {
        List<Integer> possibleMoves = new ArrayList<Integer>();

        for (int i = 1; i <= 9; i++)
            if (isFree(i))
                possibleMoves.add(i)

        if (possibleMoves.isEmpty())
            return 0;

        return 1;
    }
}
