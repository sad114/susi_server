/**
 *  Accounting
 *  Copyright 16.06.2017 by Michael Peter Christen, @0rb1t3r
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program in the file lgpl21.txt
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package ai.susi.server;

import org.json.JSONObject;

import ai.susi.json.JsonTray;

import javax.annotation.Nonnull;

public class Accounting {

    private JsonTray parent;
    private JSONObject json;
    private UserRequests requests;
    private ClientIdentity identity;
    
    /**
     * create a new authorization object. The given json object must be taken
     * as value from a parent json. If the parent json is a JsonFile, then that
     * file can be handed over as well to enable persistency.
     * @param identity
     * @param parent the parent file or null if there is no parent file (no persistency)
     */
    public Accounting(@Nonnull ClientIdentity identity, JsonTray parent) {

        this.parent = parent;
        this.requests = new UserRequests(); // temporary user request space
        this.identity = identity;

        if (parent != null) {
        	String[] lookupKeys = identity.getLookupKeys();
        	this.json = null;
        	for (String key: lookupKeys) {
        		if (parent.has(key)) {
        			this.json = parent.getJSONObject(key);
    	    		break;
        		}
        	}
        	// in case that the identity has the uuid inside, we must loop here over all objects
        	if (this.json == null && identity.isUuid()) for (String key: this.parent.keys()) {
        		if (new ClientIdentity(key).getUuid().equals(identity.getName())) {
        			this.json = parent.getJSONObject(key);
    	    		break;
        		}
        	}
	    	if (this.json == null) {
	    		this.json = new JSONObject();
	        	parent.put(identity.toString(), json, identity.isPersistent());
	        }
    	} else {
    		this.json = new JSONObject();
    	}

    }
    
    public UserRequests getRequests() {
        return this.requests;
    }
    
    public ClientIdentity getIdentity() {
        return identity;
    }
    
    public JSONObject getJSON() {
        return this.json;
    }
    
    /**
     * commit must be called in all cases where the JSON content was changed.
     * @return
     */
    public Accounting commit() {
        parent.commit();
        return this;
    }

    public JsonTray getParent() { return this.parent; }
}
