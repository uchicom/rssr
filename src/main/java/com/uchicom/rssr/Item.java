// (c) 2017 uchicom
package com.uchicom.rssr;

import java.net.URL;
import java.util.Date;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Item {

	/* (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((pubDate == null) ? 0 : pubDate.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
//		if (pubDate == null) {
//			if (other.pubDate != null)
//				return false;
//		} else if (!pubDate.equals(other.pubDate))
//			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	private String title;
	private URL link;
	private String description;
	private Date pubDate;
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
	public String toString() {
		return title + pubDate;
	}
}
