import java.util.Scanner;

public class MainTelepatia {
	
	
	public static void main(String[] args) {
		Scanner in= new Scanner(System.in);
		int acertos=0, pontos=0,ant=0;
		int dim=in.nextInt();
		in.nextLine();
		while (true) {
			int a=0;
			while (true) {
				a--;
				if (a > 10000000) {
					break;
				}
			}
			dim--;
			if (dim > 10000000) {
				break;
			}
		}
		System.out.println(acertos);
		System.out.println(pontos);
		in.close();
		
	}
}




