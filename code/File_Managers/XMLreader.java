package Crawler.File_Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLreader {

	private String xmlfile;
	
	private Document document;
	
	public XMLreader(String xmlfile)
	{
		this.xmlfile = xmlfile;
		
		document = null;
	}
	
	public boolean loadXML() throws ParserConfigurationException, SAXException, IOException
	{
		File inputFile = new File(xmlfile); 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        try
        {
        	Document document = dBuilder.parse(inputFile);
            document.getDocumentElement().normalize();
            
            this.document = document;
            
            return true;
        }
        catch (SAXParseException e)
        {
        	System.err.println("XML coding error in file " + xmlfile);
        	System.err.println("Parsing interrupted.");
        	
        	return false;
        }		
	}

	public String returnTitleSpanish()
	{
		String titleCatalan = null;
		
		Node titleGroup = document.getElementsByTagName("title-group").item(0);
		Element eNode = (Element) titleGroup;
		
		NodeList titles = eNode.getElementsByTagName("article-title");
		for (int i = 0; i < titles.getLength(); i++)
		{
			Node title = titles.item(i);
			String language = title.getAttributes().getNamedItem("xml:lang").getNodeValue();
			if (language.equals("es"))
			{
				titleCatalan = title.getTextContent();
			}
		}
		
		return titleCatalan;
	}
	
	public String returnTitleEnglish()
	{
		String titleEnglish = null;
		
		Node titleGroup = document.getElementsByTagName("title-group").item(0);
		Element eNode = (Element) titleGroup;
		
		NodeList titles = eNode.getElementsByTagName("article-title");
		for (int i = 0; i < titles.getLength(); i++)
		{
			Node title = titles.item(i);
			String language = title.getAttributes().getNamedItem("xml:lang").getNodeValue();
			if (language.equals("en"))
			{
				titleEnglish = title.getTextContent();
			}
		}
		
		return titleEnglish;
	}
	
	public String returnAbstractSpanish()
	{
		String abstractCatalan = null;
		
		NodeList abstracts = document.getElementsByTagName("abstract");
		for (int i = 0; i < abstracts.getLength(); i++)
		{
			Node abs = abstracts.item(i);
			String language = abs.getAttributes().getNamedItem("xml:lang").getNodeValue();
			if (language.equals("es"))
			{
				abstractCatalan = abs.getTextContent();
			}
		}
		
		return abstractCatalan;
	}
	
	public String returnAbstractEnglish()
	{
		String abstractEnglish= null;
		
		NodeList abstracts = document.getElementsByTagName("abstract");
		for (int i = 0; i < abstracts.getLength(); i++)
		{
			Node abs = abstracts.item(i);
			String language = abs.getAttributes().getNamedItem("xml:lang").getNodeValue();
			if (language.equals("en"))
			{
				abstractEnglish = abs.getTextContent();
			}
		}
		
		return abstractEnglish;
	}
	
	public List<String> returnBodies()
	{
		List<String> bodyList = null;
		
		NodeList bodies = document.getElementsByTagName("body");
		if (bodies.getLength() > 0)
		{
			bodyList = new ArrayList<String>();
		}
		
		for (int i = 0; i < bodies.getLength(); i++)
		{
			Node bodyNode = bodies.item(i);
			String body = bodyNode.getTextContent();
			
			bodyList.add(body);
		}
		
		return bodyList;
	}
	
	public List<String> returnNodeDublinCore(String nodeName)
	{
		List<String> nodeList = null;
		
		NodeList nodes = document.getElementsByTagName(nodeName);
		if (nodes.getLength() > 0)
		{
			nodeList = new ArrayList<String>();
		}
		
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node bodyNode = nodes.item(i);
			String body = bodyNode.getTextContent();
			
			nodeList.add(body);
		}
		
		return nodeList;
	}
}
