public /*abstract*/ class ConceptHelperV2 {
    public static void main(String[] argv) {
	new ConceptHelperV2().processArgs(argv);
    }
    public void processArgs(String[] argv) {
	//how does our parser work?
	//we have a list of commands, of the form:
	//  [synonyms] [argument_count] [max_number_invoked (usually 1)] [mandatory] [default]

	//let's try for a debug arg
	IntCommand debugLevel = new IntCommand(0, 5, false, 0, "-d", "-D",
					       "-debug", "--debug", "--debug-level")
	    .setName("Debug Level")
	    .setDescription("Sets the debug level. " + 
			    "Level 0 means no debug input is displayed, " +
			    "the allowable range for debug is (0, 5), "+
			    "and it is up to each program to decide what to display at each level."
			    + " All debug output between levels 0 and the selected level " +
			    "will be displayed during operation of the program.");
	StringCommand input = new StringCommand("", true, "-x", "-X", "--interactive-input")
	    .setName("Interactive Mode")
	    .setDescription("Enabled interactive Mode. While in interactive mode," +
			    "The primary routine is expected to come from a file (or be blank)," +
			    " While the operation of the program is expected to be influenced" +
			    " by the user.");
	BooleanCommand page = new BooleanCommand(false, "--page-enabled").setName("Page Mode")
	    .setDescription("Sets wether page mode is or isn't enabled. If it is enabled, " +
			    "The entire input stream will be read in advance. Then, " +
			    "the input stream may be arbitrarily navigated or selected by line " +
			    "with 'setLine(int)' or 'getLine(int)'");
	BooleanCommand help = new BooleanCommand(false, "-h", "-h", "--help", "--show-help")
	    .setName("Display Help")
	    .setDescription("Displays this help dialogue. " +
			    "This dialogue will also display if one " +
			    "of the inputs happens to be invalid.");

	Command[] commands = new Command[] {debugLevel, input, page, help};
	
	int index = 0;
	int index_last = -1;
	String usage = "whatever usage goes here";
	outer:
	while(index != index_last && index < argv.length) {
	    index_last = index;
	    for(int i = 0; i < commands.length; i++) {
		int new_ind = commands[i].match(argv, index);
		if(new_ind > 0) { //matched rule
		    index = new_ind;
		    continue outer;
		}
		else if (new_ind == -1) {
		    FAIL(commands);
		}
	    }		
	}

	int unprocessed_args = argv.length - index;

	if(unprocessed_args != 0 || help.matched > 0 || !arguments_satisfied(commands)) {
	    FAIL(commands);
	}
	
	//give the status of all these commands
	System.out.printf("INPUT: MATCHED = %b, VALUE = %s%n", input.matched > 0, input.value);
	System.out.printf("PAGE: MATCHED = %b, VALUE = %b%n", page.matched > 0, page.value);
	System.out.printf("DEBUGLEVEL: MATCHED = %b, VALUE = %d%n",
			  debugLevel.matched > 0, debugLevel.value);

	if(help.matched > 0) {
	    for(int i = 0; i < commands.length; i++) {
		//display the usage of each command
		System.out.println(commands[i].usage());
	    }
	}
    }

    /**
     * Checks that all given arguments with mandatory principles are satisfied
     *
     * @param commands the set of all commands
     * @return True is all commands are valid, false otherwise
     * @since 1.0
     */
    private boolean arguments_satisfied(Command[] commands) {
	for(int i =0; i < commands.length; i++)
	    if(commands[i].mandatory && commands[i].matched == 0)
		return false;

	return true;
    }

    /**
     * Prints out the usage for all of the commands, and then gracefully exists with status code 1
     *
     * @param commands the set of all commands
     * @return exits the current program
     * @since 1.0
     */
    private void FAIL(Command[] commands) {
	for(int i = 0; i < commands.length; i++) {
		//display the usage of each command
	    System.err.println(commands[i].usage());
	}

	System.exit(1);
    }
}
