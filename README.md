# SciELO Spain Crawler

## Digital Object Identifier (DOI)

https://doi.org/10.5281/zenodo.1493411


## Introduction

The Scientific Electronic Library Online – SciELO Spain (http://scielo.isciii.es) is an electronic virtual library 
covering a collection of Spanish health scientific journals that are selected following preestablished quality criteria.
The library is an integral part of a project being developed by BIREME (the Latin American and Caribbean Center on 
Health Sciences Information) in partnership with FAPESP (Fundação de Amparo à Pesquisa do Estado de São Paulo). 
In Spain, the SciELO project is the result of the collaboration between the Organización Panamericana de Salud/Organización 
Mundial de la Salud (Pan American Health Organization/World Health Organization, OPS/OMS) and the Instituto de Salud 
Carlos III (Carlos III Health Institute, ISCIII) (http://www.isciii.es/), and it is been developed by the Biblioteca 
Nacional de Ciencias de la Salud (National Library of Health Sciences, BNCS) (www.isciii.es/bncs).
The main objective of the SciELO project is to develop a common methodology for the preparation, storage, dissemination and 
evaluation of scientific literature in electronic format. 

The SciELO crawler downloads all the publications written in Spanish from the Spanish SciELO server. This server is organized 
using the OAI-PMH services maintained by ISCIII (http://scielo.isciii.es/oai/), and it provides access to all the 
metadata of each publication, which includes a link to the full text in XML. The crawler also fixes the encoding and 
creates a new XML file with all the sentences being splitted.

Example: 

Publication with ID 'S0213-12852003000100001':
- Publication website: http://scielo.isciii.es/scielo.php?script=sci_arttext&pid=S0213-12852003000100001
- Dublin Core metadata: http://scielo.isciii.es/oai/scielo-oai.php?verb=GetRecord&metadataPrefix=oai_dc&identifier=oai:scielo:S0213-12852003000100001
- Publication in XML format including full text: http://scielo.isciii.es/scieloOrg/php/articleXML.php?pid=S0213-12852003000100001&lang=es 

This crawler only downloads those journals with explicit Creative Commons licenses.


## Prerequisites

This software has been compiled with Java SE 1.8 and it should work with recent versions. You can download Java from the following website: https://www.java.com/en/download

The IXA Pipes tokenization module is needed as well. We used version 1.1.1 for this work and latest versions should work as well. 
IXA Pipes is licensed under the Apache License 2.0. You can download it from the following website: 
http://ixa2.si.ehu.es/ixa-pipes/

Apache Commons IO is also necessary to execute the crawler. We used version 2.6 for this work. Apache Commons IO is licensed under the Apache License 2.0. You can download it from the following website: http://commons.apache.org/proper/commons-io/

Finally, the Jsoup library is also needed. Version 1.10.3 has been used here. Jsoup is distributed under the MIT license. The library is available in this website: https://jsoup.org/


## Directory structure

<pre>
code/
The source code of the crawler. The main class is called <i>Crawler.java</i>

exec/
The executable to generate the database.

exec/Scielo-Spain-Crawler_lib/
The modules needed to execute the crawler. Without this directory, the crawler will crash. It includes IXA 
Pipes (the tokenization module only), Apache Commons IO and Jsoup.
</pre>


## Usage

To execute the crawler from the `exec` directory, use the following command:

<pre>java -jar Scielo-Spain-Crawler.jar OUTPUT_DIRECTORY [JOURNAL_ISSN]</pre>

where, OUTPUT_DIRECTORY is the directory to place the downloaded corpus (you should specify the absolute path where 
it is placed), and JOURNAL_ISSN is the journal's offical ISSN serial number. This parameter is optional and it 
should be used to download only an specific journal.

Note that the file CC-licenses.txt must be placed in the OUTPUT_DIRECTORY, otherwise the crawler will crash.

If you execute the crawler in a previously created directory, the crawler will only download the added documents, 
instead of the full collection again.


## Examples

Let's assume an OUTPUT_DIRECTORY `SciELO-Spanish-Articles` in the `exec` directory. 

<pre>
java -jar Scielo-Spain-Crawler.jar user/path_to_Crawler/SciELO-Spain-Crawler/exec/SciELO-Spanish-Articles
java -jar Scielo-Spain-Crawler.jar user/path_to_Crawler/SciELO-Spain-Crawler/exec/SciELO-Spanish-Articles 1578-2549
</pre>


## Output

The crawler creates a text file and five different directories in the output directory:
<pre>
last_date.txt
Stores the date of the last time the crawler was executed. This file is used to control the download of new 
documents every time the crawler is executed.

dublin_core_records/
The first folder created by the crawler. It contains all publications' metadata in Dublin Core format 
(publication's title, abstract, authors...). Each subfolder is named after a journal's ID and it includes 
all its publications. This folder contains the titles and abstracts in Spanish only.

records/
The second folder created by the crawler. It contains the publications' information in XML format, very 
similar to the metadata. This file includes the full text in HTML format, its title and its abstract in 
Spanish and English (if available).

dublin_core_extended/
The third folder created by the crawler. It contains the same files of the "dublin_core_records" folder, 
but the titles and abstracts are in both Spanish and English.

full_texts/clean_raw_text/
The fourth folder created by the crawler. It contains the full text of the article extracted from the XML 
file, with the encoding fixed, in complete raw text. Each line is a paragraph.

full_texts/clean_xml_text/
The fifth folder created by the crawler. Here the full text is organized in an XML file. Each XML contains 
the journal's ID, the article's ID and the full text splitted in paragraphs and sentences. We used IXA Pipes 
to split the sentences.

dublin_core_tsv/
The last folder created by the crawler. It includes all metadata of the "dublin_core_extended" folder in 
one single file in tabular format (TSV).
</pre>


## Contact

Ander Intxaurrondo (ander.intxaurrondo@bsc.es)


## License

(This is so-called MIT/X License)

Copyright (c) 2017-2018 Secretaría de Estado para el Avance Digital (SEAD)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
