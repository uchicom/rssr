// (c) 2017 uchicom
package com.uchicom.rssr;

import java.net.URL;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Item {

	private String title;
	private URL link;
	private String description;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public URL getLink() {
		return link;
	}
	public void setLink(URL link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
