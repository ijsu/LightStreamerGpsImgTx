import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;
import java.util.*;

public class Waypoints {
	Document docJDOM;
	Element elmtRoot;
    SAXBuilder bSAX = new SAXBuilder(false);
	
	public Waypoints(String fname) {
		try {
			File f = new File(fname);
			if (f.exists()) {
				docJDOM = bSAX.build(f);
				elmtRoot = docJDOM.getRootElement();
			} else {
				elmtRoot = new Element("waypoints");
				docJDOM = new Document(elmtRoot);
			}
		}
		catch (JDOMException e) {
			e.printStackTrace();
		  return;
		}
		catch (IOException e) {
			e.printStackTrace();
		  return;
		}
		
	}

	public Waypoints() {
		elmtRoot = new Element("waypoints");
		docJDOM = new Document(elmtRoot);	
	}

	public void addPoint(Date date, double lat, double lon, double alt) {
		Element elmt = new Element("waypoint");
		elmt.setAttribute("time",Long.toString(date.getTime()));
		elmt.setAttribute("latitude",Double.toString(lat));
		elmt.setAttribute("longitude",Double.toString(lon));
		elmt.setAttribute("altitude",Double.toString(alt));
		elmtRoot.addContent(elmt);
	}
	
	public void printPoints() {
		List lstChildren = elmtRoot.getChildren("waypoint");
		for(int i=0; i<lstChildren.size(); i++) {
		  Element elmtChild = (Element) lstChildren.get(i);
		  System.out.println("Time: " + elmtChild.getAttributeValue("time"));
		  System.out.println("Latitude: " + elmtChild.getAttributeValue("latitude"));
		  System.out.println("Longitude: " + elmtChild.getAttributeValue("longitude"));
		  System.out.println("altitude: " + elmtChild.getAttributeValue("altitude"));
		}
	}
		
	public void writeXML(String strFilename) {
		Format format = Format.getCompactFormat(); 	// 建立輸出格式物件
		format.setEncoding("big5"); 				// 設定輸出encoding 為big5
		format.setOmitEncoding(false); 				// 設定是否省略輸出的encoding
		format.setIndent(" "); 						// 設定縮排時的格式
		format.setLineSeparator(System.getProperty("line.separator")); // 設定隔行的格式

		XMLOutputter fmt = new XMLOutputter();
		fmt.setFormat(format);
		try {
		  FileWriter fwXML = new FileWriter(strFilename);
		  fmt.output(docJDOM, fwXML);
		  fwXML.close();
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Waypoints wps;
		if (args.length == 0) 
			wps = new Waypoints();
		else
			wps = new Waypoints(args[0]);
		wps.addPoint(new Date(),34.5678,67.888,2.334);
		wps.printPoints();
		if (args.length == 0) 
			wps.writeXML("daxi.xml");
		else
			wps.writeXML(args[0]);
	}
}
