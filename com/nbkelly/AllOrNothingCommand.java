package com.nbkelly;

import java.util.TreeSet;
/*
 * Enforces all of, or none of, the commands given
 */
public class AllOrNothingCommand extends Command {
    public Command[] subCommands;
    public int[] match_status;
    
    public AllOrNothingCommand(String name, String description, Command... subCommands) {
	if(subCommands.length == 0)
	    throw new IllegalArgumentException("All or Nothing given with zero inputs");
	
	this.subCommands = subCommands;
	this.match_status = new int[subCommands.length];
	mandatory = true;
	setName(name);
	setDescription(description);
    }

    public int match(String[] argv, int index) {
	for(int i = 0; i < subCommands.length; i++) {
	    //get the previous match
	    int pre = match_status[i];

	    //see if we match anything here
	    if(subCommands[i] == null)
		throw new IllegalArgumentException("command is null");
	    int submatch = subCommands[i].match(argv, index);
	    if(submatch < 0) {
		match_status[i] = submatch;
		invalid++;
		return submatch;		
	    }
	    else if(submatch > 0) {
		match_status[i] = submatch;
		return submatch;
	    }
	}

	return 0;
    }

    @Override public String usage(boolean colorEnabled, boolean supressMandatory) {
	return usage(colorEnabled);
    }
    
    @Override public String usage(boolean colorEnabled) {
	String res = "All or Nothing:\n";
	for(int i = 0; i < subCommands.length; i++) {
	    res = res + subCommands[i].usage(colorEnabled, /* suppressMandatory */ valid());
	    if(i != subCommands.length - 1)
		res = res + "\n";
	}

	return frontPad(res);
    }
    
    private String frontPad(String s) {
	return s.replaceAll("\n", "\n    | ");
    }

    @Override public boolean valid() {
	return matched();
    }
    
    @Override public boolean matched() {
	int hits = 0;
	for(int i = 0; i < match_status.length; i++) {
	    if(match_status[i] > 0)
		hits++;
	    else if (match_status[i] < 0)
		return false;
	}

	return (hits == 0 || hits == match_status.length);	
    }
}
