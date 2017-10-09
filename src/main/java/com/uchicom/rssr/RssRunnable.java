// (c) 2017 uchicom
package com.uchicom.rssr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import com.uchicom.rssr.dto.ChannelDto;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssRunnable implements Runnable {

	private RssrFrame frame;
	private String key;
	private boolean alive = true;

	public RssRunnable(RssrFrame frame, String key) {
		this.frame = frame;
		this.key = key;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (alive) {
			try {
				RssAccessor accessor = new RssAccessor(frame.getProperty("b.pubDate.format"));
				URL url = new URL(frame.getProperty(key));
				ChannelDto channelDto = accessor.execute(url);
				frame.setChannel(channelDto);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (XMLStreamException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
				// メッセージは表示したいな
				JOptionPane.showMessageDialog(frame, e1.getClass().getName() + ":" + e1.getMessage());
			}
			try {
				Thread.sleep(Constants.UPDATE_SPAN);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
