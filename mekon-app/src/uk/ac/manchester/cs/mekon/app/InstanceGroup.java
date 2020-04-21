/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class InstanceGroup {

	private Controller controller;
	private Store store;

	private CFrame rootType;
	private CFrame simpleQueriesRootType;

	private Map<CIdentity, CFrame> instancesToNonRootTypes = new HashMap<CIdentity, CFrame>();

	private InstanceIdsList rootAssertionIds;
	private InstanceIdsList rootQueryIds;

	private QueryExecutions queryExecutions;

	InstanceGroup(Controller controller, CFrame rootType) {

		this.controller = controller;
		this.rootType = rootType;

		store = controller.getStore();
		simpleQueriesRootType = getSimpleQueriesRootTypeOrNull();

		rootAssertionIds = new InstanceIdsList(this, false);
		rootQueryIds = new InstanceIdsList(this, true);

		queryExecutions = new QueryExecutions(controller, this);

		loadInstanceIds(rootType);

		if (simpleQueriesRootType != null) {

			loadInstanceIds(simpleQueriesRootType);
		}
	}

	InstanceIdsList createAssertionIdsList(CFrame type) {

		InstanceIdsList typeAssertIds = new InstanceIdsList(this, false);

		if (type.equals(rootType)) {

			typeAssertIds.addIds(rootAssertionIds.getEntityList());
		}
		else {

			for (CIdentity storeId : store.getInstanceIds(type)) {

				if (assertionId(storeId)) {

					typeAssertIds.addId(storeId);
				}
			}
		}

		return typeAssertIds;
	}

	Controller getController() {

		return controller;
	}

	CFrame getRootType() {

		return rootType;
	}

	boolean simpleQueriesEnabled() {

		return simpleQueriesRootType != null;
	}

	CFrame getSimpleQueriesRootType() {

		if (simpleQueriesRootType == null) {

			throw new Error("Should never happen!");
		}

		return simpleQueriesRootType;
	}

	boolean includesInstancesOfType(CFrame type) {

		return rootType.subsumes(type) || includesSimpleQueriesOfType(type);
	}

	boolean includesSimpleQueriesOfType(CFrame type) {

		return simpleQueriesEnabled() && simpleQueriesRootType.subsumes(type);
	}

	CFrame getInstanceType(CIdentity storeId) {

		CFrame type = instancesToNonRootTypes.get(storeId);

		return type != null ? type : rootType;
	}

	InstanceIdsList getRootAssertionIdsList() {

		return rootAssertionIds;
	}

	InstanceIdsList getRootQueryIdsList() {

		return rootQueryIds;
	}

	QueryExecutions getQueryExecutions() {

		return queryExecutions;
	}

	boolean checkAddInstance(IFrame instance, CIdentity storeId, boolean asNewId) {

		if (store.checkAdd(instance, storeId, asNewId)) {

			updateTypesForAddition(storeId);
			getInstanceIdsList(storeId).checkAddId(storeId);

			return true;
		}

		return false;
	}

	void checkRemoveInstance(CIdentity storeId) {

		if (store.checkRemove(storeId)) {

			updateTypesForRemoval(storeId);
			getInstanceIdsList(storeId).removeEntity(storeId);
		}
	}

	void checkRenameInstance(CIdentity storeId, CIdentity newStoreId) {

		if (store.checkRename(storeId, newStoreId)) {

			updateTypesForReplacement(storeId, newStoreId);
			getInstanceIdsList(storeId).replaceId(storeId, newStoreId);
		}
	}

	private void loadInstanceIds(CFrame loadRootType) {

		for (CIdentity storeId : store.getInstanceIds(loadRootType)) {

			updateTypesForAddition(storeId);
			getInstanceIdsList(storeId).addId(storeId);
		}
	}

	private void updateTypesForAddition(CIdentity storeId) {

		CFrame type = store.getType(storeId);

		if (!type.equals(rootType)) {

			instancesToNonRootTypes.put(storeId, type);
		}
	}

	private void updateTypesForRemoval(CIdentity storeId) {

		instancesToNonRootTypes.remove(storeId);
	}

	private void updateTypesForReplacement(CIdentity storeId, CIdentity newStoreId) {

		CFrame type = instancesToNonRootTypes.remove(storeId);

		if (type != null) {

			instancesToNonRootTypes.put(newStoreId, type);
		}
	}

	private CFrame getSimpleQueriesRootTypeOrNull() {

		SimpleQueriesConfig cfg = getSimpleQueriesConfig();

		return cfg.simpleQueriesFor(rootType) ? cfg.toSimpleQueryType(rootType) : null;
	}

	private SimpleQueriesConfig getSimpleQueriesConfig() {

		return controller.getCustomiser().getSimpleQueriesConfig();
	}

	private InstanceIdsList getInstanceIdsList(CIdentity storeId) {

		return assertionId(storeId) ? rootAssertionIds : rootQueryIds;
	}

	private boolean assertionId(CIdentity id) {

		return MekonAppStoreId.assertionId(id);
	}
}
