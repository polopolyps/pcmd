package com.polopoly.ps.pcmd.tool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.polopoly.cm.ContentFileInfo;
import com.polopoly.cm.client.CMException;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.policy.Util;

public class ExportFilesTool implements Tool<ContentIdListParameters> {

	@Override
	public void execute(PolopolyContext context, ContentIdListParameters parameters)
			throws FatalToolException {
		ContentIdToContentIterator it = new ContentIdToContentIterator(context, parameters.getContentIds(),
				parameters.isStopOnException());

		while (it.hasNext()) {
			ContentUtil content = Util.util(it.next(), context);

			try {
				ContentFileInfo[] files = content.listFiles("/", true);
				
				File target = new File(AbstractContentIdField.get(content.getContentId().unversioned(), context));
				
				target.mkdir();
				
				for (ContentFileInfo file : files) {
					String fullName = file.getDirectory() + "/" + file.getName();

					if (file.isDirectory()) {
						new File(target, fullName).mkdirs();
					}
					else {
						try {
							FileOutputStream os = new FileOutputStream(new File(target, fullName));
							
							content.exportFile(fullName, os);
							
							os.close();
						} catch (FileNotFoundException e) {
							System.err.println("While writing file " + file.getName() + ": " + e.getMessage());
							e.printStackTrace(System.err);
						} catch (CMException e) {
							System.err.println("While writing file " + file.getName() + ": " + e.getMessage());
							e.printStackTrace(System.err);
						} catch (IOException e) {
							System.err.println("While writing file " + file.getName() + ": " + e.getMessage());
							e.printStackTrace(System.err);
						}
					}
				}
			} catch (CMException e) {
				throw new FatalToolException("While listing files in " + content.getContentId().unversioned() + ": " + e.getMessage(), e);
			} catch (IOException e) {
				throw new FatalToolException("While listing files in " + content.getContentId().unversioned() + ": " + e.getMessage(), e);
			}
		}
	}

	@Override
	public ContentIdListParameters createParameters() {
		return new ContentIdListParameters();
	}

	@Override
	public String getHelp() {
		return "Writes all the files in the specified content objects to disk.";
	}
	
}
