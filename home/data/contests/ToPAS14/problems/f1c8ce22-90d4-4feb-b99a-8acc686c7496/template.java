import java.util.Scanner;

public class Prime {
  private static final Scanner IN = new Scanner(System.in);

  public static void main(String[] args) {
    if (isPrime(IN.nextInt())) {
      System.out.println("yes");
      return;
    }
    System.out.println("no");
  }

  ${SOLUTION}
}
