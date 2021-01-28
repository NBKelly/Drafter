package com.nbkelly.working;

import com.nbkelly.FileCommand;
import com.nbkelly.DirectoryCommand;
import com.nbkelly.Command;
import com.nbkelly.RegexCommand;
import com.nbkelly.BooleanCommand;

import com.nbkelly.Drafter;
import com.nbkelly.Timer;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class MakeHelper extends Drafter {
    private FileCommand inputFile;
    
    private RegexCommand name;
    private RegexCommand packageName;
    private DirectoryCommand directoryName;

    private RegexCommand auxiliaryPackageName;
    private DirectoryCommand auxiliaryDirectoryName;

    private FileCommand importFileCommand;
    private FileCommand insertCodeCommand;

    private FileCommand insertParamsCommand;
    private FileCommand insertSolveCommand;
    private FileCommand insertSetParams;
    private FileCommand insertPostCommand;

    private BooleanCommand overwriteMain;
    private BooleanCommand overwriteAux;
    private BooleanCommand injectSilent;

    private final String sampleClassFileName = "/com/nbkelly/SampleClass.java";
           
    private String baseAuxFilePath = "/com/nbkelly/";
    private String[] auxFilesToCopy = new String[] {
	"BooleanCommand.java",
	"Color.java",
	"Command.java",
	"DebugLogger.java",
	"DirectoryCommand.java",
	"Drafter.java",
	"FileCommand.java",
	"IntCommand.java",
	"OptionalIntCommand.java",
	"RegexCommand.java",
	"StringCommand.java",
	"AllOrNothingCommand.java",
	"SingleChoiceCommand.java",
	"Timer.java"	
    };

    private final String _import_loc = "/* imports */";    
    private final String _classname_loc = "/* class */";
    private final String _insert_code_loc = "/* insert block */";    
    private final String _main_loc = "/* main */";

    
    private final String _insert_params_loc = "/* insert params */";
    private final String _insert_in_post_loc = "/* insert in post */";
    
    private final String _insert_in_solve_loc = "/* insert in solve */";
    private final String _end_insert_in_solve_loc = "/* end insert in solve */";
    
    private final String _insert_in_set_loc = "/* insert in set */";
    private final String _end_insert_in_set_loc = "/* end insert in set */";
    
    private final String baseClassName = "Drafter";
    
    private final String _classname_replacement = "public class %s extends %s {"; //classname, drafter
    private final String[] _main_replacement = new String[] {
	"    public static void main(String[] argv) {",
	"        new %s().run(argv);",
	"    }"
    };

    private final String[] _default_imports = new String[] {
	"Drafter",
	"Command",
	"FileCommand",
	"Timer"
    };
    
    //WORKFLOW: Set an needed commands, then act on the commands, then solve the problem    
    @Override public int solveProblem() {
	Timer t = makeTimer();

	String className = name.getValue();
	DEBUGF(2, "  Class Name: %s%n", className);
	DEBUGF(2, "   File Name: %s.java%n", className);
	DEBUGF(2, "Package Name: package %s;%n", packageName.getValue());
	DEBUGF(2, "    Place At: %s%n", directoryName.getValue().toString());
	DEBUG(2, "");
	DEBUGF(2, " Aux Package: package %s;%n", auxiliaryPackageName.getValue());
	DEBUGF(2, "    Place At: %s%n", auxiliaryDirectoryName.getValue());
	
	DEBUG(2, "");

	//has the additonal imports file been specified?
	if(importFileCommand.matched > 0)
	    DEBUGF(2, "     Imports: %s%n", importFileCommand.getValue());

	//has the additonal imports file been specified?
	if(insertCodeCommand.matched > 0)
	    DEBUGF(2, " Insert Code: %s%n", insertCodeCommand.getValue());

	//has the insert params file been specified?
	if(insertParamsCommand.matched > 0)
	    DEBUGF(2, " Insert Params: %s%n", insertParamsCommand.getValue());

	//has the insert post file been specified?
	if(insertPostCommand.matched > 0)
	    DEBUGF(2, " Insert In Post: %s%n", insertPostCommand.getValue());

	//has the insert in solve file been specified?
	if(insertSolveCommand.matched > 0)
	    DEBUGF(2, " Insert in Solver: %s%n", insertSolveCommand.getValue());

	//has the insert in setCommands file been specified?
	if(insertSetParams.matched > 0)
	    DEBUGF(2, " Insert Params: %s%n", insertSetParams.getValue());
	
	DEBUG(2, "");

	//now we have all the variables we NEED, what do we need to do?
	//first, we open the required file:
	File sampleClassFile = new File(sampleClassFileName);
	File baseClassFile = new File(sampleClassFileName);

	//read through each file in the auxFilestoCopy thing
	//if the file does not already exist in the target directory, then we create it
	for(int i = 0; i < auxFilesToCopy.length; i++) {
	    String aux_path = baseAuxFilePath + auxFilesToCopy[i];
	    String res_path = auxiliaryDirectoryName.getValue().getPath() + "/" + auxFilesToCopy[i];
	    File fileToCopy = readResource(aux_path); //new File(aux_path);
	    if(fileToCopy == null)
		FAIL(1);

	    
	    var outputFileLines = readFile(fileToCopy);
	    if(outputFileLines == null)
		FAIL(1);

	    //replace the relevant parts of the file
	    outputFileLines.set(0, packageName(auxiliaryPackageName.getValue()));
	    
	    //print out the contents of the file, for the curious
	    DEBUG(4, res_path);
	    for(String s : outputFileLines)
		DEBUGF(4, "> %s%n", s);
	    DEBUG(4, "");

	    //put these in the new files
	    File outputFile = new File(res_path);
	    if(!writeFile(outputFile, outputFileLines, overwriteAux))
		FAIL(1);
	}

	
	//now we do the main file
	DEBUG("Reading primary file");
	File mainFile = readResource(sampleClassFileName);
	if(mainFile == null)
	    FAIL(1);
	
	var mainFileLines = readFile(mainFile);
	
	if(mainFileLines == null)
	    FAIL(1);	

	DEBUG("Assembling new project file");
	mainFileLines = assembleMainFile(mainFileLines);

	if(mainFileLines == null)
	    FAIL(1);
	
	for(String s : mainFileLines)
	    DEBUGF(3, "> %s%n", s);

	//write the output file to the expected location
	File outputFile = new File(directoryName.getValue().toString() + "/" + className + ".java");
	if(!writeFile(outputFile, mainFileLines, overwriteMain))
	    FAIL(1);
	
	DEBUG(1, t.split("Finished Processing"));

	return 0;
    }

    private String composeImport(String s) {
	return String.format("import %s.%s;", auxiliaryPackageName.getValue(), s);
    }

    private ArrayList<String> assembleMainFile(ArrayList<String> originalFile) {
	//first, insert the package name
	ArrayList<String> res = new ArrayList<String>();
	int index = 1;
	
	//we need to extract the package name from the original file and produce it
	String packageNameProcessed = packageName(packageName.getValue());
	res.add(packageNameProcessed);
	DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	
	/* the first thing we hunt for is imports */
	DEBUG(2, "Hunting for import block...");
	while(index < originalFile.size() && !originalFile.get(index).contains(_import_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR IMPORT INJECT LOCATION");
	    return null;
	}

	/*
	 *
	 * INSERT THE IMPORTS FILE HERE
	 *
	 */
	DEBUG(2, "Inserting default import statements...");
	res.add(originalFile.get(index++));
	DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	
	for(int i = 0; i < _default_imports.length; i++) {
	    res.add(composeImport(_default_imports[i]));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(importFileCommand.matched > 0) {
	    DEBUG(2, "Inserting extra import statements...");
	    //get all lines from the extra import file
	    var importedLines = readFile(importFileCommand.value);
	    res.add("");
	    if(injectSilent.matched == 0) res.add("/* imports from file */");
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    for(String s : importedLines) {
		res.add(s);
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }
	}
	
	

	/* hunt for the classname */
	DEBUG(2, "Hunting for class opening block...");
	while(index < originalFile.size() && !originalFile.get(index).contains(_classname_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR CLASSNAME INJECT LOCATION");
	    return null;
	}

	res.add(String.format(_classname_replacement, name.getValue(), baseClassName));
	DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	
	//we skip the /* class */ tag
	index += 2;

	/* hunt for the insert params block */
	DEBUG(2, "Hunting for insert-params block");
	while(index < originalFile.size() && !originalFile.get(index).contains(_insert_params_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR INSERT_PARAMS LOCATION");
	    return null;
	}

	/*
	 *
	 * INJECT PARAMS HERE
	 *
	 */
	if(insertParamsCommand.matched > 0) {
	    //determine amount of whitespace at start of line
	    int padSide = originalFile.get(index).indexOf(_insert_params_loc);
	    String pad = "";
	    for(int i = 0; i < padSide; i++)
		pad += " ";

	    DEBUG(2, "Injecting parameters from " + insertParamsCommand.getValue());
	    //get all lines from the extra import file
	    var importedLines = readFile(insertParamsCommand.value);
	    //res.add("");
	    if(injectSilent.matched == 0) res.add(pad + "/* params injected from file */");
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    for(String s : importedLines) {
		res.add(pad + s);
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }
	}

	index++;

	/* hunt for insert-in-solve */
	DEBUG(2, "Hunting for insert-in-solve block");
	while(index < originalFile.size() && !originalFile.get(index).contains(_insert_in_solve_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR INSERT_IN_SOLVE LOCATION");
	    return null;
	}

	/*
	 *
	 * INSERT SOLUTION HERE
	 *
	 */
	if(insertSolveCommand.matched > 0) {
	    //determine amount of whitespace at start of line
	    int padSide = originalFile.get(index).indexOf(_insert_in_solve_loc);
	    String pad = "";
	    
	    for(int i = 0; i < padSide; i++)
		pad += " ";

	    DEBUG(2, "Injecting code from " + insertSolveCommand.getValue());
	    //get all lines from the extra import file
	    var importedLines = readFile(insertSolveCommand.value);
	    //res.add("");
	    if(injectSilent.matched == 0) res.add(pad + "/* code injected from file */");
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    for(String s : importedLines) {
		res.add(pad + s);
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }

	    while(index < originalFile.size() && !originalFile.get(index).contains(_end_insert_in_solve_loc))
		index++;
	    if(index >= originalFile.size()) {
		ERR("RAN OUT OF INPUT WHEN HUNTING FOR END_INSERT_IN_SOLVE LOCATION");
		return null;
	    }
	}
	else {
	    index++;

	    while(index < originalFile.size() && !originalFile.get(index).contains(_end_insert_in_solve_loc)) {
		res.add(originalFile.get(index++));
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }

	    if(index >= originalFile.size()) {
		ERR("RAN OUT OF INPUT WHEN HUNTING FOR END_INSERT_IN_SOLVE LOCATION");
		return null;
	    }
	}

	index++;
	



	/* hunt for insert block */
	DEBUG(2, "Hunting for code-insertion block");
	while(index < originalFile.size() && !originalFile.get(index).contains(_insert_code_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR CODE-INJECT LOCATION");
	    return null;
	}

	/*
	 *
	 * INJECT CODE HERE
	 *
	 */
	if(insertCodeCommand.matched > 0) {
	    //determine amount of whitespace at start of line
	    int padSide = originalFile.get(index).indexOf(_insert_code_loc);
	    String pad = "";
	    for(int i = 0; i < padSide; i++)
		pad += " ";
	    
	    DEBUG(2, "Injecting code from " + insertCodeCommand.getValue());
	    //get all lines from the extra import file
	    var importedLines = readFile(insertCodeCommand.value);
	    //res.add("");
	    if(injectSilent.matched == 0) res.add(pad + "/* code injected from file */");
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    for(String s : importedLines) {
		res.add(pad + s);
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }
	    //res.add("");
	    //DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    
	}
	//res.add(originalFile.get(index++));
	//DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	index++;
	


	/* hunt for insert-commands */
	DEBUG(2, "Hunting for insert-in-solve block");
	while(index < originalFile.size() && !originalFile.get(index).contains(_insert_in_set_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR INSERT_IN_SET LOCATION");
	    return null;
	}

	/*
	 *
	 * INSERT COMMANDS HERE
	 *
	 */
	if(insertSetParams.matched > 0) {
	    //determine amount of whitespace at start of line
	    int padSide = originalFile.get(index).indexOf(_insert_in_set_loc);
	    String pad = "";
	    for(int i = 0; i < padSide; i++)
		pad += " ";

	    DEBUG(2, "Injecting code from " + insertSetParams.getValue());
	    //get all lines from the extra import file
	    var importedLines = readFile(insertSetParams.value);
	    //res.add("");
	    if(injectSilent.matched == 0) res.add(pad + "/* code injected from file */");
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    for(String s : importedLines) {
		res.add(pad + s);
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }

	    while(index < originalFile.size() && !originalFile.get(index).contains(_end_insert_in_set_loc))
		index++;
	    if(index >= originalFile.size()) {
		ERR("RAN OUT OF INPUT WHEN HUNTING FOR END_INSERT_IN_SET LOCATION");
		return null;
	    }
	}
	else {
	    index++;

	    while(index < originalFile.size() && !originalFile.get(index).contains(_end_insert_in_set_loc)) {
		res.add(originalFile.get(index++));
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }

	    if(index >= originalFile.size()) {
		ERR("RAN OUT OF INPUT WHEN HUNTING FOR END_INSERT_IN_SET LOCATION");
		return null;
	    }
	}

	index++;
	

	/* hunt for post-process block */
	DEBUG(2, "Hunting for command post-process block");
	while(index < originalFile.size() && !originalFile.get(index).contains(_insert_in_post_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR COMMAND POST-PROCESS LOCATION");
	    return null;
	}

	/*
	 *
	 * INJECT POST_PROCESS HERE
	 *
	 */
	if(insertPostCommand.matched > 0) {
	    //determine amount of whitespace at start of line
	    int padSide = originalFile.get(index).indexOf(_insert_in_post_loc);
	    String pad = "";
	    for(int i = 0; i < padSide; i++)
		pad += " ";
	    
	    DEBUG(2, "Injecting code from " + insertPostCommand.getValue());
	    //get all lines from the extra import file
	    var importedLines = readFile(insertPostCommand.value);
	    //res.add("");
	    if(injectSilent.matched == 0) res.add(pad + "/* code injected from file */");
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    for(String s : importedLines) {
		res.add(pad + s);
		DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    }
	    //res.add("");
	    //DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	    
	}
	//res.add(originalFile.get(index++));
	//DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	index++;
	
	/* hunt for main block */
	DEBUG(2, "Hunting for main block...");
	while(index < originalFile.size() && !originalFile.get(index).contains(_main_loc)) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}

	if(index >= originalFile.size()) {
	    ERR("RAN OUT OF INPUT WHEN HUNTING FOR MAIN LOCATION");
	    return null;
	}

	res.add(_main_replacement[0]);
	DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	res.add(String.format(_main_replacement[1], name.getValue()));
	DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	res.add(_main_replacement[2]);
	DEBUGF(4, "> %s%n", res.get(res.size() - 1));

	       
	/* finish off remainder of file */
	DEBUG(2, "Hunting for EOF...");
	index += 4;
	while(index < originalFile.size()) {
	    res.add(originalFile.get(index++));
	    DEBUGF(4, "> %s%n", res.get(res.size() - 1));
	}
	
	return res;
    }

    //private final String _import_loc = "/* imports */";    
    //private final String _classname_loc = "/* class */";
    //private final String _insert_code_loc = "/* insert block */";
    //private final String _main_loc = "/* main */";
    //
    //private final String _classname_replacement = "public class %s extends %s {"; //classname, drafter
    //private final String[] _main_replacement = new String[] {
    //	"\tpublic static void main(String[] argv) {",
    //	"\t\tnew %s().run(argv);",
    //	"\t}"
    //}

    private File readResource(String path) {
	try {
	    File f = new File(getClass().getResource(path).toURI());	    
	    return f;
	} catch (Exception e) {
	    ERR("Couldn't load resource at " + path);
	    ERR(e.toString());
	    return null;
	}
    }
    
    private ArrayList<String> readFile(File fileToCopy) {
	//check the file exists
	if(!fileToCopy.exists()) {
	    ERR(String.format("The file %s, which should exist, not!", fileToCopy));
	    return null;
	}
	
	//check the file is readable
	//check the file exists
	if(!Files.isReadable(fileToCopy.toPath())) {
	    ERR(String.format("The file %s, which does exist, is not readable!", fileToCopy));
	    return null;
	}
	
	ArrayList<String> outputFileLines = new ArrayList<String>();
	
	try {
	    DEBUG(1, "Reading File: " + fileToCopy);
	    Scanner sc = new Scanner(fileToCopy);
	    
	    //the first line contains the old package name - discard this
	    //sc.nextLine();
	    //outputFileLines.add(packageName(auxiliaryPackageName.getValue()));
	    
	    while(sc.hasNextLine())
		outputFileLines.add(sc.nextLine());
	    
	    sc.close();
	    
	} catch (Exception e) {
	    ERR(String.format("Failure when reading from file %s", fileToCopy));
	    ERR(e.toString());
	    return null;
	}

	return outputFileLines;
    }



		
    
    private boolean writeFile(File outputFile, ArrayList<String> contents, BooleanCommand overwrite) {
	try {
	    DEBUG(1, "Write File: " + outputFile);
	    
	    if(outputFile.exists() && overwrite.matched > 0) {
		//delete the file
		try {
		    DEBUG(1, String.format("Overwriting file %s because flag '%s' was set.",
					   outputFile.toString(), overwrite.getName()));
		    outputFile.delete();
		} catch (Exception e) {
		    ERR(String.format("Unable to delete file %s", outputFile.toString()));
		    throw e;
		}
	    }
	    
	    else if (overwrite.matched == 0 && outputFile.exists()) {
		DEBUG(1, String.format("Skipping file %s because it already exists.", outputFile.toString()));
		return true;
	    }
	    
	    if(!outputFile.exists()) {
		//create the file
		try {
		    if(!outputFile.createNewFile())
			throw new Exception("Could not create file (createNewFile returned false)");
		} catch (Exception e) {
		    ERR(String.format("Unable to create file %s", outputFile.toString()));
		    ERR(e.toString());
		    return false;
		}
	    }
	    //write to the file
	    FileWriter writer = new FileWriter(outputFile);
	    for(String s : contents)
		writer.write(s + "\n");
	    writer.close();
	} catch (Exception e) {
	    ERR(String.format("Unable to complete the writing or replacement of file %s!",
			      outputFile.toString()));
	    ERR(e.toString());
	    return false;
	}

	return true;
    }
    
    private String packageName(String s) {
	return String.format("package %s;", s);
    }
	    
    /* set commands */
    @Override public Command[] setCommands() {
	/* What do we need for this to work?
	   -n Name          : -> name of class
	   -p Package       : -> package name
	   -l --location    : -> package Directory
	   -ad Auxiliary class Directory
	   -ap Auxiliary package name
	   --imports        : imports file
	   --insert-blocks  : inserts the block of code at the insert-block location
	   --overwrite-main
	   --overwrite-aux
	*/

	/* valid java classnames basically require parity with valid OS classnames.
	   Additionally, following the pattern: JavaLetter [JavaDigit]* 
	   This can be given with:
	   regex = ^[^\d\s][^\s]*$
	*/
	//match one java_letter, followed by any number of java digits
	final String validClassName = "^[^\\d\\s\\.\\\\\\/:\\(\\)\\{\\}\\!\\%\\+\\-\\*\\^\\<\\>&\\|\\=\\~\\?\\[\\]][^\\s\\.\\\\\\/:\\(\\)\\{\\}\\!\\%\\+\\-\\*\\^\\<\\>&\\|\\=\\~\\?\\[\\]]{0,249}$";
	//my idea of valid comes from this article: http://dolszewski.com/java/java-class-naming-ultimate-guideline/
	final String validPackage = "^[a-z]+(\\.[a-z]+)*$";



	// Sneed's Feed & Seed
	name = new RegexCommand(null, MANDATORY, validClassName, "-n", "--class-name")
	    .setName("Class Name")
	    .setDescription("Name of the primary generated class. Must be a valid java classname.");

	packageName = new RegexCommand(null, MANDATORY, validPackage, "-p", "--package-name")
	    .setName("Package Name")
	    .setDescription("The package name for the main project file. Must follow package naming conventions");

	directoryName = new DirectoryCommand("Package Directory",
					     "Directory in which the main program goes",
					     MANDATORY,
					     false,
					     "-l", "--location", "--primary-directory");

	auxiliaryPackageName = new RegexCommand(null, MANDATORY, validPackage, "-ap", "--aux-package-name")
	    .setName("Auxiliary Package Name")
	    .setDescription("The package name for the auxiliary project classes."
			    + " Must follow package naming  conventions");

	auxiliaryDirectoryName = new DirectoryCommand("Auxiliary Package Directory",
						      "Directory in which the auxiliary classes go",
						      MANDATORY,
						      false,
						      "-ad", "--auxiliary-directory");
	

	importFileCommand = new FileCommand("Additional Imports",
					    "File from which additional imports should be taken. This is an optional argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--additional-imports");

	insertCodeCommand = new FileCommand("Inject Code Block",
					    "File from which additional code should be inserted (at the insert-code flag). This is an optional argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--insert-block");
	
	overwriteAux = new BooleanCommand(false, "--overwrite-aux")
	    .setName("Overwrite Auxiliary Files")
	    .setDescription("If enabled, auxiliary files that already exist will be overwritten where possible");

	overwriteMain = new BooleanCommand(false, "--overwrite-main")
	    .setName("Overwrite Main Files")
	    .setDescription("If enabled, main files that already exist will be overwritten where possible");

	injectSilent = new BooleanCommand(false, "--silent-injections")
	    .setName("Silent injections")
	    .setDescription("If enabled, disables code-injection headers where possible");




	insertParamsCommand = new FileCommand("Inject Parameters",
					    "File from which additional code should be inserted (at the insert-params flag). This is an optional argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--insert-params");

	insertSolveCommand = new FileCommand("Inject into Solver",
					    "File from which additional code should be inserted (at the insert-solve flag). This is an optional argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--insert-in-solution");

	insertSetParams = new FileCommand("Inject into Command-Definition",
					    "File from which additional code should be inserted (at the insert-in-set flag). This is an optional argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--insert-commands");

	insertPostCommand = new FileCommand("Inject into Command post-processing",
					    "File from which additional code should be inserted (at the insert-in-post flag). This is an optional argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--insert-in-post");
	
	
	//inputFile = new FileCommand(/*name =      */ "Input File",
	//			    /*description=*/ "Auxiliary data for this program",
	//			    /*mandatory  =*/ true,
	//			    /*[synonyms] =*/ "-f", "--file");	

	//do you want paged input to be optional? This is mainly a debugging thing,
	//or a memory management/speed thing
	_PAGE_OPTIONAL = false;
	_PAGE_ENABLED = false;

	return new Command[] {name, packageName, directoryName,
			      auxiliaryPackageName, auxiliaryDirectoryName,
			      importFileCommand, insertCodeCommand,
			      insertParamsCommand, insertSolveCommand,
			      insertSetParams, insertPostCommand,
			      injectSilent,
			      overwriteAux, overwriteMain
	};
	//return new Command[0];
    }

    
    
    /* act after commands processed */
    @Override public void actOnCommands() {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
	//use this if you have any complicated pre-processing you want to keep out of the
	//main solveProblem function

	//here, we just want to make these directories if they do not exist
	if(!auxiliaryDirectoryName.getValue().exists())
	    auxiliaryDirectoryName.getValue().mkdirs();

	if(!directoryName.getValue().exists())
	    directoryName.getValue().mkdirs();
    }
    
    /* main */
    public static void main(String[] argv) {
	new MakeHelper().run(argv);
    }    
}
