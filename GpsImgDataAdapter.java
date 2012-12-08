import java.util.*;
import java.io.File;
import com.lightstreamer.interfaces.data.*;

public class GpsImgDataAdapter implements SmartDataProvider {

    private ItemEventListener listener;
    private volatile GpsImgThread gt;

    public void init(Map params, File configDir) throws DataProviderException {
    }

    public boolean isSnapshotAvailable(String itemName) throws SubscriptionException {
        return false;
    }

    public void setListener(ItemEventListener listener) {
        this.listener = listener;
    }

    public void subscribe(String itemName, Object itemHandle, boolean needsIterator)
            throws SubscriptionException, FailureException {
        if (itemName.equals("gpsparameters")) {
            gt = new GpsImgThread(itemHandle);
            gt.start();
        }
    }
    
    public void subscribe(String itemName, boolean needsIterator)
                throws SubscriptionException, FailureException {
    }         	

    public void unsubscribe(String itemName) throws SubscriptionException,
            FailureException {
        if (itemName.equals("gpsparameters") && gt != null) {
            gt.go = false;
        }
    }

    class GpsImgThread extends Thread {

        private final Object itemHandle;
        public volatile boolean go = true;

        public GpsImgThread(Object itemHandle) {
            this.itemHandle = itemHandle;
        }

        public void run() {
			GpsImageOnePortServer monitor = new GpsImageOnePortServer(itemHandle, listener);
			monitor.go();
        }
    }

}