import java.util.Arrays;

public /*abstract*/ class ConceptHelperV2 {
    /** Does this session support/enable color? */
    private boolean _COLOR_ENABLED = false;
    private boolean _COLOR_CHECKED = false;
    private boolean _COLOR_HARD_DISABLED = false;

    /** used in argument processing */    
    private final int _ARGUMENT_MATCH_FAILED = -1;

    /** list of all commands */
    private Command[] _commands = null;

    /** page mode */
    private boolean _PAGE_ENABLED = false;

    /** interactive mode */
    private boolean _INTERACTIVE_MODE = false;

    /** debug level */
    protected int _DEBUG_LEVEL = 0;
    protected boolean _DEBUG = false; //true if _DEBUG_LEVEL > 0
    
    /** performs check-once analysis to enable colors */
    private boolean _COLOR_ENABLED() {
	if(_COLOR_HARD_DISABLED)
	    return false;

	else if (!_COLOR_CHECKED) {
	    //this supposedly only works on linux - no clue what the fuck to do on windows
	    if(System.console() != null && System.getenv().get("TERM") != null)
		_COLOR_ENABLED = true;
	    _COLOR_CHECKED = true;
	}
	
	return _COLOR_ENABLED;
    }
    
    private ConceptHelperV2 self = null;
    


    
    //THINGS THAT NEED TO BE OVERRIDDEN
    protected void actOnCommands() { };

    protected Command[] setCommands() {
	return new Command[0];
    }

    protected void solveProblem() {
	println("Normal line");
	print("A");
	print("B");
	println(a2s(new char[]{'C', 'D'}));
	//'C');

	DEBUG("PLAIN DEBUG");
	DEBUG(0, "DEBUG LEVEL 0");
	DEBUG(1, "DEBUG LEVEL 1");
	DEBUG(2, "DEBUG LEVEL 2");
	DEBUGF(3, "DEBUG LEVEL %d%n", 3);
	DEBUG(4, "DEBUG LEVEL 4");
	DEBUG(5, "DEBUG LEVEL 5");
	DEBUG(6, "DEBUG LEVEL 6");
    }



    public ConceptHelperV2() {
	self = this;
    }
    
    
    // TODO: get rid of this
    public static void main(String[] argv) {
	//first we set up the commands	
	new ConceptHelperV2().run(argv); 
    }

    /**
     * Runs the given program.
     * <p>
     * More specifically, we: <br>
     * 1) Set all default commands <br>
     * 2) *Add any additional user-based commands
     * 3) Process all commands
     * 4) Act based on the results of the default commands
     * 5) *Act on the results of all user-input commands
     * 6) Perform any pre-processing on the input
     * 7) *Run the 'solveProblem' function. This is the actual user code.
     *
     * @param argv the argument vector for the program
     * @since 1.0
     */
    private void run(String[] argv) {
	//first we set the default commands
	setDefaultCommands();
	//add in any commands the user wants to add
	addCommands(setCommands());
	//process all of the arguments
	argv = processCommands(argv);
	//act on the deafult commands
	actOnDefaultCommands();
	//act on the user commands
	actOnCommands();

	//do any other pre-processing needed
	//TODO: this part

	//run the program
	solveProblem();
    }

    
    private void setDefaultCommands() {	
	_commands = new Command[] {_debugLevel, _interactive, _page, _help, _disableColors, _ignore};	
    }    
    
    private void addCommands(Command[] c) {
	Command[] res = new Command[_commands.length + c.length];

	for(int i = 0; i < _commands.length; i++)
	    res[i] = _commands[i];

	for(int j = 0; j < c.length; j++)
	    res[j + _commands.length] = c[j];

	_commands = res;
    }

    /**
     * Process argument vector based on command set
     * @param argv input arguments 
     * @return any valid unprocessed arguments
     */
    public String[] processCommands(String[] argv) {
	int index = 0;
	int index_last = -1;

	outer:
	while(index != index_last && index < argv.length && _ignore.matched == 0) {
	    index_last = index;
	    for(int i = 0; i < _commands.length; i++) {
		int new_ind = _commands[i].match(argv, index);
		if(new_ind > 0) { //matched rule
		    index = new_ind;
		    continue outer;
		}
		else if (new_ind == _ARGUMENT_MATCH_FAILED) {
		    FAIL(_commands, 1, true);
		}
	    }
	}

	int unprocessed_args = argv.length - index;
	
	if(unprocessed_args != 0 && _ignore.matched == 0) {
	    //we have a number of unprocessed arguments
	    PRINT_ERROR_TEXT("ERROR: a number of arguments were not matched by any rule (index = " + index + ")");
	    System.err.println("Unmatched arguments: " + arrayToString(_REMAINING_ARGUMENTS(argv, index)));
	    FAIL(1);
	}
	if(_help.matched > 0)
	    FAIL(_commands, 0, false);
	if(!arguments_satisfied(_commands))
	    FAIL(_commands, 1, true);

	return _REMAINING_ARGUMENTS(argv, index);
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
     * Prints text to the stderror using red and bold     
     * @param str message to print
     */
    private void PRINT_ERROR_TEXT(String str) {
	System.err.println(Color.colorize(_COLOR_ENABLED(), str, Color.RED_BOLD));
    }

    /**
     * Fails with the given exit code
     */
    private void FAIL(int exit) {
	System.exit(exit);
    }
    
    /**
     * Prints out the usage for all of the commands, and then gracefully exists with given status code
     *
     * @param commands the set of all commands
     * @return exits the current program
     * @since 1.0
     */
    private void FAIL(Command[] commands, int exit, boolean error_only) {
	if(!error_only)
	    //display the usage of each command
	    for(int i = 0; i < commands.length; i++)		
		System.err.println(commands[i].usage(_COLOR_ENABLED()));
	else
	    //display the usage of each command with an error
	    for(int i = 0; i < commands.length; i++)
		if(commands[i].invalid())
		    System.err.println(commands[i].usage(_COLOR_ENABLED()));
	
	System.exit(exit);
    }

    /**
     * Flattens an array into a string
     * @param arr Array to flatten
     * @return String representation of array
     */
    private <T> String arrayToString(T[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i].toString() + " ");

	return res.toString() + "]";
    }

    private String arrayToString(int[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(long[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(float[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(double[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(char[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(short[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(byte[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    private String arrayToString(boolean[] arr) {
	var res = new StringBuilder("[ ");
	for(int i = 0; i < arr.length; i++)
	    res.append(arr[i] + " ");

	return res.toString() + "]";
    }

    /**
     * Cuts the input vector to a smaller size
     * @param arr the input vector
     * @param cutAt index to cut the vector at
     * @return cut argvector
     */
    private String[] _REMAINING_ARGUMENTS(String[] arr, int cutAt) {
	if(cutAt == arr.length)
	    return new String[0];
	if(cutAt == 0)
	    return arr;
	
	return Arrays.copyOfRange(arr, cutAt, arr.length);
    }



    /*********************************************************
     *
     *                    OUTPUT COMMANDS
     *
     *********************************************************/
    
    public DebugLogger logger = new DebugLogger() {
	    public void DEBUG(String s) { self.DEBUG(s); }
	    public void DEBUG(int level, Object s) { self.DEBUG(1, s); }
	    public void DEBUGF(int level, String s, Object... args) { self.DEBUGF(level, s, args); }

	    public void print(Object value) { self.print(value); }
	    public void printf(String value, Object... args) { self.printf(value, args); }
	    public void println(Object value) { self.println(value); }
	};	    
    
    //DEBUG (int level, String s)
    //DEBUG (String s) -> DEBUG(1, s)
    //DEBUGF(int level, String s, args) -> DEBUG(level, f(s, args))
    //DEBUGF(String s, args) -> DEBUGF(1, s, args)
    //
    //print
    //printf
    //println
    //
    //a2s - > arrayToString
    
    //colorize based on debug level
    //1 = black
    //2 = bold black
    //3 = yellow
    //4 = red
    //5 = bold red
    private Color _DEBUG_TO_COLOR(int level) {
	switch(level) {
	case 0:
	case 1:
	    return Color.RESET;
	case 2:
	    return Color.GREEN;
	case 3:
	    return Color.YELLOW;
	case 4:
	    return Color.RED;
	case 5:
	    return Color.RED_BOLD;
	default:
	    return Color.BLACK;
	}
    }
    
    public void DEBUG(int level, Object message) {
	if(_DEBUG_LEVEL == 0)
	    return;

	//we print anything with a level equal to or below our own
	if(_DEBUG_LEVEL >= level)
	    System.err.println(_DEBUG_COLORIZE(message.toString(), _DEBUG_TO_COLOR(level)));
    }

    public void DEBUG(Object message) {
	DEBUG(1, message);
    }

    public void DEBUGF(int level, String message, Object... args) {
	if(_DEBUG_LEVEL == 0)
	    return;

	if(_DEBUG_LEVEL == 0)
	    return;

	String tmp = String.format(message, args);
	
	//we print anything with a level equal to or below our own
	if(_DEBUG_LEVEL >= level)
	    System.err.print(_DEBUG_COLORIZE(tmp, _DEBUG_TO_COLOR(level)));
    }

    public void DEBUGF(String message, Object... args) {
	DEBUGF(1, message, args);
    }

    private String _DEBUG_COLORIZE(String s, Color c) {
	return Color.colorize(_COLOR_ENABLED(), s, c);
    }
    
    public void print(Object a) {
	System.out.print(a);
    }

    public void printf(String a, Object... args) {
	System.out.printf(a, args);
    }

    public void println(Object a) {
	System.out.println(a);
    }


    //array to str

    
    public String a2s(Object[] a) {
	return arrayToString(a);
    }

    public String a2s(int[] a) {
	return arrayToString(a);
    }

    public String a2s(long[] a) {
	return arrayToString(a);
    }

    public String a2s(float[] a) {
	return arrayToString(a);
    }

    public String a2s(double[] a) {
	return arrayToString(a);
    }

    public String a2s(boolean[] a) {
	return arrayToString(a);
    }

    public String a2s(byte[] a) {
	return arrayToString(a);
    }

    public String a2s(short[] a) {
	return arrayToString(a);
    }

    public String a2s(char[] a) {
	return arrayToString(a);
    }
        

    /*********************************************************
     *
     *                   DEFAULT COMMANDS
     *
     *********************************************************/

    private final IntCommand _debugLevel = new IntCommand(0, 5, false, 0, "-d", "-D", "-debug", "--debug", "--debug-level")
	.setName("Debug Level")
	.setDescription("Sets the debug level. " + 
			"Level 0 means no debug input is displayed, " +
			"the allowable range for debug is (0, 5), "+
			"and it is up to each program to decide what to display at each level."
			+ " All debug output between levels 0 and the selected level " +
			"will be displayed during operation of the program.");

    private final BooleanCommand _interactive = new BooleanCommand(false, "-x", "-X", "--interactive-input")
	.setName("Interactive Mode")
	.setDescription("Enabled interactive Mode. While in interactive mode, " +
			"the input stream will not be pre-processed.");
    private final BooleanCommand _page = new BooleanCommand(false, "-p --page-enabled").setName("Page Mode")
	.setDescription("Sets wether page mode is or isn't enabled. If it is enabled, " +
			"Then all input that is read will be saved. All of the saved input will be readily accessible on a line-by-line basis with the page(line) function. "
			+ "This may end up using too much memory if the input happens to be particularly large. This is disabled by default.");
    private final BooleanCommand _help = new BooleanCommand(false, "-h", "-h", "--help", "--show-help")
	.setName("Display Help")
	.setDescription("Displays this help dialogue. " +
			"This dialogue will also display if one " +
			"of the inputs happens to be invalid.");
    
    private final BooleanCommand _disableColors = new BooleanCommand(false, "-dc", "--disable-colors")
	.setName("Disable Colors")
	.setDescription("Disables the output of any colorized strings");
    
    private final BooleanCommand _ignore = new BooleanCommand(false, "-i", "--ignore-remaining")
	.setName("Ignore Remaining")
	.setDescription("Ignores all remaining input");

    private void actOnDefaultCommands() {
	_COLOR_HARD_DISABLED = (_disableColors.matched > 0);
	_PAGE_ENABLED = (_page.matched > 0);
	_INTERACTIVE_MODE = (_interactive.matched > 0);
	_DEBUG_LEVEL = (_debugLevel.matched > 0 ? _debugLevel.getValue() : 0);

	//if the debug level is greater than 0, then debug mode as a whole is enabled
	_DEBUG = _DEBUG_LEVEL > 0;
    }
}
