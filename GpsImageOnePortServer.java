import javax.swing.*;
import java.awt.*;
import java.io.*;
import com.lightstreamer.interfaces.data.*;

class GpsImageOnePortServer {
    private ItemEventListener listener;
    private Object itemHandle;

        public GpsImageOnePortServer(Object itemHandle, ItemEventListener listener) {
            this.itemHandle = itemHandle;
			this.listener = listener;
        }
		
    public void go () {
		JFrame f = new JFrame();
		ImagePanel pgps = new ImagePanel(new ImageIcon("./samplemap.png").getImage());
		Image img2 = (new ImageIcon("./street.png").getImage()).getScaledInstance(360,360,Image.SCALE_FAST);
		ImagePanel pimg = new ImagePanel(img2);
		f.getContentPane().setLayout(new GridLayout(1,2));
		f.getContentPane().add(pgps);
		f.getContentPane().add(pimg);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(720,360);
		f.setVisible(true);
		Thread gps_img_t = new Thread(new ConnectionServer(5566, pgps, pimg, itemHandle, listener));
        gps_img_t.start();
	}
}
