// (c) 2017 uchicom
package com.uchicom.rssr.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ChannelDto {

	private List<ItemDto> itemList = new ArrayList<>();

	public List<ItemDto> getItemList() {
		return itemList;
	}

	public void setItemList(List<ItemDto> itemList) {
		this.itemList = itemList;
	}
}
