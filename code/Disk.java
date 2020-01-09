public class Disk {
	public static String getPrintSize(long size) {

		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}

		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		
		if (size < 1024) {
			size = size * 100;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
		} else {
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
		}
	}
}