// (c) 2017 uchicom
package com.uchicom.rssr;

import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Constants {
	public static final File CONF_FILE = new File("conf/rssr.properties");
	public static final String PROP_KEY_RSSR = "rssr";

	public static final String APP_NAME = "RSSR";
	public static final String VERSION = "1.0.1";

	public static DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.systemDefault());
	public static DateTimeFormatter formatter2 =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final String NAME = "RSSR";
	public static final long UPDATE_SPAN = 15 * 60 * 1000;
}
