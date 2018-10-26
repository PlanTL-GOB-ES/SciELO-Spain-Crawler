package Crawler.File_Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XMLfixer {

	private String corpusDirectory;
	
	public XMLfixer(String corpusDirectory) 
	{
		this.corpusDirectory = corpusDirectory;
	}

	public void fixXMLdocuments() throws IOException
	{
		String xmlDir = corpusDirectory + File.separator + "records";
		File dir = new File(xmlDir);
		File folders[] = dir.listFiles();
		for (int i = 0; i < folders.length; i++)
		{
			File folder = folders[i];
			String name = folder.getName();
			File files[] = folder.listFiles();
			for (int j = 0; j < files.length; j++)
			{
				String file = files[j].getName();
				List<String> lines = new ArrayList<String>();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlDir + File.separator + name + File.separator + file), 
						StandardCharsets.ISO_8859_1));
				String line = "";
				while ((line = reader.readLine()) != null)
				{
					if (!line.contains("CDATA") && line.contains(" & "))
					{
						line = line.replaceAll(" & ", " &amp; ");
					}
					if (!line.contains("CDATA") && line.contains("&acute;"))
					{
						line = line.replaceAll("&acute;", "'");
					}					
					if (!line.contains("CDATA") && line.contains(" &ap"))
					{
						line = line.replaceAll("&ap", "ap");
					}
					if (!line.contains("CDATA") && line.contains("&shy;"))
					{
						line = line.replaceAll("&shy;", "-");
					}
					lines.add(line);
				}
				reader.close();

				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(xmlDir + File.separator + name + File.separator + file), 
						StandardCharsets.ISO_8859_1);
				for (int k = 0; k < lines.size(); k++)
				{
					String newLine = lines.get(k);
					writer.write(newLine + "\n"); 
				}
				writer.close();
			}
		}
	}
	
}
