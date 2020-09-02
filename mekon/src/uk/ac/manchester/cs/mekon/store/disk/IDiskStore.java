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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * @author Colin Puleston
 */
class IDiskStore implements IStore {

	private CModel model;

	private StoreSerialiser serialiser;
	private LogFile logFile;

	private List<IMatcher> matchers = new ArrayList<IMatcher>();
	private IDirectMatcher defaultMatcher = new IDirectMatcher();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IRegenType> types = new HashMap<CIdentity, IRegenType>();
	private InstanceIndexes indexes = new InstanceIndexes();

	private IStoreActiveRegenReport regenReport;
	private InstanceRefIntegrityManager refIntegrityManager;

	private boolean loaded = false;

	private class Initialiser {

		private List<IMatcher> reloadableMatchers = new ArrayList<IMatcher>();

		Initialiser() {

			initialiseMatchers();
			reloadInstances();
			processReloadedInstances();

			indexes.reinitialiseFreeIndexes();
		}

		private void initialiseMatchers() {

			initialiseMatcher(defaultMatcher);

			for (IMatcher matcher : matchers) {

				initialiseMatcher(matcher);

				if (matcher.rebuildOnStartup()) {

					reloadableMatchers.add(matcher);
				}
			}
		}

		private void initialiseMatcher(IMatcher matcher) {

			matcher.initialise(IDiskStore.this, indexes);
		}

		private void reloadInstances() {

			for (IInstanceProfile profile : serialiser.resolveStoredProfiles()) {

				reloadInstance(profile);
			}
		}

		private void reloadInstance(IInstanceProfile profile) {

			CIdentity identity = profile.getInstanceIdentity();
			IRegenType type = createRegenType(profile.getTypeIdentity());

			identities.add(identity);
			types.put(identity, type);
			indexes.assignIndex(identity, profile.getIndex());

			refIntegrityManager.onReloadedInstance(identity, profile);
		}

		private void processReloadedInstances() {

			for (CIdentity identity : getAllIdentities()) {

				IFrame instance = checkRegenInstance(identity);

				if (instance != null) {

					checkAddToMatcher(instance, identity);
				}
			}
		}

		private IFrame checkRegenInstance(CIdentity identity) {

			IRegenInstance regen = regen(identity, true);

			switch (regen.getStatus()) {

				case FULLY_INVALID:
					regenReport.addFullyInvalidRegenId(identity);
					return null;

				case PARTIALLY_VALID:
					regenReport.addPartiallyValidRegenId(identity);
					break;
			}

			return regen.getRootFrame();
		}

		private void checkAddToMatcher(IFrame instance, CIdentity identity) {

			IMatcher matcher = lookForMatcher(reloadableMatchers, instance.getType());

			if (matcher != null) {

				matcher.add(instance, identity);
			}
		}
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);
		types.put(identity, createRegenType(instance));

		serialiser.write(instance, identity, index);

		refIntegrityManager.onAddedInstance(instance, identity);
		addToMatcher(instance, identity);

