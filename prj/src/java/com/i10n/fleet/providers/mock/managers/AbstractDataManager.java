package com.i10n.fleet.providers.mock.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.XPathUtils;

/**
 * Mock : an abstract class for all the {@link IDataManager}. contains common
 * functionalities for all the {@link IDataManager}. This class will be removed
 * in future
 * 
 * @author Sabarish
 * 
 */
public abstract class AbstractDataManager {

	private Document m_document = null;

	/**
	 * DATA Type that an xml node will have to determine how the child nodes are
	 * to be stored.
	 * 
	 * @author Sabarish
	 * 
	 */
	private static enum XML_DATA_TYPE {
		array, map
	}

	private static final Logger LOG = Logger.getLogger(AbstractDataManager.class);

	/**
	 * parses the global document given and sets it baack.
	 */
	private void parseDocument() {
		Document doc = null;
		String documentName = getDocumentName();
		if (null != documentName) {
			InputStream stream = AbstractDataManager.class
			.getResourceAsStream(documentName);
			if (null != stream) {
				try {
					DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
					doc = docBuilder.parse(stream);
				}
				catch (ParserConfigurationException e) {
					LOG.error("Caught Parser Configuration Exception while building Document Builder Factory",e);
				}
				catch (SAXException e) {
					LOG.error("Caught SAXException while parsing vehicles document", e);
				}
				catch (IOException e) {
					LOG.error("Caught IOException while parsing vehicles document", e);
					e.printStackTrace();
				}
				finally {
					IOUtils.closeQuietly(stream);
				}
			}
		}
		synchronized (this) {
			if (null == m_document) {
				m_document = doc;
			}
		}
	}

	/**
	 * Returns the document representing the data manager. if the document is
	 * null , it parses and then returns the document
	 * 
	 * @return
	 */
	public Document getDocument() {
		if (null == m_document) {
			parseDocument();
		}
		return m_document;
	}

	/**
	 * Sets the current document
	 * 
	 * @param doc
	 */
	public void setDocument(Document doc) {
		m_document = doc;
	}

	/**
	 * Returns the document resource name for parsing the data
	 * 
	 * @return
	 */
	protected abstract String getDocumentName();

	/**
	 * converts the node to dataset using the options.
	 * 
	 * @param root
	 * @param options
	 * @return
	 */
	protected IDataset getNodeData(Node root, IDataset options) {
		IDataset result = new Dataset();
		List<Node> nodes = XPathUtils.getNodes(root, "*");
		for (Node node : nodes) {
			String name = node.getNodeName();
			String value = node.getTextContent();
			if (!isSkippable(name, options) && null != value) {
				value = value.trim();
				result.put(name, value);
			}
		}
		return result;
	}

	/**
	 * checks if a given nodename can be skipped to be parsed.
	 * 
	 * @param nodeName
	 * @param options
	 * @return
	 */
	protected boolean isSkippable(String nodeName, IDataset options) {
		boolean result = false;
		result = null != options && null != options.get("skip." + nodeName)
		&& options.getBoolean("skip." + nodeName);
		return result;
	}

