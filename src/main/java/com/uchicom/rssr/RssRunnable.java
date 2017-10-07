// (c) 2017 uchicom
package com.uchicom.rssr;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.uchicom.rssr.dto.ItemDto;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssRunnable implements Runnable {

	private final Channel channel = new Channel();
	private Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();
	private RssrFrame frame;
	private String key;
	private ItemDto item;
	private boolean alive = true;

	public RssRunnable(RssrFrame frame, String key) {
		this.frame = frame;
		this.key = key;
		initHandlers();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (alive) {
			// ここから
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader r = null;
			try {
				URL url = new URL(frame.getProperty(key));
				URLConnection con = url.openConnection();
				con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
				con.setRequestProperty("Accept-Charset", "UTF-8,*;q=0.5");
				con.setRequestProperty("Accept-Language", "ja,en-US;q=0.8,en;q=0.6");
				con.setRequestProperty("User-Agent", "RSSR/1.0.1");
				InputStream is = con.getInputStream();
				r = factory.createXMLEventReader(is, "utf-8");
				while (r.hasNext()) {
					XMLEvent e = r.nextEvent();
					handlers.get(e.getEventType()).handle(e, r);
				}
				is.close();
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
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

	Handler empty = (e, r) -> {
	};

	/**
	 * runnableに持たせて、それぞれでパースの挙動を変更する。
	 *
	 * @return
	 */
	private void initHandlers() {

		handlers.put(XMLEvent.START_ELEMENT, (e, r) -> {
			QName name = e.asStartElement().getName();
			String text = null;
			try {
				switch (name.getLocalPart()) {
				case "item":
					item = new ItemDto();
					channel.getItemList().add(item);
					break;
				case "title":
					if (item == null)
						break;
					text = r.getElementText();
					item.setTitle(text);
					break;
				case "link":
					if (item == null)
						break;
					try {
						text = r.getElementText();
						item.setLink(new URL(text));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;
				case "description":
					if (item == null)
						break;
					text = r.getElementText();
					item.setDescription(text);
					break;
				case "guid":
					if (item == null)
						break;
					text = r.getElementText();
					item.setGuid(text);
					break;
				case "pubDate":
					if (item == null)
						break;
					try {
						text = r.getElementText();
						item.setPubDate(Date.from(OffsetDateTime.parse(text, Constants.formatter)
								.truncatedTo(ChronoUnit.SECONDS).toInstant()));
					} catch (Exception ee) {
						item.setPubDate(
								Date.from(
										OffsetDateTime
												.parse(text,
														DateTimeFormatter
																.ofPattern(frame.getProperty("b.pubDate.format")))
												.toInstant()));
					}
					break;
				}
			} catch (Exception e2) {
				// TODO 自動生成された catch ブロック
				e2.printStackTrace();
			}
		});
		handlers.put(XMLEvent.END_ELEMENT, (e, r) -> {
			QName name = e.asEndElement().getName();
			if ("item".equals(name.getLocalPart())) {
				item = null;
			}
		});
		handlers.put(XMLEvent.CHARACTERS, empty);
		handlers.put(XMLEvent.COMMENT, empty);
		handlers.put(XMLEvent.DTD, empty);
		handlers.put(XMLEvent.END_DOCUMENT, (e, r) -> {
			frame.setChannel(channel);
		});
		handlers.put(XMLEvent.ENTITY_DECLARATION, empty);
		handlers.put(XMLEvent.ENTITY_REFERENCE, empty);
		handlers.put(XMLEvent.NAMESPACE, empty);
		handlers.put(XMLEvent.NOTATION_DECLARATION, empty);
		handlers.put(XMLEvent.PROCESSING_INSTRUCTION, empty);
		handlers.put(XMLEvent.SPACE, empty);
		handlers.put(XMLEvent.START_DOCUMENT, empty);

	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
