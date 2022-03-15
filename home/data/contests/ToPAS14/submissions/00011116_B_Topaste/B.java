//package topas;

import java.lang.reflect.Array;
import java.util.Scanner;

public class B {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner ler=new Scanner(System.in);
		int cont=0;
		int n=ler.nextInt();
		int[] vetor=new int[n+1];
		for(int i=0;i<n;i++){
			vetor[i]=ler.nextInt();
		}
		
		for(int e=0;e<n;e++){
			if(e==0){
				if(vetor[e]>vetor[e+1]*2){
					cont++;
				}	
			}else
			if(e==n){
				if(vetor[e]>vetor[e-1]*2){
					cont++;
				}	
			}else
			if((vetor[e]>vetor[e+1]*2)&&(vetor[e]>vetor[e-1]*2)){
				cont++;
			}
			//System.out.println("-->"+e);
			
		}
		System.out.println(cont);
	}

}
