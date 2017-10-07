// (c) 2017 uchicom
package com.uchicom.rssr;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssAccessor {

	private final Channel channel = new Channel();

	private Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();

	private DateTimeFormatter dateFormatter;
	private Item item;

	public RssAccessor(String dateFormat) {
		this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
		initHandlers();
	}

	public Channel execute(URL url) throws IOException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader r = null;
		URLConnection con = url.openConnection();
		// con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
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
		return channel;
	}

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
					item = new Item();
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
						item.setPubDate(Date.from(OffsetDateTime.parse(text, dateFormatter).toInstant()));
					}
					break;
				}
			} catch (Exception e2) {
				// TODO 自動生成された catch ブロック
				e2.printStackTrace();
			}
		});
		handlers.put(XMLEvent.END_ELEMENT, (e, text) -> {
			QName name = e.asEndElement().getName();
			if ("item".equals(name.getLocalPart())) {
				item = null;
			}
		});
		handlers.put(XMLEvent.ATTRIBUTE, (e, text) -> {
		});
		handlers.put(XMLEvent.CDATA, (e, text) -> {
		});
		handlers.put(XMLEvent.CHARACTERS, (e, text) -> {
		});
		handlers.put(XMLEvent.COMMENT, (e, text) -> {
		});
		handlers.put(XMLEvent.DTD, (e, text) -> {
		});
		handlers.put(XMLEvent.END_DOCUMENT, (e, text) -> {
		});
		handlers.put(XMLEvent.ENTITY_DECLARATION, (e, text) -> {
		});
		handlers.put(XMLEvent.ENTITY_REFERENCE, (e, text) -> {
		});
		handlers.put(XMLEvent.NAMESPACE, (e, text) -> {
		});
		handlers.put(XMLEvent.NOTATION_DECLARATION, (e, text) -> {
		});
		handlers.put(XMLEvent.PROCESSING_INSTRUCTION, (e, text) -> {
		});
		handlers.put(XMLEvent.SPACE, (e, text) -> {
		});
		handlers.put(XMLEvent.START_DOCUMENT, (e, text) -> {
		});

	}
}
