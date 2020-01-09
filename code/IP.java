import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class IP {
	public static NetworkInterface getWlan0NetworkInterface() throws SocketException  {
		Enumeration nets = NetworkInterface.getNetworkInterfaces();
		NetworkInterface netInterfaceWlan0 = null;
		for (Object obj : Collections.list(nets)) {
			NetworkInterface netInterface = (NetworkInterface) obj;
			if (netInterface.getName().equals("wlan0")) {
				netInterfaceWlan0 = netInterface;
			}
		}
		return netInterfaceWlan0;
	}

	public static String getWlan0IP(NetworkInterface netInterface) {
		String ip = "";
		if (netInterface != null) {
			Enumeration addresses = netInterface.getInetAddresses();

			for (Object obj : Collections.list(addresses)) {
				InetAddress inetAddress = (InetAddress) obj;
				if (inetAddress instanceof Inet6Address) {
					continue;
				} else {
					ip = inetAddress.getHostAddress();
				}
			}
		}
		return ip;
	}
}
