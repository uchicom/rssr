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
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssRunnable implements Runnable {

	private Stack<QName> stack = new Stack<>();

	private final Channel channel = new Channel();
	private Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();
	private RssrFrame frame;
	private String key;
	private Item temp;
	private boolean alive = true;

	public RssRunnable(RssrFrame frame, String key) {
		this.frame = frame;
		this.key = key;
		initHandlers();
	}

	/* (非 Javadoc)
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
					handlers.get(e.getEventType()).handle(e);
				}
				is.close();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (XMLStreamException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
				//メッセージは表示したいな
				JOptionPane.showMessageDialog(frame, e1.getClass().getName() + ":" +  e1.getMessage());
			}
			try {
				Thread.sleep(Constants.UPDATE_SPAN);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

	/**
	 * runnableに持たせて、それぞれでパースの挙動を変更する。
	 *
	 * @return
	 */
	private void initHandlers() {

		handlers.put(XMLEvent.START_ELEMENT, (e) -> {
			QName name2 = new QName("item");
			QName name = ((StartElement) e).getName();
			stack.push(name);
			if (name2.equals(name)) {
				temp = new Item();

			}
		});
		handlers.put(XMLEvent.END_ELEMENT, (e) -> {
			stack.pop();
			QName name2 = new QName("item");
			QName name = ((EndElement) e).getName();
			if (name2.equals(name)) {
				boolean exist = false;
				for (Item item : channel.getItemList()) {
					if (item.equals(temp)) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					channel.getItemList().add(temp);
				}
			}
		});
		handlers.put(XMLEvent.ATTRIBUTE, (e) -> {
		});
		handlers.put(XMLEvent.CDATA, (e) -> {
			if (temp == null)
				return;
			Characters characters = (Characters) e;
			if (characters.isWhiteSpace())
				return;


			Item item = temp;
			switch (stack.peek().getLocalPart()) {
			case "title":
				item.setTitle(characters.getData());
				break;
			case "link":
				try {
					item.setLink(new URL(characters.getData()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "description":
				item.setDescription(characters.getData());
				break;
			case "guid":
				item.setGuid(characters.getData());
				break;
			case "pubDate":
				try {
					item.setPubDate(
							Date.from(OffsetDateTime.parse(characters.getData(), Constants.formatter).truncatedTo(ChronoUnit.SECONDS).toInstant()));
				} catch (Exception ee) {
					item.setPubDate(
							Date.from(OffsetDateTime
									.parse(characters.getData(),
											DateTimeFormatter.ofPattern(frame.getProperty("b.pubDate.format")))
									.toInstant()));
				}
				break;
			}
		});
		handlers.put(XMLEvent.CHARACTERS, (e) -> {
			if (temp == null)
				return;
			Characters characters = (Characters) e;

			if (characters.isWhiteSpace())
				return;

			Item item = temp;
			switch (stack.peek().getLocalPart()) {
			case "title":
				item.setTitle(characters.getData());
				break;
			case "link":
				try {
					item.setLink(new URL(characters.getData()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "description":
				item.setDescription(characters.getData());
				break;
			case "guid":
				item.setGuid(characters.getData());
				break;
			case "pubDate":
				try {
					item.setPubDate(
							Date.from(OffsetDateTime.parse(characters.getData(), Constants.formatter).toInstant().truncatedTo(ChronoUnit.SECONDS)));
				} catch (Exception ee) {
					item.setPubDate(
							Date.from(OffsetDateTime
									.parse(characters.getData(),
											DateTimeFormatter.ofPattern(frame.getProperty("b.pubDate.format")))
									.toInstant()));
				}
				break;
			}
		});
		handlers.put(XMLEvent.COMMENT, (e) -> {
		});
		handlers.put(XMLEvent.DTD, (e) -> {
		});
		handlers.put(XMLEvent.END_DOCUMENT, (e) -> {
			frame.setChannel(channel);
		});
		handlers.put(XMLEvent.ENTITY_DECLARATION, (e) -> {
		});
		handlers.put(XMLEvent.ENTITY_REFERENCE, (e) -> {
		});
		handlers.put(XMLEvent.NAMESPACE, (e) -> {
		});
		handlers.put(XMLEvent.NOTATION_DECLARATION, (e) -> {
		});
		handlers.put(XMLEvent.PROCESSING_INSTRUCTION, (e) -> {
		});
		handlers.put(XMLEvent.SPACE, (e) -> {
		});
		handlers.put(XMLEvent.START_DOCUMENT, (e) -> {
		});

	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
