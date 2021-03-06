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

import com.uchicom.rssr.dto.ChannelDto;
import com.uchicom.rssr.dto.ItemDto;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssAccessor {

	private ChannelDto channel;

	private Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();

	private DateTimeFormatter dateFormatter;
	private ItemDto itemDto;

	public RssAccessor(String dateFormat) {
		this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
		initHandlers();
	}

	public ChannelDto execute(URL url) throws IOException, XMLStreamException {
		channel = new ChannelDto(url);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		URLConnection con = url.openConnection();
		// con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
		con.setRequestProperty("Accept-Charset", "UTF-8,*;q=0.5");
		con.setRequestProperty("Accept-Language", "ja,en-US;q=0.8,en;q=0.6");
		con.setRequestProperty("User-Agent", "RSSR/1.0.1");

		try (InputStream is = con.getInputStream()) {
			XMLEventReader r = factory.createXMLEventReader(is, "utf-8");
			while (r.hasNext()) {
				XMLEvent e = r.nextEvent();
				handlers.get(e.getEventType()).handle(e, r);
			}
		}
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
					itemDto = new ItemDto();
					channel.getItemList().add(itemDto);
					break;
				case "title":
					if (itemDto == null)
						break;
					text = r.getElementText();
					itemDto.setTitle(text);
					break;
				case "link":
					if (itemDto == null)
						break;
					try {
						text = r.getElementText();
						itemDto.setLink(new URL(text));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;
				case "description":
					if (itemDto == null)
						break;
					text = r.getElementText();
					itemDto.setDescription(text);
					break;
				case "guid":
					if (itemDto == null)
						break;
					text = r.getElementText();
					itemDto.setGuid(text);
					break;
				case "date":
					if (itemDto == null || !"dc".equals(name.getPrefix())) {
						break;
					}
					try {
						text = r.getElementText();
						itemDto.setPubDate(Date.from(OffsetDateTime.parse(text)
								.truncatedTo(ChronoUnit.SECONDS)
								.toInstant()));
					} catch (Exception ee) {
						itemDto.setPubDate(Date.from(OffsetDateTime.parse(text, dateFormatter).toInstant()));
					}
					break;
				case "pubDate":
					if (itemDto == null)
						break;
					try {
						text = r.getElementText();
						itemDto.setPubDate(Date.from(OffsetDateTime.parse(text, Constants.formatter)
								.truncatedTo(ChronoUnit.SECONDS)
								.toInstant()));
					} catch (Exception ee) {
						itemDto.setPubDate(Date.from(OffsetDateTime.parse(text, dateFormatter).toInstant()));
					}
					break;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		handlers.put(XMLEvent.END_ELEMENT, (e, text) -> {
			QName name = e.asEndElement().getName();
			if ("item".equals(name.getLocalPart())) {
				itemDto = null;
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
