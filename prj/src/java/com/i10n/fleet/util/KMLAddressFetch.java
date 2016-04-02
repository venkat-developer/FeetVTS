package com.i10n.fleet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.Point;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.i10n.db.dao.AddressDaoimpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.Address;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class KMLAddressFetch {
	private static final Logger LOG = Logger.getLogger(KMLAddressFetch.class);

	public static void processForKMLAddressInsertion(File fileName) {
		FileInputStream instream = null;
		String fname= fileName.getAbsolutePath();
		LOG.info("file name is "+fname);
		InputSource is=null;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			xmlReader.setContentHandler(new MyContentHandler());
			instream = new FileInputStream(fname);
			is = new InputSource(instream);
			xmlReader.parse(is);
		}catch( IOException e) {
			LOG.error("Error while Reading File",e);
		} catch( SAXException e) {
			LOG.error("Error while Parsing KML File",e);
		} catch( ParserConfigurationException e) {
			LOG.error("Error while ParserConfigurationException File",e);
		} finally {
			if(instream != null)
				try {
					instream.close();
				} catch (IOException e) {
					LOG.error("Error While Closeing the file stream",e);
				}
		}
	}
}

class MyContentHandler extends XMLFilterImpl {
	private static final Logger LOG = Logger.getLogger(MyContentHandler.class);

	int endPlaceMarker=1;
	boolean printMe = false;
	StringBuffer data=new StringBuffer();
	String st;

	public void characters(char[] ch, int start, int length) {
		String gotString = new String(ch, start, length);
		if (printMe) {
			if (! gotString.trim().equals("")) {
				st=gotString+",";
				data.append(st);
			}
		}
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) {
		if (localName.equals("Placemark")) {
			//int attrsLength = attributes.getLength();
		}
		if (localName.equals("name") || localName.equals("description") || localName.equals("coordinates")){
			printMe=true;
		} else 
			printMe=false;
	}

	public void endElement(String namespaceURI,String localName,String qName){
		if (localName.equals("Placemark")) {
			processForInsertion(data.toString().trim());
			data=new StringBuffer();
		}
	}

	public static void processForInsertion(String addressString)  {
		String[] addressArray=addressString.split(",");
		Address addressEntity = new Address();
		addressEntity.setCountry("India");
		if(addressArray.length >= 4){
			LOG.info("Line 2 exist in KML ");
			addressEntity.setLine1(addressArray[0]);
			addressEntity.setLine2(addressArray[1]);
			LOG.info("Line 2 "+addressArray[1]);
			addressEntity.setLat(new Double(addressArray[3]));
			addressEntity.setLng(new Double(addressArray[2]));	
		}else{
			LOG.info("Line 2 does not exist in KML ");
			addressEntity.setLine1(addressArray[0]);
			addressEntity.setLine2(" ");
			addressEntity.setLat(new Double(addressArray[2]));
			addressEntity.setLng(new Double(addressArray[1]));
		}

		Point p = new Point(addressEntity.getLng(), addressEntity.getLat());
		Geometry location = (Geometry)p;
		addressEntity.setLatlon_point(location);
		try {
			((AddressDaoimpl)DBManager.getInstance().getDao(DAOEnum.ADDRESS_DAO)).insert(addressEntity);
		} catch (OperationNotSupportedException e) {
			LOG.error("Exception In KML Address Fetch ",e);
		}
	}
}


