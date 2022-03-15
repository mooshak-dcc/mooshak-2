import java.util.Scanner;
public class b {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		int n = in.nextInt();
		int c = 0;
		
		int[] numeros = new int[n];
		
		for(int i = 0; i<n; i++){
			numeros[i] = in.nextInt();
		}
		
		for(int i = 1; i<n-1; i++){
			if(numeros[i] > numeros[i-1]*2 && numeros[i] > numeros[i+1]*2){
				c++;
			}
		}
		in.close();
		
		System.out.println(c);
	}

}
