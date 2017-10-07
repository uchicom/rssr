// (c) 2017 uchicom
package com.uchicom.rssr;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public interface Handler {

	public void handle(XMLEvent event, XMLEventReader r);
}
