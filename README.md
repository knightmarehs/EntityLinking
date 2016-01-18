# EntityLinking
A part from ZhiCy, using DBpedia and Lucene.

To run this project correctly, you should meet the following requirments:

1. Add necessary JARs, you can find the list in the end of this file.

2. You should download the dataset of DBpedia, and then build lucene index using the codes in package "fgmt" and package "lcn".

3. After all, you can run "EntityRecognition.java" in package "process" to start this project.

# Notice
In fact, you can use another way to replace lucene index. For example, you can use "DBpedia lookup" to find the associated entities online, then you need not to keep the index even the dataset. However, "DBpedia lookup" will bring a lot of noises, so we use lucene index in practice.

# JARs list
lucene-core-2.0.0.jar;
lucene-demos-2.0.0.jar;
stanford-corenlp-1.3.4.jar;
stanford-corenlp-1.3.4-javadoc.jar;
stanford-corenlp-1.3.4-models.jar;
stanford-corenlp-1.3.4-sources.jar;
xom.jar
