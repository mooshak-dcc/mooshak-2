import java.util.Scanner;
public class a {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int ano = in.nextInt();
		int mes = in.nextInt();
		int dia = in.nextInt();
		int a = 0;
		int b = 0;
		int c = 0;
		double d = 0;
		double e = 0;
		int mes1 = mes;
		int ano1 = ano;
		if(mes < 3){
			ano1 = ano - 1;
			mes1 = mes + 12;
		}
		a = ano/100;
		b = a/4;
		if((ano>1582) || (ano==1582 && mes>10 && dia>15))
		{
			c=2-a+b;
		}
		else
		{
			c = 0;
		}
		d = 365.25*(ano1 + 4716);
		e = 30.6001*(mes1 + 1);
		int nd = (int) d;
		int ne = (int) e;

		int dj = nd + ne + dia + c - 1524;

		System.out.println(dj);
		in.close();
	}

}