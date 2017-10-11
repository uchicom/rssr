// (c) 2017 uchicom
package com.uchicom.rssr.dto;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ChannelDto {
	private URL url;
	public ChannelDto(URL url) {
		this.url = url;
	}

	private List<ItemDto> itemList = new ArrayList<>();

	public List<ItemDto> getItemList() {
		return itemList;
	}

	public void setItemList(List<ItemDto> itemList) {
		this.itemList = itemList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		ChannelDto other = (ChannelDto) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
