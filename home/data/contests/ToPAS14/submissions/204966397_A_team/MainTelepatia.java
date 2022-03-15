import java.util.Scanner;

public class MainTelepatia {
	
	
	public static void main(String[] args) {
		Scanner in= new Scanner(System.in);
		Scanner first,second;
		int acertos=0, pontos=0,ant=0;
		int dim=in.nextInt();
		in.nextLine();
		first=new Scanner(in.nextLine());
		second=new Scanner(in.nextLine());
		while (dim>0) {
			if (first.nextInt()==second.nextInt()) {
				acertos++;
				ant++;
			}
			else {
				if (ant==1) pontos++;
				else pontos = pontos + ant*3;
				ant=0;
			}
			dim--;
		}
		if (ant==1) 
			pontos++;
		else 
			pontos = pontos + ant*3;
		System.out.println(acertos);
		System.out.println(pontos);
		first.close();
		second.close();
		in.close();
		
	}
}