	/**
	 * checks if the given data passes the filter given as options.
	 * 
	 * @param data
	 * @param options
	 * @return
	 */
	protected boolean isFiltered(IDataset data, IDataset options) {
		boolean result = true;
		if (null != options && null != options.get("filter")) {
			for (Entry<String, Object> entry : options.getDataset("filter").entrySet()) {
				if (null != data.getValue(entry.getKey())
						&& !data.getValue(entry.getKey()).equals(entry.getValue())) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Converts a given node to {@link IDataset}. if a node has an attribute
	 * "if" then the attribute value is used, else a node name is used as a key
	 * for child elements. if a node has attribute type with value array , then
	 * the child elements are store as lists. rest of the cases they are stored
	 * as maps. The implementation is neither tested nor efficient and certainly
	 * not of production level, hence must not be used outside the mock area
	 * 
	 * @param node
	 * @return
	 */
	protected IDataset getXMLDataset(Node node) {
		IDataset dataset = new Dataset();
		String idAttr = XPathUtils.getAttribute(node, "id");
		if (idAttr == null || idAttr.trim().length() == 0) {
			idAttr = node.getNodeName();
		}
		String childAttrType = XPathUtils.getAttribute(node, "type");
		XML_DATA_TYPE childType = null;
		if (childAttrType == null || childAttrType.trim().length() == 0) {
			childType = XML_DATA_TYPE.map;
		}
		else {
			childType = XML_DATA_TYPE.valueOf(childAttrType);
		}

		if (XML_DATA_TYPE.map.equals(childType)) {
			handleMapData(idAttr, node, dataset);

		}
		else if (XML_DATA_TYPE.array.equals(childType)) {
			handleArrayData(idAttr, node, dataset);
		}
		return dataset;
	}

	/**
	 * handles the data that is supposed to be stored as maps.
	 * 
	 * @param id
	 * @param node
	 * @param dataset
	 */
	private void handleMapData(String id, Node node, IDataset dataset) {
		String trID = id.replace(".", "_");
		List<Node> children = XPathUtils.getNodes(node, "*");
		IDataset childData = new Dataset();
		NamedNodeMap attrNodes = node.getAttributes();
		if (attrNodes != null) {
			for (int i = 0; i < attrNodes.getLength(); i++) {
				Node attrNode = attrNodes.item(i);

				if (!attrNode.getNodeName().equals("id")
						&& !attrNode.getNodeName().equals("type")) {
					childData.put(attrNode.getNodeName(), attrNode.getNodeValue());
				}
			}
		}
		if (children.size() > 0) {
			for (Node child : children) {
				childData.putAll(getXMLDataset(child));
			}
		}
		if (childData.isEmpty()) {
			String nodeValue = node.getTextContent();
			if (null != nodeValue) {
				dataset.put(trID, nodeValue.trim());
			}
		}
		else {
			dataset.put(trID, childData);
		}
	}

	/**
	 * handles the data that is supposed to be stored as lists
	 * 
	 * @param id
	 * @param node
	 * @param dataset
	 */
	private void handleArrayData(String id, Node node, IDataset dataset) {
		List<Node> children = XPathUtils.getNodes(node, "*");
		if (children.size() > 0) {
			List<IDataset> data = new ArrayList<IDataset>();
			for (Node child : children) {
				IDataset childData = getXMLDataset(child);
				data.add(childData.getDataset(child.getNodeName()));
			}
			dataset.put(id.replace(".", "_"), data);
		}
	}

	/**
	 * Filters/Decorates the result data based on the options given.
	 * 
	 * @param data
	 * @param options
	 * @return
	 */
	protected IDataset processResult(IDataset data, IDataset options) {
		IDataset result = data;
		if (null != options) {
			IDataset filteredData = filterResult(data, options);
			result = skipResult(filteredData, options);
		}
		return result;
	}

	/**
	 * removes the data not needed based on the options given. all such
	 * removeable fields are places inside the options maps as
	 * options.skip.{fieldname} = true
	 * 
	 * @param data
	 * @param options
	 * @return
	 */
	private IDataset skipResult(IDataset data, IDataset options) {
		IDataset result = new Dataset();
		IDataset skip = options.getDataset("skip");
		if (null != skip && skip.size() > 0) {
			for (Entry<String, Object> dataEntry : data.entrySet()) {
				String dataKey = dataEntry.getKey();
				IDataset dataValue = (IDataset) dataEntry.getValue();
				IDataset innerData = new Dataset();
				for (Entry<String, Object> dataValEntry : dataValue.entrySet()) {
					boolean isSkipped = skip.getBoolean(dataValEntry.getKey());
					if (!isSkipped) {
						innerData.put(dataValEntry.getKey(), dataValEntry.getValue());
					}
				}
				result.put(dataKey, innerData);
			}
		}
		else {
			result = data;
		}
		return result;
	}

	/**
	 * filters the data based on the options given. all such filters are given
	 * as options.filter.{fieldname} = {required filed value}
	 * 
	 * @param data
	 * @param options
	 * @return
	 */
	private IDataset filterResult(IDataset data, IDataset options) {
		IDataset result = new Dataset();
		IDataset filter = options.getDataset("filter");
		if (null != filter && filter.size() > 0) {
			for (Entry<String, Object> dataEntry : data.entrySet()) {
				String dataKey = dataEntry.getKey();
				IDataset dataValue = (IDataset) dataEntry.getValue();
				boolean isFiltered = true;
				for (Entry<String, Object> filterEntry : filter.entrySet()) {
					String dataFilterValue = dataValue.getValue(filterEntry.getKey());
					if (null == dataFilterValue
							|| !dataFilterValue.equals(filterEntry.getValue())) {
						isFiltered = false;
						break;
					}
				}
				if (isFiltered) {
					result.put(dataKey, dataValue);
				}
			}
		}
		else {
			result = data;
		}
		return result;
	}
}
