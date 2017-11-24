// (c) 2017 uchicom
package com.uchicom.rssr;

import javax.swing.SwingUtilities;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			RssrFrame frame = new RssrFrame();
			frame.setVisible(true);
		});

	}

}
