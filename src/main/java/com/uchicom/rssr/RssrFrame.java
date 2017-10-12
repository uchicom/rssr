// (c) 2017 uchicom
package com.uchicom.rssr;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.uchicom.rssr.dto.ChannelDto;
import com.uchicom.rssr.dto.ItemDto;
import com.uchicom.ui.ResumeFrame;
import com.uchicom.wjm.WJMFrame;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RssrFrame extends ResumeFrame {

	private JList<ItemDto> list = new JList<>();
	private JEditorPane editorPane = new JEditorPane();

	private List<ChannelDto> channelList = new ArrayList<>();

	private List<RssRunnable> runnableList = new ArrayList<>();

	/**
	 *
	 */
	public RssrFrame() {
		this(Constants.CONF_FILE);
	}
	public RssrFrame(File configFile) {
		super(configFile, Constants.PROP_KEY_RSSR);
		initComponents(configFile);
	}



	private void initComponents(File configFile) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(Constants.NAME);
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent we) {
				runnableList.forEach(runnable->{
					runnable.setAlive(false);
				});
			}

		});

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
				ItemDto item = list.getSelectedValue();
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
				RssRunnable runnable = new RssRunnable(this, key);
				runnableList.add(runnable);
				Thread thread = new Thread(runnable);
				thread.setDaemon(true);
				thread.start();
			}
		});
		pack();
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	public void setChannel(ChannelDto channel) {
		synchronized (this) {
			List<ItemDto> tempList = new ArrayList<>();
			ItemDto before = list.getSelectedValue();
			if (!channelList.contains(channel)) {
				channelList.add(channel);
			} else {
				channelList.forEach((c)->{
					if (c.equals(channel)) {
						channel.getItemList().forEach((item)->{
							if (!c.getItemList().contains(item)) {
								c.getItemList().add(item);
							}
						});
					}
				});
			}

			channelList.forEach((c)-> {
				c.getItemList().forEach((item) -> {
					tempList.add(item);
				});
			});
			SwingUtilities.invokeLater(() -> {
				tempList.sort(new Comparator<ItemDto>() {
					@Override
					public int compare(ItemDto o1, ItemDto o2) {
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

				DefaultListModel<ItemDto> model = new DefaultListModel<>();
				tempList.forEach((i)->{
					model.addElement(i);
				});
				list.setModel(model);
				list.setSelectedValue(before, true);

			});
		}
	}

}
