import java.util.ArrayList;
import java.util.Scanner;

public class exDDDDDDD {


		public static void main(String[] args) {
			Scanner sc=new Scanner(System.in);
			ArrayList<Integer> vet=new ArrayList<Integer>();
			int tempo,n;
			
			for (int i = 1; i <=24; i++) {
				tempo=sc.nextInt();
				n=sc.nextInt();
					vet.add(n);
				}
				
			
			 
			double media1=(vet.get(11)+vet.get(23))/2;
			double media2=(vet.get(21)+vet.get(22)+vet.get(23))/3;
			double media3=(media1+media2)/2;
			media3=media3*100;
			int l=(int)media3/100;
			System.out.println(l);
			

		}

	}
