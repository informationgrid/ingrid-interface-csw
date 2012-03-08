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
