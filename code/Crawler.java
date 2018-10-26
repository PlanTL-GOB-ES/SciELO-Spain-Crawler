package Crawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import Crawler.File_Managers.LicenseReader;
import Crawler.File_Managers.TSVwriter;
import Crawler.File_Managers.XMLfixer;
import Crawler.Full_Text_Extractor.ExtractTextsFromBody;

public class Crawler {
	
	private String corpusDirectory;
	
	private Map<String, String> licenses;
	
	private String set = null;
	
	private String lastDate;
	
	public Crawler(String corpusDirectory)
	{
		this.corpusDirectory = corpusDirectory;
		
		lastDate = new String();
		licenses = new HashMap<String, String>();
	}
	
	public Crawler(String corpusDirectory, String set)
	{
		this.corpusDirectory = corpusDirectory;
		this.set = set;
		
		lastDate = new String();
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
		// TODO Auto-generated method stub
		String corpusDirectory = args[0];
		
		if (args.length == 1)
		{
			Crawler crawler = new Crawler(corpusDirectory);
			crawler.start();
		}
		else
		{
			String set = args[1];
			
			Crawler crawler = new Crawler(corpusDirectory, set);
			crawler.start();
		}		
	}

	public void start() throws IOException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException
	{
		getLicenses();
		getPublicationsDCs();
		downloadPublicationsXMLs();
		extendDublinCores();
		extractFullTextFromBody();
		createTSVfile();
	}
	
	public void getLicenses() throws IOException
	{
		LicenseReader licenseReader = new LicenseReader(corpusDirectory);
		licenses = licenseReader.getLicences();
	//	licenseReader.printSetsWithoutLicense();
	}
	
	public void getPublicationsDCs() throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		GetAllSets sets = new GetAllSets(licenses);
		sets.getURL();
		sets.getSets();
		sets.printSets();
		
		if (set == null)
		{
			GetRecordsFromSets records = new GetRecordsFromSets(corpusDirectory);
			lastDate = records.getLastDate();
			records.readSetsFile();
			records.analyzeSets(lastDate);
			int docCounter = records.returnDocCounter();
			System.out.println("Number of Scielo documents downloaded: " + docCounter);
			records.saveDate();
			records.saveNewRecords();
		}
		else
		{
			GetRecordsFromSets records = new GetRecordsFromSets(corpusDirectory, set);
			lastDate = records.getLastDate();
			records.analyzeSet(lastDate);
			int docCounter = records.returnDocCounter();
			System.out.println("Number of Scielo documents in Dublin Core format downloaded: " + docCounter);
			records.saveDate();
			records.saveNewRecords();
		}
	}
	
	public void downloadPublicationsXMLs() throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		GetPublicationsXMLs getNew; 
		if (lastDate == null)
		{
			getNew = new GetPublicationsXMLs(corpusDirectory);
		}
		else
		{
			getNew = new GetPublicationsXMLs(corpusDirectory, true);
		}
		getNew.getRecords();
		int numRecords = getNew.returnNumRecords();
		System.out.println("Number of Scielo full text documents downloaded: " + numRecords);
		
		XMLfixer xml = new XMLfixer(corpusDirectory);
		xml.fixXMLdocuments();
	}
	
	public void extendDublinCores() throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException
	{		
		ExtractTitlesAbstractXML extract;
		if (lastDate == null)
		{
			extract = new ExtractTitlesAbstractXML(corpusDirectory, licenses);
		}
		else
		{
			extract = new ExtractTitlesAbstractXML(corpusDirectory, licenses, true);
		}
		extract.start();
		int numRecords = extract.returnNumRecords();
		System.out.println("Number of Scielo Dublin Cores extended: " + numRecords);
		extract.printNumFailed();
	}
	
	public void extractFullTextFromBody() throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		ExtractTextsFromBody extract;
		if (lastDate == null)
		{
			extract = new ExtractTextsFromBody(corpusDirectory);
		}
		else
		{
			extract = new ExtractTextsFromBody(corpusDirectory, true);
		}
		extract.start();
		int numRecords = extract.returnNumRecords();
		System.out.println("Number of raw text and XMLs extracted from publications: " + numRecords);
		extract.printNumFailed();
	}
	
	public void createTSVfile() throws ParserConfigurationException, SAXException, IOException
	{
		TSVwriter tsv = new TSVwriter(corpusDirectory);
		tsv.start();
		System.out.println("Finished creating TSV file from the complete Scielo corpus.");
		tsv.printNumFailed();
	}
}
