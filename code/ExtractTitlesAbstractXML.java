package Crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Crawler.File_Managers.DCextender;
import Crawler.File_Managers.XMLreader;

public class ExtractTitlesAbstractXML {
	
	private String corpusDirectory;
	private Map<String, String> licenses;
	
	private List<String> newRecords;
	
	private int numRecords;
	private int failedRecords;
	
	public ExtractTitlesAbstractXML(String corpusDirectory, Map<String, String> licenses)
	{
		this.corpusDirectory = corpusDirectory;
		this.licenses = licenses;
		newRecords = null;
		numRecords = 0;
	}
	
	public ExtractTitlesAbstractXML(String corpusDirectory, Map<String, String> licenses, boolean update) throws IOException
	{
		this.corpusDirectory = corpusDirectory;
		this.licenses = licenses;
		if (update)
		{
			newRecords = getNewRecords();	
		}
		else
		{
			newRecords = null;
		}
		numRecords = 0;
		failedRecords = 0;
	}
	
	private List<String> getNewRecords() throws IOException 
	{
		newRecords = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader("/tmp/scielo_new_records.txt"));
		String line = "";
		while ((line = reader.readLine()) != null)
		{
			newRecords.add(line);
		}
		reader.close();
		return newRecords;
	}
	
	public void start() throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException
	{
		String DCdir = corpusDirectory + File.separator + "dublin_core_records";
		String XMLdir = corpusDirectory + File.separator + "records";
		String newDCdir = corpusDirectory + File.separator + "dublin_core_extended";
		
		File theDir = new File(newDCdir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
		
		File dcfolders[] = new File(DCdir).listFiles();
		for (int i = 0; i < dcfolders.length; i++)
		{
			String folderName = dcfolders[i].getName();
			System.out.println("Extended Dublin Core information for set " + folderName);
		
			String license = licenses.get(folderName);
			
			File theFolderDir = new File(newDCdir + File.separator + folderName);
			if (!theFolderDir.exists()) 
			{
				theFolderDir.mkdir();
			}
			
			File files[] = new File(DCdir + File.separator + folderName).listFiles();
			for (int j = 0; j < files.length; j++)
			{
				String fileName = files[j].getName().replace("oai:scielo:", "");
				
				if (newRecords == null)
				{
					Map<String, String> xmlInfo = readXML(XMLdir + File.separator + folderName + File.separator + fileName);
					
					if (xmlInfo == null)
					{
						failedRecords++;
					}
					else
					{
						Document newDC = editDC(DCdir + File.separator + folderName + File.separator + "oai:scielo:" + fileName, xmlInfo, license);
						
						newDC = removeOriginalTitleAbstractNodes(newDC);
						newDC = removeEmptyNodes(newDC);
						
						printExtendedDC(newDC, newDCdir + File.separator + folderName + File.separator + fileName);
						
						numRecords++;
					}
				}
				else
				{
					String newInfo = folderName + File.separator + "oai:scielo:" + fileName;
					if (newRecords.contains(newInfo))
					{
						Map<String, String> xmlInfo = readXML(XMLdir + File.separator + folderName + File.separator + fileName);
						
						if (xmlInfo == null)
						{
							failedRecords++;
						}
						else
						{
							Document newDC = editDC(DCdir + File.separator + folderName + File.separator + "oai:scielo:" + fileName, xmlInfo, license);
							
							newDC = removeOriginalTitleAbstractNodes(newDC);
							newDC = removeEmptyNodes(newDC);
							
							printExtendedDC(newDC, newDCdir + File.separator + folderName + File.separator + fileName);
							
							numRecords++;
						}
					}
				}
			}
		}
	}
	
	private Document removeOriginalTitleAbstractNodes(Document newDC) 
	{
		Element metadata = (Element) newDC.getElementsByTagName("metadata").item(0);
		
		NodeList titles = metadata.getElementsByTagName("dc:title");
		for (int i = 0; i < titles.getLength(); i++)
		{
			Node title = titles.item(i);
			if (!title.hasAttributes())
			{
				Element e = (Element) title;
				e.getParentNode().removeChild(title);
			}
		}

		NodeList abstracts = metadata.getElementsByTagName("dc:description");
		for (int i = 0; i < abstracts.getLength(); i++)
		{
			Node abs = abstracts.item(i);
			if (!abs.hasAttributes())
			{
				Element e = (Element) abs;
				e.getParentNode().removeChild(abs);
			}
		}
		
		return newDC;
	}
	
	private Document removeEmptyNodes(Document newDC) throws XPathExpressionException
	{
		XPathFactory xpathFactory = XPathFactory.newInstance();
		// XPath to find empty text nodes.
		XPathExpression xpathExp = xpathFactory.newXPath().compile(
		        "//text()[normalize-space(.) = '']");  
		NodeList emptyTextNodes = (NodeList) 
		        xpathExp.evaluate(newDC, XPathConstants.NODESET);

		// Remove each empty text node from document.
		for (int i = 0; i < emptyTextNodes.getLength(); i++) {
		    Node emptyTextNode = emptyTextNodes.item(i);
		    emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}
		
		return newDC;
	}

	public Map<String, String> readXML(String xmlfile) throws ParserConfigurationException, SAXException, IOException
	{
		XMLreader xmlreader = new XMLreader(xmlfile);
		boolean loadSuccessful = xmlreader.loadXML();
		
		if (loadSuccessful)
		{
			String titleSpanish = xmlreader.returnTitleSpanish();
			String titleEnglish = xmlreader.returnTitleEnglish();
			String abstractSpanish = xmlreader.returnAbstractSpanish();
			String abstractEnglish = xmlreader.returnAbstractEnglish();
			
			Map<String, String> xmlInfo = new HashMap<String, String>();
			xmlInfo.put("title-spanish", titleSpanish);
			xmlInfo.put("title-english", titleEnglish);
			xmlInfo.put("abstract-spanish", abstractSpanish);
			xmlInfo.put("abstract-english", abstractEnglish);
			
			return xmlInfo;
		}
		else
		{
			return null;
		}
	}
	
	public Document editDC(String dcfile, Map<String, String> xmlInfo, String license) throws ParserConfigurationException, SAXException, IOException
	{
		DCextender dcreader = new DCextender(dcfile);
		dcreader.loadDC();
		
		dcreader.setTitleSpanish(xmlInfo.get("title-spanish"));
		dcreader.setTitleEnglish(xmlInfo.get("title-english"));
		dcreader.setAbstractSpanish(xmlInfo.get("abstract-spanish"));
		dcreader.setAbstractEnglish(xmlInfo.get("abstract-english"));
		
		if (license != null)
		{
			dcreader.setRights(license);
		}		
		
		return dcreader.returnDocument();
	}
	
	public void printExtendedDC(Document newDC, String fileName) throws TransformerException
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(newDC);
		StreamResult result = new StreamResult(new File(fileName));
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}
	
	public int returnNumRecords()
	{
		return numRecords;
	}
	
	public void printNumFailed()
	{
		System.err.println("Failed records: " + failedRecords);
	}
}
