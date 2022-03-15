import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Margarida Mamede
 */





public class BeansGame
{

    private static int scoreOfAlex( int[] seq )
    {
        int steps = seq.length / 2;
        int left = 0; 
        int right = seq.length - 1; 
        int score = 0;
        boolean minStep = true;

        for ( int i = 0; i < steps; i++ )
        {
            // Alex selection
            if ( seq[left] > seq[right] )
            {
                score += seq[left];
                left++;
            }
            else 
            {
                score += seq[right];
                right--;
            }

            // Bela selection
            if ( minStep )
            {
                if ( seq[left] < seq[right] )
                    left++;
                else
                    right--;
            }
            else 
            {
                if ( seq[left] > seq[right] )
                    left++;
                else
                    right--;
            }
            minStep = !minStep;
        }
        return score;
    }


    public static void main( String[] args ) throws IOException
    {
        BufferedReader input = 
            new BufferedReader( new InputStreamReader(System.in) );

        int seqLen = Integer.parseInt( input.readLine() );
        int[] seq = new int[seqLen];

        String[] tokens = input.readLine().split(" ");
        int sum = 0;
        for ( int i = 0; i < seq.length; i++ )
        {
            int elem = Integer.parseInt( tokens[i] );
            seq[i] = elem;
            sum += elem;
        }

        int scA = scoreOfAlex(seq);
        int scB = sum - scA;

        if ( scA > scB )
            System.out.println("Alex ganha com " + scA + " contra " + scB);
        else if ( scA < scB )
            System.out.println("Bela ganha com " + scB + " contra " + scA);
        else
            System.out.println("Alex e Bela empatam a " + scA);


    }





}
