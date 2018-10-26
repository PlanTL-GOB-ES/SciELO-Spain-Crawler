package Crawler.File_Managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LicenseReader {
	
	private String corpusDirectory;
	private Map<String, String> licensesFull;
	
	private List<String> licensesDirs;
	
	public LicenseReader(String corpusDirectory)
	{
		this.corpusDirectory = corpusDirectory;
		licensesFull = new HashMap<String, String>();
		licensesDirs = new ArrayList<String>();
	}
	
	public Map<String, String> getLicences() throws IOException
	{		
		BufferedReader reader = new BufferedReader(new FileReader(corpusDirectory + File.separator + "CC-licenses.txt"));
		String line = "";
		while ((line = reader.readLine()) != null)
		{
			licensesFull.put(line.split("\t")[0], line.split("\t")[1] + "\t" + line.split("\t")[2]);
			licensesDirs.add(line.split("\t")[0]);
		}
		reader.close();
		
		return licensesFull;
	}
	
	public void printSetsWithoutLicense()
	{
		File dir = new File(corpusDirectory + File.separator + "dublin_core_records");
		File folders[] = dir.listFiles();
		for (int i = 0; i < folders.length; i++)
		{
			String folder = folders[i].getName();
			if (!licensesDirs.contains(folder))
			{
				System.out.println("WARNING!! No license for set " + folder);
			}
		}
	}
}
