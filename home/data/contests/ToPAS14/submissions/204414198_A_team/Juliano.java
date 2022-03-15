import java.util.Scanner;

public class Juliano {

	static Scanner in = new Scanner(System.in);

	public static void main(String args[]) {

		int ano = in.nextInt();
		int mes = in.nextInt();
		int dia = in.nextInt();

		System.out.println(jdn(ano, mes, dia));
	}

	static int jdn(int ano, int mes, int dia) {

		if (mes < 3) {
			mes += 12;
			ano--;
		}

		int a = ano / 100;
		int b = a / 4;
		int c = 0;

		if (ano > 1582)
			c = 2 - a + b;
		else if (ano < 1582)
			c = 0;
		else {
			if (mes > 10)
				c = 2 - a + b;
			if (mes < 10)
				c = 0;
			else {
				if (dia >= 15)
					c = 2 - a + b;
				else if (dia <= 4)
					c = 0;
			}
		}

		int d = (int) (365.25 * (ano + 4716));
		int e = (int) (30.6001 * (mes + 1));

		return d + e + dia + c - 1524;
	}
}
