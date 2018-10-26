package Crawler.File_Managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class TSVwriter {

	private String corpusDirectory;
	
	private int numRecords;
	private int failedRecords;
	
	public TSVwriter(String corpusDirectory)
	{
		this.corpusDirectory = corpusDirectory;
		
		numRecords = 0;
		failedRecords = 0;
	}
	
	public void start() throws ParserConfigurationException, SAXException, IOException
	{
		String DCdir = corpusDirectory + File.separator + "dublin_core_extended";
		String TSVdir = corpusDirectory + File.separator + "dublin_core_TSV";
		
		File theDir = new File(TSVdir);
		if (!theDir.exists()) 
		{
			theDir.mkdir();
		}
		
		List<Map<String, List<String>>> allDCinfo = new ArrayList<Map<String, List<String>>>();
		
		File dcfolders[] = new File(DCdir).listFiles();
		for (int i = 0; i < dcfolders.length; i++)
		{
			String folderName = dcfolders[i].getName();
			if (!folderName.endsWith(".txt"))
			{
				System.out.println("Creating TSV lines of Dublin Core files at set " + folderName);
				
				File files[] = new File(DCdir + File.separator + folderName).listFiles();
				for (int j = 0; j < files.length; j++)
				{
					String fileName = files[j].getName();
					
					Map<String, List<String>> dcInfo = readXML(DCdir + File.separator + folderName + File.separator + fileName);
					
					if (dcInfo == null)
					{
						failedRecords++;
					}
					else
					{
						allDCinfo.add(dcInfo);
						numRecords++;
					}												
				}
			}		
		}
		
		writeTSV(TSVdir, allDCinfo);
	}
	
	public void writeTSV(String TSVdir, List<Map<String, List<String>>> allDCinfo) throws IOException 
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(TSVdir + File.separator + "scielo.tsv"));	
		boolean header = true;		
		for (int i = 0; i < allDCinfo.size(); i++)
		{
			Map<String, List<String>> dcInfo = allDCinfo.get(i);
			
			if (header)
			{
				Iterator<String> iter = dcInfo.keySet().iterator();
				String line = new String();
				while (iter.hasNext())
				{
					String info = iter.next();
					if (info.equals("dc:title") || info.equals("dc:description"))
					{
						info = info + "_es" + "\t" + info + "_en";
					}
					line = line + "\t" + info;
				}
				line = line.substring(1);
				writer.write("#" + line + "\n");
				header = false;
			}			
			
			Iterator<String> iter = dcInfo.keySet().iterator();
			String line = new String();
			while (iter.hasNext())
			{
				String info = iter.next();
				List<String> value = dcInfo.get(info);
				String infoString = new String();
				if (value == null)
				{
					if (info.equals("dc:title") || info.equals("dc:description"))
					{
						infoString = "<null>\t<null>";
					}
					else
					{
						infoString = "<null>";
					}					
				}
				else
				{
					if (info.equals("dc:title") || info.equals("dc:description"))
					{
						if (value.size() == 2)
						{
							infoString = value.get(0) + "\t" + value.get(1);
						}
						else
						{
							infoString = value.get(0) + "\t" + "<null>";
						}
					}
					else
					{
						for (int j = 0; j < value.size(); j++)
						{
							infoString = infoString + "," + value.get(j);
						}
						infoString = infoString.substring(1);
					}					
				}				
				line = line + "\t" + infoString;
			}
			line = line.substring(1);
			writer.write(line + "\n");
		}
		writer.close();
	}

	public Map<String, List<String>> readXML(String DCfile) throws ParserConfigurationException, SAXException, IOException
	{
		XMLreader xmlreader = new XMLreader(DCfile);
		boolean loadSuccessful = xmlreader.loadXML();
		
		if (loadSuccessful)
		{
			List<String> identifiers = xmlreader.returnNodeDublinCore("identifier");
			List<String> datestamps = xmlreader.returnNodeDublinCore("datestamp");
			List<String> setSpecs = xmlreader.returnNodeDublinCore("setSpec");
			List<String> creators = xmlreader.returnNodeDublinCore("dc:creator");
			List<String> subjects = xmlreader.returnNodeDublinCore("dc:subject");
			List<String> publishers = xmlreader.returnNodeDublinCore("dc:publisher");
			List<String> sources = xmlreader.returnNodeDublinCore("dc:source");
			List<String> dates = xmlreader.returnNodeDublinCore("dc:date");
			List<String> types = xmlreader.returnNodeDublinCore("dc:type");
			List<String> formats = xmlreader.returnNodeDublinCore("dc:format");
			List<String> dcidentifiers = xmlreader.returnNodeDublinCore("dc:identifier");
			List<String> languages = xmlreader.returnNodeDublinCore("dc:language");
			List<String> titles = xmlreader.returnNodeDublinCore("dc:title");
			List<String> abstracts = xmlreader.returnNodeDublinCore("dc:description");
			List<String> rights = xmlreader.returnNodeDublinCore("dc:rights");
			
			Map<String, List<String>> dcInfo = new HashMap<String, List<String>>();
			dcInfo.put("identifier", identifiers);
			dcInfo.put("datestamp", datestamps);
			dcInfo.put("setSpec", setSpecs);
			dcInfo.put("dc:creator", creators);
			dcInfo.put("dc:subject", subjects);
			dcInfo.put("dc:publisher", publishers);
			dcInfo.put("dc:source", sources);
			dcInfo.put("dc:date", dates);
			dcInfo.put("dc:type", types);
			dcInfo.put("dc:format", formats);
			dcInfo.put("dc:identifier", dcidentifiers);
			dcInfo.put("dc:language", languages);
			dcInfo.put("dc:title", titles);
			dcInfo.put("dc:description", abstracts);
			dcInfo.put("dc:rights", rights);
			
			return dcInfo;
		}
		else
		{
			return null;
		}
	}
	
	public int returnNumRecords()
	{
		return numRecords;
	}
	
	public void printNumFailed()
	{
		System.err.println("Couldn't create TSV lines for " + failedRecords + " records.");
	}
}
