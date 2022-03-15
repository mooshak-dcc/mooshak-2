import java.util.*;
public class c {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		int px = in.nextInt();
		int py = in.nextInt();
		String t = in.next();
		int nr = in.nextInt();
		int mx = 0;
		int my = 0;
		int m = -1;
		int dist = 0;
		int teste = 0;
		
		for (int i=0; i< nr;i++){
			int x = in.nextInt();
			int y = in.nextInt();
			String tipo = in.next();
			
			if(tipo.equals(t)){
				int x1 = px - x;
				int y1 = py - y;
				if (x1<0){
					x1 = x1* (-1);
				}
				if (y1<0){
					y1 = y1* (-1);
				}
				dist = ((x1)*(x1)) + ((y1)*(y1));
				if(m == -1){
					m = dist;
					mx = x;
					my = y;
				}
				if(dist < m){
					m = dist;
					mx = x;
					my = y;
				}
				teste++;
			}
		}
		String output = "Nenhum";
		if(teste != 0){
			output = mx + " " + my + " " + t + " " + m;
		}
		in.close();
		
		System.out.println(output);
	}

}