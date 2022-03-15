import java.util.Scanner;

public class MainTelepatia {
	
	
	public static void main(String[] args) {
		Scanner in= new Scanner(System.in);
		int acertos=0, pontos=0,ant=0;
		int dim=in.nextInt();
		in.nextLine();
		while (true) {
			dim--;
if (dim > 10000000) break;
		}
		if (ant==1) 
			pontos++;
		else 
			pontos = pontos + ant*3;
		System.out.println(acertos);
		System.out.println(pontos);
		in.close();
		
	}
}
