import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import com.lightstreamer.interfaces.data.*;

class ConnectionServer implements Runnable {
    ServerSocket server = null;
	String  svtype;
	int port;
	ImagePanel pgps, pimg;
    private ItemEventListener listener;
    private Object itemHandle;

    ConnectionServer (int port, ImagePanel pgps, ImagePanel pimg, Object itemHandle, ItemEventListener listener) {
		this.pgps = pgps;
		this.pimg = pimg;
		this.port = port;
		this.svtype = svtype;
        this.itemHandle = itemHandle;
		this.listener = listener;
		
		// construct GPS/Image Listening Server 
        try {
            server = new ServerSocket(port); /* start listening on the port */
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }	
	}
	
	public void run() {
        Socket client = null;
		
		// listening and accept the client connection
        while(true) {
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            /* start a new thread to handle this client */
            Thread t = new Thread(new SrvConn(client,pgps, pimg, itemHandle, listener));
            t.start();
        }

			/* print IP information */
 			// InetAddress addr = client.getLocalAddress().getLocalHost();
			// System.out.println("Client Information: ");
			// System.out.println("  Local Host: " + client.getLocalAddress().getLocalHost());
			// System.out.println("  Host Name : " + addr.getHostName());
			// System.out.println("  IP address: " + addr.getHostAddress());
			// System.out.println("  Port      : " + client.getLocalPort());
			// System.out.println();
			// System.out.println("Connection to Remote: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
			// System.out.println();
    }
	
}

