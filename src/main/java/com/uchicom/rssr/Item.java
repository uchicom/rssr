// (c) 2017 uchicom
package com.uchicom.rssr;

import java.net.URL;
import java.util.Date;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Item {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		return true;
	}
	private String title;
	private URL link;
	private String description;
	private Date pubDate;
	private String guid;
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
		if (this.description != null) {
			this.description += description;
		} else {
			this.description = description;
		}
	}

	/**
	 * pubDateを取得します.
	 *
	 * @return pubDate
	 */
	public Date getPubDate() {
		return pubDate;
	}
	/**
	 * pubDateを設定します.
	 *
	 * @param pubDate pubDate
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String toString() {
		return title + pubDate;
	}
}
