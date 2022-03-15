import java.util.Scanner;
public class f {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		int s = in.nextInt();
		int m = in.nextInt();
		int n = in.nextInt();
		int ix = 1;
		int iy = n;
		String output = "";
		String[][] matriz = new String[m][n];
		
		for(int l = 0; l < m; l++){
			String linha = in.next();
			for(int c = 0; c < n; c++){
				if(c < m-1){
					matriz[l][c] = linha.substring(c, (c+1));
				}
				else{
					matriz[l][c] = linha.substring(c);
				}
			}
		}
		
		int z = in.nextInt();
		System.out.print(z);
		
		for(int i = 0; i < z; i++){
			String linha = in.next();
			String letra = linha.substring(0, 1);
			switch (letra){
			case "E":
				ix += Integer.parseInt(linha.substring(1));
				break;
			case "N":
				iy -= Integer.parseInt(linha.substring(1));
				break;
			case "S":
				iy += Integer.parseInt(linha.substring(1));
				break;
			case "W":
				ix -= Integer.parseInt(linha.substring(1));
				break;
			}
		}
		switch(matriz[ix][iy]){
		case "V":
			output = "Dentro(" + ix + "," + iy + "):";
		}
		
		/*
		for(int l = 0; l < m; l++){
			for(int c = 0; c < n; c++){
				System.out.print(matriz[l][c] + " ");
			}
			System.out.println();
		}
		*/
		in.close();
		output = ix + "," + iy;
		System.out.print(output);
	}

}