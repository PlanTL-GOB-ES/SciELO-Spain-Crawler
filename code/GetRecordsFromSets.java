package Crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GetRecordsFromSets {
	
	static final String SCIELOoaiURL = "http://scielo.isciii.es/oai/scielo-oai.php";
	static final String setsFile = "/tmp/scielo-sets.txt";
	
	private String corpusDirectory;
	private String defaultSet = null;
	
	private String DCdir;
	
	private List<String> sets;
	
	private List<String> newRecords;
	
	private int docCounter;
	
	
	public GetRecordsFromSets(String corpusDirectory)
	{
		this.corpusDirectory = corpusDirectory;
		sets = new ArrayList<String>();
		newRecords = new ArrayList<String>();
		DCdir = new String();
		docCounter = 0;
	}
	
	public GetRecordsFromSets(String corpusDirectory, String defaultSet)
	{
		this.corpusDirectory = corpusDirectory;
		this.defaultSet = defaultSet;
		sets = new ArrayList<String>();
		newRecords = new ArrayList<String>();
		DCdir = new String();
		docCounter = 0;
	}
	
	public void readSetsFile() throws IOException, ParserConfigurationException, SAXException, TransformerException 
	{
		BufferedReader reader = new BufferedReader(new FileReader(setsFile));
		String line = "";
		while ((line = reader.readLine()) != null)
		{
			sets.add(line);
		}
		reader.close();
	}
	
	public void analyzeSets(String lastDate) throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		DCdir = corpusDirectory + File.separator + "dublin_core_records";
		File theDir = new File(DCdir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
	
		for (int i = 0; i < sets.size(); i++)
		{
			String set = sets.get(i);
						
			System.out.println("Extracting records from set " + set);
			
			getXML(set, lastDate, null);
			String resumptionToken = readXML(set);
			
			while (resumptionToken != null)
			{
				getXML(set, lastDate, resumptionToken);
				resumptionToken = readXML(set);
			}
		}
	}
	
	public void analyzeSet(String lastDate) throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		DCdir = corpusDirectory + File.separator + "dublin_core_records";
		File theDir = new File(DCdir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
						
		String setDir = DCdir + File.separator + defaultSet;
		File theSetDir = new File(setDir);
		if (!theSetDir.exists()) 
		{
			theSetDir.mkdir();
		}
		
		System.out.println("Extracting records from set " + defaultSet);

		getXML(defaultSet, lastDate, null);
		String resumptionToken = readXML(defaultSet);
		
		while (resumptionToken != null)
		{
			getXML(defaultSet, lastDate, resumptionToken);
			resumptionToken = readXML(defaultSet);
		}
		
	}
	
	public void getXML(String set, String lastDate, String resumptionToken) throws IOException
	{
		String URLdir = new String();
		if (lastDate == null)
		{
			if (resumptionToken == null)
			{
				URLdir = SCIELOoaiURL + "?verb=ListRecords&set=" + set + "&metadataPrefix=oai_dc";
			}
			else
			{
				URLdir = SCIELOoaiURL + "?verb=ListRecords&set=" + set + "&metadataPrefix=oai_dc&resumptionToken=" + resumptionToken;
			}
		}
		else
		{
			if (resumptionToken == null)
			{
				URLdir = SCIELOoaiURL + "?verb=ListRecords&set=" + set + "&from=" + lastDate + "&metadataPrefix=oai_dc";
			}
			else
			{
				URLdir = SCIELOoaiURL + "?verb=ListRecords&set=" + set + "&from=" + lastDate + "&resumptionToken=" + resumptionToken;
			}
		}
		
		URL scieloSet = new URL(URLdir);
//		System.err.println(scieloSet.toString());
        BufferedReader in = new BufferedReader(new InputStreamReader(scieloSet.openStream()));

        BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/set_" + set + ".xml"));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
        {
        	writer.write(inputLine + "\n");
        }           
        in.close();
        writer.close();
	}
	
	public String readXML(String set) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		File inputFile = new File("/tmp/set_" + set + ".xml"); 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(inputFile);
        document.getDocumentElement().normalize();
        
        NodeList nList = document.getElementsByTagName("record");
        for (int i = 0; i < nList.getLength(); i++) 
        {     	
        	Node recordNode = nList.item(i);
        	
        	Element eNode = (Element) recordNode;
        	NodeList lNode = eNode.getElementsByTagName("dc:language");
        	String language = lNode.item(0).getTextContent();
        	
        	if (language.equals("es"))
        	{
        		Node headerNode = recordNode.getFirstChild();
            	String identifier = headerNode.getFirstChild().getTextContent();
            	
            	saveRecordDublinCore(recordNode, set, identifier);
        	}       	
        }
        
        try
        {
        	Node resumptionNode = document.getElementsByTagName("resumptionToken").item(0);
            String resumptionToken = resumptionNode.getTextContent();
            
            if (resumptionToken.equals(""))
            {
            	return null;
            }   
            else
            {
            	return resumptionToken;
            }
        }
        catch (Exception e)
        {
        	Node error = document.getElementsByTagName("error").item(0);
        	if (error == null)
        	{
        		System.out.println("Reached the end of set " + set);
        	}
        	else
        	{
        		String code = error.getAttributes().getNamedItem("code").getNodeValue();
            	if (code.equals("noRecordsMatch"))
            	{
            		System.out.println("No new records found at set " + set);
            	}
            	else
            	{
            		System.err.println("Error code " + code);            		
            	}        	
        	}        	
        	return null;
        }
	}
	
	public void saveRecordDublinCore(Node node, String set, String identifier) throws ParserConfigurationException, TransformerException
	{
		String setDir = DCdir + File.separator + set;
		File theSetDir = new File(setDir);
		if (!theSetDir.exists()) 
		{
			theSetDir.mkdir();
		}
		
		String fileName = DCdir + File.separator + set + File.separator + identifier + ".xml";
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		// initial lines
		Document doc = docBuilder.newDocument();
		
		// root elementsvoid
		Element rootElement = doc.createElement("scielo-document");		
		doc.appendChild(rootElement);
		
		Node importedNode = doc.importNode(node, true);		
		rootElement.appendChild(importedNode);
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
		
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	//	transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://dublincore.org/2000/12/01-dcmes-xml-dtd.dtd");
		
		transformer.transform(source, result);
	//	System.out.println("Dublin Core created for record " + identifier);
		
		docCounter++;
		newRecords.add(set + File.separator + identifier + ".xml");
	}
	
/*	public void jumpToNext(String set, String resumptionToken) throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		getXML(set, resumptionToken);
		readXML(set);
	}*/
	
	public int returnDocCounter()
	{
		return docCounter;
	}
	
	public void saveDate() throws IOException
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		System.out.println("Date saved: " + dateFormat.format(date));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(corpusDirectory + File.separator + "last_date.txt"));
		writer.write(dateFormat.format(date));
		writer.close();
	}
	
	public String getLastDate()
	{		
		try
		{
			String date = new String();
			BufferedReader reader = new BufferedReader(new FileReader(corpusDirectory + File.separator + "last_date.txt"));
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				date = line;
			}
			reader.close();
			
			return date;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public void saveNewRecords() throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/scielo_new_records.txt"));
		for (int i = 0; i < newRecords.size(); i++)
		{
			String record = newRecords.get(i);
			writer.write(record + "\n");
		}
		writer.close();
	}
}
