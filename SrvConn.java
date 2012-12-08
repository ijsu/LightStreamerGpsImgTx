import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import com.lightstreamer.interfaces.data.*;


class SrvConn implements Runnable {
    private Socket client;
    private DataInputStream in = null;
	ImagePanel pimg, pgps;
    private ItemEventListener listener;
    private Object itemHandle;

    SrvConn(Socket client, ImagePanel pgps, ImagePanel pimg, Object itemHandle, ItemEventListener listener) {
		this.client = client;
		this.pgps = pgps;
		this.pimg = pimg;
        this.itemHandle = itemHandle;
		this.listener = listener;
        try {
            /* obtain an input stream to this client ... */
            in = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
    }

    public void run() {
		ReadGPSImage();
    }
	
	void ReadGPSImage () {
	        try {
			// read date and gps information from network
			Date date = new Date(in.readLong());
			double numlat = in.readDouble();
			double numlon = in.readDouble();
			double numalt = in.readDouble();
			// read image length from network
			int img_length = in.readInt();
			
			// read image content form network
			if (img_length > 0) {
				byte[] img_buf = new byte[img_length];
				int total = 0;
				while (total < img_length) {
					int count = in.read(img_buf, total, img_length - total);
					if (count < 0) {
						break;
					}
					total += count;
				}

				// construct image object and write to file
				BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(img_buf));
				ImageIO.write(bimg, "png", new File("../../../pages/GpsImgTx/imgxml/" + Long.toString(date.getTime())+ ".png"));

				// scale image and show on the panel
				pimg.img = bimg.getScaledInstance(360,360,Image.SCALE_FAST);;
				pimg.repaint();			
			}
			
			// close connection
			in.close();
			client.close();
							
			System.out.println("Captured Time: " + date);
			System.out.println("Lat: " + numlat);
			System.out.println("Lon: " + numlon);
			System.out.println("Image Length: " + img_length);
			
			//constuct the URL of google map
			String mapurl = "http://maps.google.com/staticmap?center=";
			String lon = Double.toString(numlon);
			String lat = Double.toString(numlat);
			String alt = Double.toString(numalt);
			String marker = "&markers=" + lat + "," + lon + ",red";
			String zoomsize = "&zoom=" + "15" + "&size=" + "360x360";
			String key = "&key=ABQIAAAABPwouWYj3VnD5GFB6ruqxxRQKVdcuJ1PIKoX_YH_iOiTmZiR8BR__D2FH9CJzfKzbgyDU3EOrVPL8w";
			String urlpath = mapurl + lon + "'" + lat + marker + zoomsize + key;
   			
			// paint the map image
			pgps.img = ImageIO.read(new URL(urlpath));
			pgps.repaint();
			
			// write to File
			Waypoints wps = new Waypoints("../../../pages/GpsImgTx/imgxml/recorded.xml");
			wps.addPoint(date, numlat, numlon, numalt);
			wps.writeXML("../../../pages/GpsImgTx/imgxml/recorded.xml");
			
			// prepare for push information 
            Map<String, String> data = new HashMap<String, String>();
            data.put("gpstime", Long.toString(date.getTime()));
            data.put("latitude", lat);
            data.put("longitude", lon);
            data.put("altitude", alt);
			
			// push data
            listener.smartUpdate(itemHandle, data, false);
        } catch (IOException e) {
            System.err.println(e);
        }
	}

}