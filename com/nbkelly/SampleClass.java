package com.nbkelly;

/* imports */

/* class */
public class SampleClass extends Drafter {    
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */

    private FileCommand inputFile;
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();
	
	println("Hello World");
		
	DEBUG(1, t.split("Finished Processing"));

	return 1;
    }

    /* insert block */
    
    /* set commands */
    @Override public Command[] setCommands() {
	//inputFile = new FileCommand(/*name =      */ "Input File",
	//			    /*description=*/ "Auxiliary data for this program",
	//			    /*mandatory  =*/ true,
	//			    /*[synonyms] =*/ "-f", "--file");	

	//do you want paged input to be optional? This is mainly a debugging thing,
	//or a memory management/speed thing
	_PAGE_OPTIONAL = false; //page does not show up as a user input command
	_PAGE_ENABLED = false;  //page is set to disabled by default

	//return new Command[] {inputFile};
	return new Command[0];
    }
    
    /* act after commands processed */
    @Override public void actOnCommands() {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
    }
    
    /* main */
    public static void main(String[] argv) {
	new SampleClass().run(argv);
    }
}
