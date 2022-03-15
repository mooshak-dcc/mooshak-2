import java.util.Scanner;
public class d {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		int num;
		int soma = 0;
		String data;
		int c = 0;
		int[][] matriz = new int[24][3];
		
		for(int i = 0; i < 24; i++){
			data = in.next();
			num = in.nextInt();
			for(int l=0; l < 3; l++){
				switch(l){
				case 0:
					matriz[i][l] = Integer.parseInt(data.substring(0, 4));
					break;
				case 1:
					matriz[i][l] = Integer.parseInt(data.substring(4));
					break;
				case 2:
					matriz[i][l] = num;
					break;
				}
			}
		}
		
		int media3 = (matriz[23][2] + matriz[22][2] + matriz[21][2])/3;
		
		int mes = matriz[23][1] + 1;
		
		for(int i = 0; i < 24; i++){
			if(matriz[i][1] == mes){
				c++;
				soma += matriz[i][2];
			}
		}
		
		int media = soma/c;
		int output = (media+media3)/2;
		
		in.close();
		
		System.out.println(output);
	}

}