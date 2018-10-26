package Crawler.File_Managers;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;

public class DCextender {

	private String dcfile;
	
	private Document document;
	
	public DCextender(String dcfile)
	{
		this.dcfile = dcfile;
		
		document = null;
	}
	
	public void loadDC() throws ParserConfigurationException, SAXException, IOException
	{
		File inputFile = new File(dcfile); 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(inputFile);
        document.getDocumentElement().normalize();
        
        this.document = document;
	}
	
	public void setTitleSpanish(String titleSpanish)
	{
		if (titleSpanish!= null)
		{
			// remove original title (if exists)
		/*	Element element = (Element) document.getElementsByTagName("dc:title").item(0);
			if (element != null || element.hasAttribute("xml:lang"))
			{
				element.getParentNode().removeChild(element);
			}*/
			
			// create new node
			Element newTitleSpanish= document.createElement("dc:title");
			newTitleSpanish.setAttribute("xml:lang", "es");
			CDATASection cdata = document.createCDATASection(titleSpanish);
			newTitleSpanish.appendChild(cdata);
			
			// append new node
			Element metadata = (Element) document.getElementsByTagName("oai-dc:dc").item(0);
			metadata.appendChild(newTitleSpanish);	
		}	
	}
	
	public void setTitleEnglish(String titleEnglish)
	{
		if (titleEnglish != null)
		{
			// remove original title (if exists)
		/*	Element element = (Element) document.getElementsByTagName("dc:title").item(0);
			if (element != null) // || element.hasAttribute("xml:lang"))
			{
				element.getParentNode().removeChild(element);
			}			*/
			
			// create new node
			Element newTitleEnglish = document.createElement("dc:title");
			newTitleEnglish.setAttribute("xml:lang", "en");
			CDATASection cdata = document.createCDATASection(titleEnglish);
			newTitleEnglish.appendChild(cdata);
			
			// append new node
			Element metadata = (Element) document.getElementsByTagName("oai-dc:dc").item(0);
			metadata.appendChild(newTitleEnglish);	
		}		
	}
	
	public void setAbstractSpanish(String abstractSpanish)
	{
		if (abstractSpanish!= null)
		{
			// remove original abstract (if exists)
		/*	Element element = (Element) document.getElementsByTagName("dc:description").item(0);
			if (element != null || element.hasAttribute("xml:lang"))
			{
				element.getParentNode().removeChild(element);
			}		*/	
			
			// create new node
			Element newAbstractSpanish= document.createElement("dc:description");
			newAbstractSpanish.setAttribute("xml:lang", "es");		
			CDATASection cdata = document.createCDATASection(abstractSpanish);
			newAbstractSpanish.appendChild(cdata);
			
			// append new node
			Element metadata = (Element) document.getElementsByTagName("oai-dc:dc").item(0);
			metadata.appendChild(newAbstractSpanish);	
		}		
	}
	
	public void setAbstractEnglish(String abstractEnglish)
	{
		if (abstractEnglish != null)
		{
			// remove original abstract (if exists)	
		/*	Element element = (Element) document.getElementsByTagName("dc:description").item(0);
			if (element != null) // || element.hasAttribute("xml:lang"))
			{
				element.getParentNode().removeChild(element);
			}*/
			
			// create new node
			Element newAbstractEnglish = document.createElement("dc:description");
			newAbstractEnglish.setAttribute("xml:lang", "en");
			CDATASection cdata = document.createCDATASection(abstractEnglish);
			newAbstractEnglish.appendChild(cdata);
			
			// append new node
			Element metadata = (Element) document.getElementsByTagName("oai-dc:dc").item(0);
			metadata.appendChild(newAbstractEnglish);	
		}		
	}
	
	public void setRights(String license)
	{
		String licenseName = license.split("\t")[0];
		String licenseURL = license.split("\t")[1];
		
		Element licenseNode = document.createElement("dc:rights");
//		licenseNode.setAttribute("content", licenseURL);
//		licenseNode.setTextContent(licenseName);
		licenseNode.setTextContent(licenseURL);
		
		// append new node
		Element metadata = (Element) document.getElementsByTagName("oai-dc:dc").item(0);
		metadata.appendChild(licenseNode);	
	}
	
	public Document returnDocument()
	{
		return document;
	}
}
