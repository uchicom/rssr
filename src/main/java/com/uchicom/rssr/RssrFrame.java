// (c) 2017 uchicom
package com.uchicom.rssr;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.uchicom.wjm.WJMFrame;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssrFrame extends JFrame {

	private JList<Item> list = new JList<>();
	private JEditorPane editorPane = new JEditorPane();
	private File configFile = new File("conf/rssr.properties");
	private Properties config = new Properties();

	private List<Channel> channelList = new ArrayList<Channel>();

	/**
	 *
	 */
	public RssrFrame() {
		super(Constants.NAME);
		initComponents();
	}

	private void initProperties() {
		try (FileInputStream fis = new FileInputStream(configFile);) {
			config.load(fis);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private void initComponents() {
		initProperties();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		list.setFont(list.getFont().deriveFont(Font.PLAIN));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(new JScrollPane(list));
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
			if (!e.getValueIsAdjusting()) {
				Item item = list.getSelectedValue();
				StringBuffer strBuff = new StringBuffer();
				if (item != null) {
					strBuff.append("<h1>").append(item.getTitle()).append("</h1><h2>");
					strBuff.append(
							Constants.formatter2.format(item.getPubDate().toInstant().atZone(ZoneId.systemDefault())))
							.append("</h2><p>");
					if (item.getDescription().contains("<br/>")) {
						strBuff.append(item.getDescription());
					} else {
						strBuff.append(item.getDescription().replaceAll("\n", "<br/>"));
					}
					strBuff.append("</p>");
				}
				editorPane.setText(strBuff.toString());
				editorPane.setCaretPosition(0);
			}
		});
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() > 1) {
					if (!list.isSelectionEmpty()) {
						try {
							URL link = list.getSelectedValue().getLink();
							URL url = new URL(link.getProtocol(), link.getHost(), link.getPort(), link.getFile() , new URLStreamHandler() {

								@Override
								protected int getDefaultPort() {
									System.out.println("dp:" + super.getDefaultPort());
									return 80;
								}
								@Override
								protected URLConnection openConnection(URL paramURL) throws IOException {
									System.out.println("openCon:" + paramURL);
									URLConnection con = new URL(paramURL.toString()).openConnection();
//									con.setRequestProperty("Accept-Encoding", "gzip");
									con.setRequestProperty("Accept-Charset", "UTF-8,*;q=0.5");
									con.setRequestProperty("Accept-Language", "ja,en-US;q=0.8,en;q=0.6");
									con.setRequestProperty("User-Agent", "WJM/1.0");
									return con;
								}

							});
							WJMFrame frame = new WJMFrame(url);
							frame.setPreferredSize(new Dimension(300, 300));
							frame.setVisible(true);
						} catch (MalformedURLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
					}
				}
			}
		});
		splitPane.setRightComponent(new JScrollPane(editorPane));
		getContentPane().add(splitPane);

		config.entrySet().forEach((e) -> {
			String key = e.getKey().toString();
			if (key.endsWith(".url")) {
				Thread thread = new Thread(new RssRunnable(this, key));
				thread.setDaemon(true);
				thread.start();
			}
		});
		pack();
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	public void setChannel(Channel channel) {
		synchronized (this) {
			Item before = list.getSelectedValue();
			if (!channelList.contains(channel)) {
				channelList.add(channel);
			}

			List<Item> tempList = new ArrayList<>();
			channelList.forEach((c)-> {
				c.getItemList().forEach((item) -> {
					tempList.add(item);
				});
			});
			SwingUtilities.invokeLater(() -> {
				tempList.sort(new Comparator<Item>() {
					@Override
					public int compare(Item o1, Item o2) {
						if (o1 == null) {
							if (o2 == null) {
								return 0;
							} else {
								return 1;
							}
						} else if (o2 == null) {
							return -1;
						}
						if (o1.getPubDate()==null) {
							return 1;
						} else if (o2.getPubDate()==null) {
							return -1;
						}
						return -o1.getPubDate().compareTo(o2.getPubDate());
					}

				});

				DefaultListModel<Item> model = new DefaultListModel<>();
				tempList.forEach((i)->{
					model.addElement(i);
				});
				list.setModel(model);
				list.setSelectedValue(before, true);

			});
		}
	}

}
