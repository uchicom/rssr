// (c) 2017 uchicom
package com.uchicom.rssr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JFrame;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssrFrame extends JFrame {

	private final Channel channel = new Channel();
	private Stack<QName> stack = new Stack<>();

	public RssrFrame() {
		initComponents();
	}

	private void initComponents() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader r;
		try {
			r = factory.createXMLEventReader(
					new URL("https://crowdworks.jp/public/jobs/group/development.rss").openStream());
			Map<Integer, Handler> handlers = initHandlers();
			while (r.hasNext()) {
				XMLEvent e = r.nextEvent();
				handlers.get(e.getEventType()).handle(e);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		channel.getItemList().forEach((item)-> {
			System.out.println(item.getTitle());
		});
		pack();
	}

	private Map<Integer, Handler> initHandlers() {
		Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();

		handlers.put(XMLEvent.START_ELEMENT, (e)-> {
			QName name2 = new QName("item");
			QName name = ((StartElement)e).getName();
			stack.add(name);
			if (name2.equals(name)) {
				channel.getItemList().add(new Item());
			}
		});
		handlers.put(XMLEvent.END_ELEMENT, (e)-> {
			stack.pop();
		});
		handlers.put(XMLEvent.ATTRIBUTE,  (e)-> {
		});
		handlers.put(XMLEvent.CDATA, (e)-> {
		});
		handlers.put(XMLEvent.CHARACTERS, (e)-> {
			if (channel.getItemList().isEmpty()) return;
			Characters characters = (Characters) e;

			List<Item> list = channel.getItemList();
			int max = list.size() - 1;

			Item item = list.get(max);
			switch (stack.peek().getLocalPart()) {
			case "title":
				item.setTitle(characters.getData());
				break;
			case "url":
				try {
					item.setLink(new URL(characters.getData()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "description":
				item.setDescription(characters.getData());
				break;
			}
		});
		handlers.put(XMLEvent.COMMENT, (e)-> {
		});
		handlers.put(XMLEvent.DTD, (e)-> {
		});
		handlers.put(XMLEvent.END_DOCUMENT, (e)-> {
		});
		handlers.put(XMLEvent.ENTITY_DECLARATION, (e)-> {
		});
		handlers.put(XMLEvent.ENTITY_REFERENCE, (e)-> {
		});
		handlers.put(XMLEvent.NAMESPACE, (e)-> {
		});
		handlers.put(XMLEvent.NOTATION_DECLARATION, (e)-> {
		});
		handlers.put(XMLEvent.PROCESSING_INSTRUCTION, (e)-> {
		});
		handlers.put(XMLEvent.SPACE, (e)-> {
		});
		handlers.put(XMLEvent.START_DOCUMENT, (e)-> {
		});

		return handlers;
	}
}
