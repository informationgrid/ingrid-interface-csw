/**
 * 
 */
package de.ingrid.interfaces.csw.utils;

import junit.framework.TestCase;

/**
 * @author Administrator
 * 
 */
public class IBusHelperTest extends TestCase {

	/**
	 * @param name
	 */
	public IBusHelperTest(String name) {
		super(name);
	}


	public void testGetPagingsignle() {

		int startPosition=4;
		int requestedHits=2;
		
		int[] paging = IBusHelper.getPaging(startPosition, requestedHits);
		int searchResultStart = paging[1] == 1 ? paging[0] : Math.max((paging[0] - 1)*paging[1], 1);
		int searchResultEnd = searchResultStart + paging[1] - 1;
		System.out.println("[(startPosition, requestedHits):(start, end) -> (pageNo, pageSize):(start, end)] = [(" + startPosition + ", " + requestedHits + "):(" + startPosition + ", " + (startPosition + requestedHits - 1) + ") -> (" + paging[0]
						+ ", "
						+ paging[1]
						+ "):("
						+ searchResultStart
						+ ", "
						+ searchResultEnd  + ")]");
		assertEquals(true,
				searchResultEnd >= (startPosition + requestedHits - 1));
		assertEquals(true, searchResultStart <= startPosition);
	}
	
	
	/**
	 * Test method for
	 * {@link de.ingrid.interfaces.csw.utils.IBusHelper#getPaging(int, int)}.
	 */
	public void testGetPaging() {

		for (int startPosition=1; startPosition <= 100; startPosition++ ) {
			for (int requestedHits=1; requestedHits <= 20; requestedHits++) {
				int[] paging = IBusHelper.getPaging(startPosition, requestedHits);
				int searchResultStart = paging[1] == 1 ? paging[0] : Math.max((paging[0] - 1) * paging[1] + 1, 1);
				int searchResultEnd = searchResultStart + paging[1] - 1;
				System.out.println("[(startPosition, requestedHits):(start, end) -> (pageNo, pageSize):(start, end)] = [(" + startPosition + ", " + requestedHits + "):(" + startPosition + ", " + (startPosition + requestedHits - 1) + ") -> (" + paging[0]
								+ ", "
								+ paging[1]
								+ "):("
								+ searchResultStart
								+ ", "
								+ searchResultEnd  + ")]");
				assertEquals(true,
						searchResultEnd >= (startPosition + requestedHits - 1));
				assertEquals(true, searchResultStart <= startPosition);
			}
		}
	}

	
	
}
