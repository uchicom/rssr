// (c) 2017 uchicom
package com.uchicom.rssr;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
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

	private JList<Item> list = new JList<>();
	private final Channel channel = new Channel();
	private Stack<QName> stack = new Stack<>();
	private JFrame frame = new JFrame();
	private JEditorPane editorPane = new JEditorPane();

	/**
	 *
	 */
	public RssrFrame() {
		super(Constants.NAME);
		initComponents();
	}

	private void initComponents() {
		frame.getContentPane().add(new JScrollPane(editorPane));
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		list.setFont(list.getFont().deriveFont(Font.PLAIN));
		getContentPane().add(new JScrollPane(list));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// list.
		// list.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mouseEntered(MouseEvent me) {
		// editorPane.setText(list.getComponentAt(list.getMousePosition()).getName());
		// frame.pack();
		// frame.setVisible(true);
		// }
		// @Override
		// public void mouseExited(MouseEvent me) {
		// frame.setVisible(false);
		// }
		// });

		list.addListSelectionListener((e) -> {
//			if (e.getValueIsAdjusting()) {
				if (e.getLastIndex() >= 0) {
					editorPane.setText(channel.getItemList().get(e.getLastIndex()).getDescription());
					editorPane.setCaretPosition(0);
					frame.setVisible(true);
				} else {
					frame.setVisible(false);
				}
//			}
		});

		// ここから
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader r = null;
		try {
			URL url = new URL(Constants.RSS_CROWDWORKS);
			URLConnection con = url.openConnection();
			con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
			con.setRequestProperty("Accept-Charset", "UTF-8,*;q=0.5");
			con.setRequestProperty("Accept-Language", "ja,en-US;q=0.8,en;q=0.6");
			con.setRequestProperty("User-Agent", "RSSR/1.0");
			InputStream is = con.getInputStream();
			r = factory.createXMLEventReader(is, "utf-8");
			Map<Integer, Handler> handlers = initHandlers();
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
		}
		channel.getItemList().forEach((item) -> {
			System.out.println(item.getTitle());
			System.out.println(item.getLink());
			System.out.println(item.getDescription());
		});
		DefaultListModel<Item> model = new DefaultListModel<>();
		channel.getItemList().forEach((item) -> {
			model.addElement(item);
		});
		list.setModel(model);
		pack();
	}

	private Map<Integer, Handler> initHandlers() {
		Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();

		handlers.put(XMLEvent.START_ELEMENT, (e) -> {
			QName name2 = new QName("item");
			QName name = ((StartElement) e).getName();
			stack.add(name);
			if (name2.equals(name)) {
				channel.getItemList().add(new Item());
			}
		});
		handlers.put(XMLEvent.END_ELEMENT, (e) -> {
			stack.pop();
		});
		handlers.put(XMLEvent.ATTRIBUTE, (e) -> {
		});
		handlers.put(XMLEvent.CDATA, (e) -> {
			if (channel.getItemList().isEmpty())
				return;
			Characters characters = (Characters) e;
			if (characters.isWhiteSpace())
				return;
			System.out.println(characters.isCData());
			System.out.println(characters.getData());

			List<Item> list = channel.getItemList();
			int max = list.size() - 1;

			Item item = list.get(max);
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
			}
		});
		handlers.put(XMLEvent.CHARACTERS, (e) -> {
			if (channel.getItemList().isEmpty())
				return;
			Characters characters = (Characters) e;

			if (characters.isWhiteSpace())
				return;
			System.out.println(characters.isCData());
			System.out.println(characters.getData());
			List<Item> list = channel.getItemList();
			int max = list.size() - 1;

			Item item = list.get(max);
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
			}
		});
		handlers.put(XMLEvent.COMMENT, (e) -> {
		});
		handlers.put(XMLEvent.DTD, (e) -> {
		});
		handlers.put(XMLEvent.END_DOCUMENT, (e) -> {
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

		return handlers;
	}
}
