/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.interfaces.csw.admin.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to add/remove and to execute added command object like synchronize of plug description and mapping directory.
 *
 */
public class Command {
	private List<Command> commandList;
	
	public Command() {
		this.commandList = new ArrayList<Command>();
	}

	/**
	 * Add new command to a list of command.
	 * 
	 * @param command
	 */
	public void add(Command command){
		if(commandList == null){
			commandList = new ArrayList<Command>();
		}
		commandList.add(command);
	}
	
	/**
	 * Remove a command from list.
	 * 
	 * @param command
	 */
	public void remove(Command command){
		if(commandList != null && !commandList.isEmpty()){
			commandList.remove(command);
		}
	}
	
	/**
	 * Execution of all command in a command list.
	 */
	public void execute(){
		for(int i=0; i<commandList.size();i++){
			commandList.get(i).execute();	
		}
	}
	
	/**
	 * Remove all command objects from command list.
	 */
	public void clear(){
		if(commandList != null && !commandList.isEmpty()){
			commandList.clear();			
		}
	}
}
