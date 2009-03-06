package de.ingrid.interfaces.csw.utils;

import java.util.HashMap;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.utils.IBus;

public class IBusHelper {
	private static IBus bus = null;
	
	private static boolean cache = false;
	
	public static IBus getIBus() throws Exception {
		BusClient client = null;
		
		if (bus == null) {
			client = BusClientFactory.createBusClient();
	
			CswConfig config = CswConfig.getInstance();
        
	        if (config.getBoolean("csw.enable.caching", true) == true) {
	        	bus = client.getCacheableIBus();
	        	cache = true;
	        } else {
	        	bus = client.getNonCacheableIBus();
	        }
		}
        return bus;
	}
	
    public static void injectCache(HashMap map) {
    	if (!map.containsKey("cache")) {
	    	if (cache) {
	    		map.put("cache", "on");
	        } else {
	        	map.put("cache", "off");
	        }
    	}
    }
}
