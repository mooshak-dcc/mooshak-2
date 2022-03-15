package exBfeito;

import java.util.ArrayList;
import java.util.Scanner;

public class exBfeito {

	public static void main(String[] args) {
Scanner sc=new Scanner(System.in);
		
		ArrayList<Integer> vet=new ArrayList<Integer>();
		
		int n,x,a=0,b=0,c=0,cont;
		n=sc.nextInt();
		cont=0;
		for (int i =1 ; i <=n ; i++) {
			x=sc.nextInt();
			vet.add(x);
		}
		for (int j = 1; j <= vet.size(); j++) {
			if ((j+2<vet.size()) && (j+1<vet.size())){
				a=vet.get(j)*2;
				b=vet.get(j+2)*2;
				c=vet.get(j+1);
				if ((c>a) && (c>b)) {
					cont++;
					
					
				}
			}
		}
		
		System.out.println(cont);
		

	}

}
