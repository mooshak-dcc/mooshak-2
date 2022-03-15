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

  public static boolean isPrime(int n) {
    if (n <= 1) {
      return false;
    }
    for (int i = 2; i < Math.sqrt(n); i++) {
      if (n % i == 0) {
        return false;
      }
    }
    return true;
  }

}
