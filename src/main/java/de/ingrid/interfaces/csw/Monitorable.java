/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw;

/**
 * Interface for classes that can provide information about itself.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Monitorable {

	/**
	 * Get the current status of the instance.
	 * 
	 * @return Status
	 */
	public Status getStatus();
}
