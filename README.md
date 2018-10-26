# SciELO Spain Crawler

## Introduction

The Scientific Electronic Library Online – SciELO Spain is an electronic virtual library covering a collection of Spanish health scientific journals selected following preestablished quality criteria.

The library is an integral part of a project being developed by BIREME (the Latin American and Caribbean Center on Health Sciences Information), in partnership with FAPESP (Fundação de Amparo à Pesquisa do Estado de São Paulo). In Spain, the SciELO project is result of the collaboration between OPS/OMS and Carlos III Health Institute ISCIII and it is been developed by the National Library of Health Sciences.

The main aim of the SciELO project is to develop a common methodology for the preparation, storage, dissemination and evaluation of Spanish scientific literature in electronic format. 

- Official SciELO Spain website: http://scielo.isciii.es
- Carlos III Health Institute website: http://www.isciii.es/
- Health Sciences National Library: www.isciii.es/bncs
- Contact: scielo@isciii.es

### SciELO crawler

This is a crawler which downloads all publications written in Spanish from the Spanish SciELO server. This server is organized using OAI-PMH services, providing access to all metadata of each publication. This protocol gives access to the full text of each publication in XML. The crawler also extract the complete text, fixes the encoding, and creates a new XML file with all sentences splitted.

The OAI-PMH server is maintained by the Carlos III Health Institute (Instituto de Salud Carlos III) in Madrid, Spain. A number of new publications are added every week.

This crawler only downloads those journals with explicit Creative Commons licences.

### SciELO Spain OAI-PMH server

Server URL: http://scielo.isciii.es/oai/

Example for publication with ID 'S0213-12852003000100001':
- Dublin Core metadata: http://scielo.isciii.es/oai/scielo-oai.php?verb=GetRecord&metadataPrefix=oai_dc&identifier=oai:scielo:S0213-12852003000100001
- Publication website: http://scielo.isciii.es/scielo.php?script=sci_arttext&pid=S0213-12852003000100001
- Publication in XML format including full text: http://scielo.isciii.es/scieloOrg/php/articleXML.php?pid=S0213-12852003000100001&lang=es 

## Prerequisites

This software has been compiled with Java SE 1.8 and it should work with recent versions. You can download Java from the following website: https://www.java.com/en/download

IXA Pipes is needed as well. We used version 1.1.1 for this work, and latest versions should work as well. IXA pipes is licensed under the Apache License 2.0. You can download it from the following website: http://ixa2.si.ehu.es/ixa-pipes/

Apache Commons IO is also necessary to execute the crawler. We used version 2.6 for this work. Apache Commons IO is licensed under the Apache License 2.0. You can download it from the following website: http://commons.apache.org/proper/commons-io/

The Jsoup library is the last library required for the software. Version 1.10.3 has been used here. Jsoup is distributed under the MIT license. The library is available in this website: https://jsoup.org/

## Directory structure

<pre>
code/
This folder contains the source code of the crawler. The main class is called <i>Crawler.java</i>

exec/
The executable to generate the database.

exec/Scielo-Spain-Crawler_lib/
The modules needed to execute the crawler. Without this directory, the crawler will crash.
Includes IXA Pipes, Apache Commons IO and Jsoup.
</pre>

## Usage

To execute the crawler, you just need the "Scielo-Spain-Crawler.jar" file. Simply type the following command:

<pre>java -jar Scielo-Spain-Crawler.jar OUTPUT_DIRECTORY [JOURNAL_ID]</pre>

OUTPUT_DIRECTORY is the directory where the user wants the corpus to be downloaded. JOURNAL_ID is the optional parameter where the user specified a certain journal's ID to download this journal's publication's only.

The file CC-licenses.txt must be in the OUTPUT_DIRECTORY. Otherwise the crawler will crash.

If you execute the crawler in a directory previously created, the crawler will only download the documents added, instead of downloading the full collection again.

### Crawler output guide
The crawler creates 4 different directories in the output directory, and a text file:
<pre>
last_date.txt
stores the date of the last time the crawled was executed, this file is used to control the download of new documents every time the crawler is executed.

dublin_core_records/
This is the first folder created by the crawler. Contains all publications' metadata in Dublin Core (DC) format. You can find information about the publication's title, abstract, authors,... Each subfolder is a journal's ID, you can find the publications of the journal inside the folder. This folder contains the titles and abstracts in Spanish only

records/
this is the second folder created. Contains the publication's info in XML format, very similar to the metadata. This file includes the full text in HTML format, titles and abstract in Spanish and English (if available),...

dublin_core_extended/
this is the third folder created by the crawler. You can find the same files of the "dublin_core_records" folder, but this time titles and abstracts can be found in both Spanish and English.

full_texts/clean_raw_text/
this is the fourth folder created by the crawler. In this folder we can find the full text of the article extracted from the XML file, with the encoding fixed, in complete raw text. Each line is a paragraph.

full_texts/clean_xml_text/
this is the fifth folder created byt the crawler. This time, the full text is organized in an XML file. Each XML contains the journal's ID, the article's ID, and the full text splitted in paragraphs and sentences. We used IXA pipes [1] to split the sentences.

dublin_core_tsv/
this is the last folder created by the crawler. Here we can find all metadata of the "dublin_core_extended" folder in one single file in tabular format (TSV).
</pre>

## Example

<pre>
java -jar Scielo-Spain-Crawler.jar ./SciELO-Spanish-Articles
java -jar Scielo-Spain-Crawler.jar ./SciELO-Spanish-Articles 1578-2549
</pre>

## Contact

Ander Intxaurrondo (ander.intxaurrondo@bsc.es)

## License

(This is so-called MIT/X License)

Copyright (c) 2017-2018 Secretaría de Estado para el Avance Digital (SEAD)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

# References

[1] Rodrigo Agerri, Josu Bermudez and German Rigau (2014): "IXA pipeline: Efficient and Ready to Use Multilingual NLP tools", in: Proceedings of the 9th Language Resources and Evaluation Conference (LREC2014), 26-31 May, 2014, Reykjavik, Iceland.
