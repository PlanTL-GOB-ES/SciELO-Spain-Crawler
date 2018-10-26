package Crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class GetAllSets {

	static final String SCIELOoaiURL = "http://scielo.isciii.es/oai/scielo-oai.php";
	static final String SCIELOsetsFile = "/tmp/scielo-sets.xml";
	
	private List<String> sets;
	
	private Map<String, String> licenses;
	
	public GetAllSets(Map<String, String> licenses)
	{
		sets = new ArrayList<String>();
		this.licenses = licenses;
	}

	public void getURL() throws IOException
	{
		System.out.println("Getting Scielo journals.");
		
		String listSetsURL = new String(SCIELOoaiURL + "?verb=ListSets");
		
		URL scielo = new URL(listSetsURL);
        BufferedReader in = new BufferedReader(new InputStreamReader(scielo.openStream()));

        BufferedWriter writer = new BufferedWriter(new FileWriter(SCIELOsetsFile));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
        {
        	writer.write(inputLine + "\n");
        }           
        in.close();
        writer.close();
	}
	
	public void getSets() throws ParserConfigurationException, SAXException, IOException
	{
		System.out.println("Parsing Scielo journals XML.");
		
		try
		{
			File inputFile = new File(SCIELOsetsFile); 
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(inputFile);
	        document.getDocumentElement().normalize();
			
	        NodeList nList = document.getElementsByTagName("set");
	        for (int i = 0; i < nList.getLength(); i++) 
	        {
	        	Node nNode = nList.item(i);

	        	NodeList arrList = nNode.getChildNodes();
	        	for (int j = 0; j < arrList.getLength(); j++)
	        	{
	        		Node arrNode = arrList.item(j);
	        		String nodeName = arrNode.getNodeName();
	        		if (nodeName.equals("setSpec"))
	        		{
	        			String setID = arrNode.getTextContent();
	        			if (licenses.containsKey(setID))
	        			{
	        				sets.add(setID);
	        			}	        			
	        		}
	        	}
	        }
		}
		catch (SAXParseException e)
		{
			System.err.println("Error parsing journals.");
		}
		
		System.out.println(sets.size() + " journals found.");
	}
	
	public void printSets() throws IOException
	{
		System.out.println("Saving Scielo journals info.");
		
		 BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/scielo-sets.txt"));
		 for (int i = 0; i < sets.size(); i++)
		 {
			 String set = sets.get(i);
			 writer.write(set + "\n");
		 }
		 writer.close();
		 
		 
	}
}
