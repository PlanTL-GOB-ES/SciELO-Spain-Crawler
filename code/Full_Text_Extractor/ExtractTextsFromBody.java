package Crawler.Full_Text_Extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import Crawler.File_Managers.XMLreader;

public class ExtractTextsFromBody {

	private String corpusDirectory;
	
	private List<String> newRecords;
	
	private int numRecords;
	private int failedRecords;
	
	public ExtractTextsFromBody(String corpusDirectory)
	{
		this.corpusDirectory = corpusDirectory;
		newRecords = null;
		numRecords = 0;
		failedRecords = 0;
	}
	
	public ExtractTextsFromBody(String corpusDirectory, boolean update) throws IOException 
	{
		this.corpusDirectory = corpusDirectory;
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

	public void start() throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		String XMLdir = corpusDirectory + File.separator + "records";
		String textDir = corpusDirectory + File.separator + "full_texts";
		
		File theDir = new File(textDir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
		
		File xmld = new File(XMLdir);
		File xmlFolders[] = xmld.listFiles();
		for (int i = 0; i < xmlFolders.length; i++)
		{
			String folderName = xmlFolders[i].getName();
			System.out.println("Getting full text from publications at set " + folderName);
			
			File files[] = new File(XMLdir + File.separator + folderName).listFiles();
			for (int j = 0; j < files.length; j++)
			{
				String fileName = files[j].getName();
				
				if (newRecords == null)
				{
					List<String> bodyList = readXML(XMLdir + File.separator + folderName + File.separator + fileName);
					
					if (bodyList == null)
					{
						failedRecords++;
					}
					else
					{
						List<String> bodyTexts = extractCleanRawText(bodyList);	
						
						printCleanRawText(textDir, folderName, fileName, bodyTexts);
						List<List<String>> paragraphSentences = splitSentencesRawText(textDir, folderName, fileName.replace(".xml", ".txt"));
						
						printFullTextXML(textDir, folderName, fileName, paragraphSentences);
						
						numRecords++;
					}
				}
				else
				{
					String newInfo = folderName + File.separator + "oai:scielo:" + fileName;
					if (newRecords.contains(newInfo))
					{
						List<String> bodyList = readXML(XMLdir + File.separator + folderName + File.separator + fileName);
						
						if (bodyList == null)
						{
							failedRecords++;
						}
						else
						{
							List<String> bodyTexts = extractCleanRawText(bodyList);	
							
							printCleanRawText(textDir, folderName, fileName, bodyTexts);
							List<List<String>> paragraphSentences = splitSentencesRawText(textDir, folderName, fileName.replace(".xml", ".txt"));
							
							printFullTextXML(textDir, folderName, fileName, paragraphSentences);
							
							numRecords++;
						}
					}
				}				
			}
		}
	}
	
	public void printCleanRawText(String textDir, String setName, String fileName, List<String> bodyTexts) throws IOException 
	{
		String cleanTextDir = textDir + File.separator + "clean_raw_text";
		File mainDir = new File(cleanTextDir);
		if (!mainDir.exists()) 
		{
			mainDir.mkdir();
		}
		
		String cleanTextSetDir = textDir + File.separator + "clean_raw_text" + File.separator + setName;
		File theDir = new File(cleanTextSetDir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(cleanTextSetDir + File.separator + fileName.replace(".xml", ".txt")));
		for (int i = 0; i < bodyTexts.size(); i++)
		{
			String text = bodyTexts.get(i);
			writer.write(text + "\n");
		}
		writer.close();
	}
	
	public List<List<String>> splitSentencesRawText(String textDir, String setName, String fileName) throws IOException
	{
		String cleanTextSetDir = textDir + File.separator + "clean_raw_text" + File.separator + setName;
		
		List<String> paragraphs = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(cleanTextSetDir + File.separator + fileName));
		String line = "";
		while ((line = reader.readLine()) != null)
		{
			paragraphs.add(line);
		}
		reader.close();
		
		List<List<String>> paragraphSentences = new ArrayList<List<String>>();
		for (int i = 0; i < paragraphs.size(); i++)
		{
			String paragraph = paragraphs.get(i);
			List<String> sentences = FullTextManagement.splitSentences(paragraph);
			paragraphSentences.add(sentences);
		}
		
		return paragraphSentences;
	}
	
	public void printFullTextXML(String textDir, String setName, String fileName, List<List<String>> paragraphSentences) throws ParserConfigurationException, TransformerException
	{
		String xmlDir = textDir + File.separator + "clean_xml_text";
		File theDir = new File(xmlDir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
		
		String xmlTextSetDir = xmlDir + File.separator + setName;
		File theDir2 = new File(xmlTextSetDir);
		if (!theDir2.exists()) 
		{
			theDir2.mkdir();
		}
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("full_text");
		doc.appendChild(rootElement);
		
		// set and ID 
		Element set = doc.createElement("set");
		set.appendChild(doc.createTextNode(setName));
		rootElement.appendChild(set);
		
		Element ID = doc.createElement("ID");
		ID.appendChild(doc.createTextNode(fileName));
		rootElement.appendChild(ID);
		
		int offsetStart = 0;
		
		// paragraphs and sentences
		for (int i = 0; i < paragraphSentences.size(); i++)
		{
			List<String> sentences = paragraphSentences.get(i);
			
			Element paragraph = doc.createElement("paragraph");
			Attr attr = doc.createAttribute("id");
			attr.setValue("P" + (i + 1));
			paragraph.setAttributeNode(attr);
			Attr attr2 = doc.createAttribute("start");
			attr2.setValue("" + offsetStart);
			paragraph.setAttributeNode(attr2);
			rootElement.appendChild(paragraph);
			
			for (int j = 0; j < sentences.size(); j++)
			{
				String sentence = sentences.get(j);
				if (sentence.startsWith(" "))
				{
					sentence = sentence.substring(1);
				}
				
				Element sentenceNode = doc.createElement("sentence");
				sentenceNode.appendChild(doc.createTextNode(sentence));
				Attr attr3 = doc.createAttribute("id");
				attr3.setValue("P" + (i + 1) + ".S" + (j + 1));
				sentenceNode.setAttributeNode(attr3);
				Attr attr4 = doc.createAttribute("start");
				attr4.setValue("" + offsetStart);
				sentenceNode.setAttributeNode(attr4);
				paragraph.appendChild(sentenceNode);
								
				offsetStart = offsetStart + sentence.length() + 1;
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(xmlTextSetDir + File.separator + fileName));
		
		transformer.transform(source, result);
	}

	public List<String> readXML(String xmlfile) throws ParserConfigurationException, SAXException, IOException
	{
		XMLreader xmlreader = new XMLreader(xmlfile);
		boolean loadSuccessful = xmlreader.loadXML();
		
		if (loadSuccessful)
		{
			List<String> bodyList = xmlreader.returnBodies();
			if (bodyList == null)
			{
				return new ArrayList<String>();
			}
			else
			{
				return bodyList;
			}			
		}
		else
		{
			return null;
		}
	}
	
	public List<String> extractCleanRawText(List<String> bodyList)
	{
		List<String> bodyTexts = new ArrayList<String>();
		for (int i = 0; i < bodyList.size(); i++)
		{
			String text = bodyList.get(i);
			String textNoHTML = FullTextManagement.cleanHTML(text);
			bodyTexts.add(textNoHTML);
		}		
		return bodyTexts;
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
