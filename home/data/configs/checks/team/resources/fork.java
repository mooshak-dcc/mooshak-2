
import java.util.Vector;
import java.lang.Thread;
import java.lang.InterruptedException;

class fork extends Thread {
  private String path;

  public static void main(String args[]) {
    fork f = new fork("");
  }
  
  fork(String path) {
    this.path=path;
    System.out.println(path);
    start();
  }

  public void run() {
    Vector<fork> children;

    children = new Vector<fork>();
    int count = 0;
    while(true) {
	children.add(new fork(path+"/"+(count++)));
	try {
	    sleep(10);
	} catch(InterruptedException e) {}

    }

  }

}
