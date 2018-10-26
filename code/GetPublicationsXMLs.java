package Crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GetPublicationsXMLs {

	private String corpusDirectory;
	
	private List<String> newRecords;
	
	static final String xmlURL = "http://scielo.isciii.es/scieloOrg/php/articleXML.php?pid=";
	
	private int numRecords;
	
	public GetPublicationsXMLs(String corpusDirectory)
	{
		this.corpusDirectory = corpusDirectory;	
		newRecords = null;
		numRecords = 0;
	}
	
	public GetPublicationsXMLs(String corpusDirectory, boolean update) throws IOException
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
	
	public void getRecords() throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		System.out.println("Extracting new publication XMLs from Scielo.");
		
		String XMLdir = corpusDirectory + File.separator + "records";
		File theDir = new File(XMLdir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
		
		String DCdir = corpusDirectory + File.separator + "dublin_core_records"; 
		File dir = new File(DCdir);
		File folders[] = dir.listFiles();
		for (int i = 0; i < folders.length; i++)
		{
			File folder = folders[i];
			String setID = folder.getName();
			File files[] = folder.listFiles();
			
			for (int j = 0; j < files.length; j++)
			{
				String fileName = files[j].getName();
				String recordID = fileName.split(":")[2];
				
				if (newRecords == null)
				{
					// getRecordWGET(XMLdir, setID, recordID.replace(".xml", ""));
					getRecordWGET_Temporary(XMLdir, setID, recordID.replace(".xml", ""));
				}
				else
				{
					String newInfo = setID + File.separator + "oai:scielo:" + recordID;
					if (newRecords.contains(newInfo))
					{
						// getRecordWGET (XMLdir, setID, recordID.replace(".xml", ""));
						getRecordWGET_Temporary(XMLdir, setID, recordID.replace(".xml", ""));
					}
				}
			}		
		}
		
		System.out.println("Finished downloading all XMLs from new publications.");
	}
	
	public void getRecordWGET(String XMLdir, String setID, String recordID)
	{
		/*
		 * Currently there are some problems with the SciELO server and using wget in a normal way
		 * is impossible. We are using the method below to download the records in XML right,
		 * when we should have been using this one.
		 * Once the problems are fixed, we will return to this method.
		 */
 		String setDir = XMLdir + File.separator + setID;
		File theDir = new File(setDir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
			System.out.println("Getting XML records for set " + setID);
		}
		
		// example: wget http://scielo.isciii.es/scieloOrg/php/articleXML.php?pid=S1130-01082004001000010 -O /home/aintxaur/Scielo-Corpus/scielo-isciii/records/1130-0108/S1130-01082004001000010.xml
		String wget = "wget -U \"Opera 11.0\" " + xmlURL + recordID + " -O " + XMLdir + File.separator + setID + File.separator + recordID + ".xml";
		
		Process p;
		try
		{
			p = Runtime.getRuntime().exec(wget);
			p.waitFor();
			numRecords++;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void getRecordWGET_Temporary(String XMLdir, String setID, String recordID)
	{
		/*
		 * Currently there are some problems with the SciELO server and using wget in a normal way
		 * is impossible. We are using this method to download the records in XML right now.
		 * Once the problems are fixed, we will return to the method above.
		 */
 		String setDir = XMLdir + File.separator + setID;
		File theDir = new File(setDir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
			System.out.println("Getting XML records for set " + setID);
		}
		
		// example: wget http://scielo.isciii.es/scieloOrg/php/articleXML.php?pid=S1130-01082004001000010 -O /home/aintxaur/Scielo-Corpus/scielo-isciii/records/1130-0108/S1130-01082004001000010.xml
		String wget = "wget -U \"Opera 11.0\" " + xmlURL + recordID + " -O " + XMLdir + File.separator + setID + File.separator + recordID + ".xml";
		
		Process p;
		try
		{
			p = Runtime.getRuntime().exec(wget);
			p.waitFor();
			numRecords++;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int returnNumRecords()
	{
		return numRecords;
	}
}
