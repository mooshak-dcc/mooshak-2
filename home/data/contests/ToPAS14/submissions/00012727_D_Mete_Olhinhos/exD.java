import java.util.ArrayList;
import java.util.Scanner;


public class exD {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		ArrayList<Integer> vet=new ArrayList<Integer>();
		int tempo,n;
		
		for (int i = 1; i <=24; i++) {
			tempo=sc.nextInt();
			n=sc.nextInt();
			if ((i==11) || (i==12) || (i==22) || (i==23) || (i==24) ){
				vet.add(n);
			}
			
		}
		 
		double media1=(vet.get(0)+vet.get(1))/2;
		double media2=(vet.get(2)+vet.get(3)+vet.get(4))/3;
		double media3=(media1+media2)/2;
		media3=media3*100;
		int l=(int)media3/100;
		System.out.println(l);
		
		
		

	}

}
