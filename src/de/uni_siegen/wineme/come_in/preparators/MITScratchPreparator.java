/*
 * regain - A file search engine providing plenty of formats (Plugin)
 * Copyright (C) 2012  Come_IN Computerclubs (University of Siegen)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Come_IN-Team <come_in-team@listserv.uni-siegen.de>
 */
package de.uni_siegen.wineme.come_in.preparators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.mit.scratch.ObjReader;
import net.sf.regain.RegainException;
import net.sf.regain.crawler.document.AbstractPreparator;
import net.sf.regain.crawler.document.RawDocument;

public class MITScratchPreparator extends AbstractPreparator {
	public static final String SCRATCH_MIME_TYPE = "application/x-mit-scratch";
	
    private static final String MIME_TYPE_UNKNOWN = "application/x-unknown-mime-type";
	public static final String SCRATCH_EXTENSION = "sb";

	public MITScratchPreparator() throws RegainException
	{
		super( new String[]{ 
			SCRATCH_MIME_TYPE,
			MIME_TYPE_UNKNOWN /* When in doubt, just try */
		});
	}
	
	@Override
	public void prepare(RawDocument rawDocument) throws RegainException {
		Hashtable<?, ?> parsedScratchFile = loadFile(rawDocument.getContentAsFile());
		
		ArrayList<String> info = new ArrayList<String>();

		info.add((String) parsedScratchFile.get("author"));
		info.add((String) parsedScratchFile.get("comment"));
		info.add("Language:" + parsedScratchFile.get("language"));
		
		setCleanedContent(concatenateStringParts(info, Integer.MAX_VALUE));
        setTitle(concatenateStringParts(info, 2));

		rawDocument.setMimeType(SCRATCH_MIME_TYPE);
	}

	protected Hashtable<?, ?> loadFile(File file) throws RegainException {
		Hashtable<?,?> parsedScratchFile = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			ObjReader reader = new ObjReader(in);
	
			try {
				 parsedScratchFile = reader.readInfo();
			} catch (IOException e) {
				throw new RegainException("Error - Is this really a scratch project file?");
			}
			
		} catch (FileNotFoundException e) {
			throw new RegainException("Could not read file " + file.getAbsolutePath());
		} finally {
			if (in != null)
			{
				try {
					in.close();
				} catch (IOException e) { }
			}
		}
		return parsedScratchFile;
	}

}
