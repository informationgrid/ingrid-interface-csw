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

	/**
	 * Compute paging parameters so the resulting result range includes the
	 * requested result range as states by startPosition and requestedHits.
	 * 
	 * The iBus search interface takes only pageNo and pageSize as parameter to
	 * determine the start- and number of hits to return. If startPosition is no
	 * multiple of requestedHits we need special treatment...
	 * 
	 * @param startPosition
	 * @param requestedHits
	 * @return Returns an array of int with int[0] is the pageNo and int[1] is
	 *         the pageSize.
	 */
	public static int[] getPaging(final int startPosition,
			final int requestedHits) {
		int[] results = new int[2];
		results[1] = requestedHits;
		results[0] = requestedHits == 1 ? startPosition : Math.max(
				(int) (startPosition / requestedHits) + 1, 1);
		int searchResultStart = results[1] == 1 ? results[0] : Math.max(
				(results[0] - 1) * results[1] + 1, 1);
		int searchResultEnd = searchResultStart + results[1] - 1;

		// find a new page size which includes the startPosition and the
		// (startPosition + requestedHits)
		while (searchResultStart > startPosition
				|| searchResultEnd < (startPosition + requestedHits - 1)) {
			results[1]++;
			results[0] = Math.max((int) ((startPosition) / results[1]) + 1, 1);
			searchResultStart = Math.max((results[0] - 1) * results[1] + 1, 1);
			searchResultEnd = searchResultStart + results[1] - 1;
		}
		return results;
	}
}
