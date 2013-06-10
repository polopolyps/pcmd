package com.polopoly.ps.contentimporter.hotdeploy.xml.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.file.FileDeploymentFile;

public class ContentXmlParser
    implements DeploymentFileParser
{
	private static final Logger logger = Logger.getLogger(ContentXmlParser.class.getName());

	public ContentXmlParser()
	{
	}

	private void handleException(final DeploymentFile file,
	                             final Exception e)
	{
		logger.log(Level.WARNING, "While parsing " + file + ": " + e.getMessage(), e);
	}

	public void parse(final DeploymentFile file,
	                  final ParseCallback callback)
	{
		InputStream inputStream = null;

		try {
			inputStream = file.getInputStream();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			Element root = document.getDocumentElement();

			String rootName = root.getNodeName();

			if (rootName.equals("template-definition")) {
			    if (file instanceof FileDeploymentFile) {
			        ((FileDeploymentFile)file).setIsTemplateDefinitionFile(true);
			    }

				new TemplateDefinitionParser(file, root, callback);
			} else if (rootName.equals("batch")) {
				new XmlIoParser(file, root, callback);
			} else {
				logger.log(Level.WARNING, "File " + file + " was of unknown type.");
			}
		} catch (FileNotFoundException e) {
			handleException(file, e);
		} catch (ParserConfigurationException e) {
			handleException(file, e);
		} catch (SAXException e) {
			handleException(file, e);
		} catch (IOException e) {
			handleException(file, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
}
