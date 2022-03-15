import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class Security {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		

			URL url = new URL("http://mooshak.dcc.fc.up.pt/index.html");
			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader in = new BufferedReader(reader);
			String line;
			
			while((line = in.readLine()) != null)
				System.out.println(line);

	}

}
