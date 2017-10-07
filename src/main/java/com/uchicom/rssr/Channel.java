// (c) 2017 uchicom
package com.uchicom.rssr;

import java.util.ArrayList;
import java.util.List;

import com.uchicom.rssr.dto.ItemDto;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Channel {

	private List<ItemDto> itemList = new ArrayList<>();

	public List<ItemDto> getItemList() {
		return itemList;
	}

	public void setItemList(List<ItemDto> itemList) {
		this.itemList = itemList;
	}
}
