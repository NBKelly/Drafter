package com.nbkelly;

public class SampleClass extends ConceptHelperV2 {
    private FileCommand inputFile;
    //WORKFLOW: Set an needed commands, then act on the commands, then solve the problem    
    @Override public void solveProblem() {
	Timer t = makeTimer();

	while(hasNextLine())
	    println(nextLine());

	println("Hello World");

	println(inputFile.value);

	DEBUGF(1, t.split("Finished Processing"));
    }
    
    /* set commands */
    @Override public Command[] setCommands() {
	inputFile = new FileCommand(/*name =      */ "Input File",
				    /*description=*/ "Auxiliary data for this program",
				    /*mandatory  =*/ true,
				    /*[synonyms] =*/ "-f", "--file");	

	//do you want paged input to be optional? This is mainly a debugging thing,
	//or a memory management/speed thing
	_PAGE_OPTIONAL = false;
	_PAGE_ENABLED = false;

	return new Command[] {inputFile};
	//return new Command[0];
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
