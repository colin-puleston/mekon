/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class FileStore {

	static private final String DEFAULT_STORE_DIR_NAME = "mekon-store";
	static private final String PROFILE_FILE_PREFIX = "PROFILE-";
	static private final String INSTANCE_FILE_PREFIX = "INSTANCE-";
	static private final String STORE_FILE_SUFFIX = ".xml";

	static File getDefaultNamedDirectory(File parentDir) {

		return new File(parentDir, DEFAULT_STORE_DIR_NAME);
	}

	static private KFileStore createFileStore(String filePrefix) {

		return new KFileStore(filePrefix, STORE_FILE_SUFFIX);
	}

	private KFileStore profiles = createFileStore(PROFILE_FILE_PREFIX);
	private KFileStore instances = createFileStore(INSTANCE_FILE_PREFIX);

	private LogFile log;
	private InstanceSerialiser instanceSerialiser;

	FileStore(CModel model, File directory) {

		if (directory == null) {

			directory = new File(DEFAULT_STORE_DIR_NAME);
		}

		log = new LogFile(directory);
		instanceSerialiser = new InstanceSerialiser(model, log);

		profiles.setDirectory(directory);
		instances.setDirectory(directory);
	}

	void write(IFrame instance, CIdentity identity, int index) {

		CIdentity typeId = instance.getType().getIdentity();
		List<CIdentity> refedIds = instance.getAllReferenceIds();
		InstanceProfile profile = new InstanceProfile(identity, typeId, refedIds);

		File pFile = profiles.getFile(index);
		File iFile = instances.getFile(index);

		ProfileSerialiser.render(profile, pFile);
		instanceSerialiser.render(instance, iFile);
	}

	IRegenInstance read(
					CIdentity identity,
					IFrameFunction function,
					int index,
					boolean freeInstance) {

		File iFile = instances.getFile(index);

		return instanceSerialiser.parse(identity, function, iFile, freeInstance);
	}

	CIdentity readTypeId(int index) {

		File pFile = profiles.getFile(index);

		return ProfileSerialiser.parse(pFile).getTypeId();
	}

	void remove(int index) {

		profiles.removeFile(index);
		instances.removeFile(index);
	}

	void clear() {

		profiles.clear();
		instances.clear();
	}

	List<StoredProfile> getStoredProfiles() {

		List<StoredProfile> storedProfiles = new ArrayList<StoredProfile>();

		for (File pFile : profiles.getAllFiles()) {

			InstanceProfile profile = ProfileSerialiser.parse(pFile);
			int index = profiles.getIndex(pFile);

			storedProfiles.add(new StoredProfile(profile, index));
		}

		return storedProfiles;
	}
}