		return previous;
	}

	public synchronized boolean remove(CIdentity identity) {

		if (checkRemove(identity) != null) {

			refIntegrityManager.onRemovedInstance(identity);

			return true;
		}

		return false;
	}

	public synchronized boolean clear() {

		if (identities.isEmpty()) {

			return false;
		}

		for (CIdentity identity : getAllIdentities()) {

			remove(identity);
		}

		return true;
	}

	public CModel getModel() {

		return model;
	}

	public IStoreRegenReport getRegenReport() {

		return regenReport;
	}

	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	public synchronized IRegenType getType(CIdentity identity) {

		return types.get(identity);
	}

	public synchronized IRegenInstance get(CIdentity identity) {

		return indexes.hasIndex(identity) ? regen(identity, false) : null;
	}

	public synchronized List<CIdentity> getAllIdentities() {

		return new ArrayList<CIdentity>(identities);
	}

	public synchronized IMatches match(IFrame query) {

		query = createFreeCopy(query);

		IMatches matches = getMatcher(query).match(query);

		indexes.ensureOriginalLabelsInMatches(matches);

		return matches;
	}

	public synchronized boolean matches(IFrame query, IFrame instance) {

		query = createFreeCopy(query);
		instance = createFreeCopy(instance);

		IMatcher matcher = getMatcher(query);

		if (matcher != getMatcher(instance)) {

			return false;
		}

		return matcher.matches(query, instance);
	}

	IDiskStore(CModel model) {

		this(model, new StoreStructureBuilder().build(model));
	}

	IDiskStore(CModel model, StoreStructure structure) {

		this.model = model;

		serialiser = new StoreSerialiser(model, structure);
		logFile = new LogFile(structure.getMainDirectory());
		regenReport = new IStoreActiveRegenReport(logFile.getFile());
		refIntegrityManager = new InstanceRefIntegrityManager(this);
	}

	void addMatchers(Collection<IMatcher> matchers) {

		this.matchers.addAll(matchers);
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void initialisePostRegistration() {

		new Initialiser();

		loaded = true;
	}

	void stop() {

		for (IMatcher matcher : matchers) {

			matcher.stop();
		}

		matchers.clear();
	}

	void update(IFrame instance, CIdentity identity) {

		int index = indexes.getIndex(identity);

		removeFromMatcher(instance.getType(), identity);

		serialiser.remove(index);
		serialiser.write(instance, identity, index);

		addToMatcher(instance, identity);
	}

	IFrame regenOrNull(CIdentity identity, boolean freeInstance) {

		return regenOrNull(identity, indexes.getIndex(identity), freeInstance);
	}

	private IFrame checkRemove(CIdentity identity) {

		if (!indexes.hasIndex(identity)) {

			return null;
		}

		identities.remove(identity);
		types.remove(identity);

		int index = indexes.getIndex(identity);
		IFrame removed = regenOrNull(identity, index, false);

		if (removed != null) {

			removeFromMatcher(removed.getType(), identity);
		}

		serialiser.remove(index);
		indexes.freeIndex(identity);

		return removed;
	}

	private IFrame regenOrNull(CIdentity identity, int index, boolean freeInstance) {

		IRegenInstance regen = regen(identity, index, freeInstance);

		if (regen.getStatus() == IRegenStatus.FULLY_INVALID) {

			return null;
		}

		return regen.getRootFrame();
	}

	private IRegenInstance regen(CIdentity identity, boolean freeInstance) {

		return regen(identity, indexes.getIndex(identity), freeInstance);
	}

	private IRegenInstance regen(CIdentity identity, int index, boolean freeInstance) {

		IRegenInstance regen = serialiser.read(identity, index, freeInstance);

		if (!loaded) {

			logFile.logParsedInstance(identity, regen);
		}

		return regen;
	}

	private void addToMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).add(createFreeCopy(instance), identity);
	}

	private void removeFromMatcher(CFrame type, CIdentity identity) {

		getMatcher(type).remove(identity);
	}

	private IMatcher getMatcher(IFrame frame) {

		return getMatcher(frame.getType());
	}

	private IMatcher getMatcher(CFrame frameType) {

		IMatcher matcher = lookForMatcher(matchers, frameType);

		return matcher != null ? matcher : defaultMatcher;
	}

	private IMatcher lookForMatcher(List<IMatcher> candidates, CFrame frameType) {

		for (IMatcher candidate : candidates) {

			if (candidate.handlesType(frameType)) {

				return candidate;
			}
		}

		return null;
	}

	private IRegenType createRegenType(IFrame instance) {

		return new IRegenValidType(instance.getType());
	}

	private IRegenType createRegenType(CIdentity typeId) {

		CFrame type = model.getFrames().getOrNull(typeId);

		return type != null ? new IRegenValidType(type) : new IRegenInvalidType(typeId);
	}

	private IFrame createFreeCopy(IFrame instance) {

		return IFreeCopier.get().createFreeCopy(instance);
	}

	private CFrame getTypeOrNull(int index) {

		return model.getFrames().getOrNull(serialiser.readTypeId(index));
	}
}
