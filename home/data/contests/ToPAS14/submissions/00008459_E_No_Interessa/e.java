import java.util.Scanner;
public class e {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		int n1 = in.nextInt();
		String output = "";
		String[] linhas = new String[n1];
		
		for(int i = 0; i < n1; i++){
			linhas[i] = in.next();
		}
		
		int n = in.nextInt();
		
		while(n != 0){
			int x = in.nextInt();
			switch (n){
			case 1:
				for(int k = 0; k<x; k++){
					String palavra = in.next();
					for(int i = 0; i < n1; i++){
						if(palavra.equals(linhas[i])){
							output += (i+1);
							if(k < x-1){
								output += " ";
							}
						}
					}
				}
				break;
			case 2:
				for(int k = 0; k<x; k++){
					int palavra = in.nextInt();
					for(int i = 0; i < n1; i++){
						if(palavra == i+1){
							output += linhas[i];
							if(k < x-1){
								output += " ";
							}
						}
					}
				}
				break;
			}
			output += "\n";
			n = in.nextInt();
		}
		
		in.close();
		
		System.out.println(output);
	}

}