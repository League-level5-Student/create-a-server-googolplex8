import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JavaWebServer {

	private static final int NUMBER_OF_THREADS = 100;
	private static final Executor THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);

		// Waits for a connection request
		while (true) {
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					HandleRequest(connection);
				}
			};
			THREAD_POOL.execute(task);

		}

	}

	private static void HandleRequest(Socket s) {
		BufferedReader in;
		PrintWriter out;
		String request;

		try {
			String webServerAddress = s.getInetAddress().toString();
			System.out.println("New Connection:" + webServerAddress);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			request = in.readLine();
			System.out.println("--- Client request: " + request);

			out = new PrintWriter(s.getOutputStream(), true);

			out.println("HTTP/1.0 200");
			String url = "";
			String p1 = request.replace("GET ", "");
			String p2 = p1.replace(" HTTP/1.1", "");
			System.out.println("modified request: " + p2);
			switch (p2) {
			case "/":
				url = "page2.html";
				out.println("Content-type: text/html");
				break;
			case "/script.js":
				url = p2.substring(1);
				out.println("Content-type: application/javascript");
				break;
			case "/styles.css":
				url = p2.substring(1);
				out.println("Content-type: text/css");
				break;
			case "/favicon.ico":
				url = p2.substring(1);
				out.println("Content-type: image/x-icon");
				break;
			case "/page.html":
				url = p2.substring(1);
				out.println("Content-type: text/html");
				break;
			}
			while (in.ready()) {
				System.out.println(in.readLine());
			}
			out.println("Server-name: myserver");

			File f = new File(url);
			Scanner scanner = new Scanner(f);
			String lines = "";
			while (scanner.hasNext()) {
				lines = lines + scanner.nextLine();
			}
			out.println("");
			out.println(lines);

			/*
			 * String response = "<html>" + "<head>" + "<title>My Web Server</title></head>"
			 * + "<h1>Change the server code so that it can read files!</h1>" + "</html>";
			 * out.println("Content-length: " + response.length()); out.println("");
			 * out.println(response);
			 */
			out.flush();
			out.close();
			s.close();

		} catch (IOException e) {
			System.out.println("Failed respond to client request: " + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

}