
import java.util.Scanner;


public class Avintes {
    static Scanner in = new Scanner(System.in);


    public static void main(String args[]) {
        int n = in.nextInt();
        int k;
        String words[] = new String[n];

        for(int c=0; c<n; c++)
            words[c] = in.next();

        while(true) {
            switch(in.nextInt()){
            case 0:
                System.exit(0);
            case 1:
            	k = in.nextInt();
            	for(int c=0; c<k; c++) {
            		String word = in.next();
            		if(c>0)
            			System.out.print(" ");
            		for(int i=0; i<n; i++)
            			if(word.equals(words[i]))
            				System.out.print(i+1);
            	}
            	System.out.println("");
                break;
            case 2:
            	k = in.nextInt();
            	for(int c=0; c<k; c++) {
            		int i = in.nextInt();
            		if(c>0)
            			System.out.print(" ");
            		 System.out.print(words[i-1]);
            	}
            	System.out.println("");
                break; 
            }
        }
    }
} 