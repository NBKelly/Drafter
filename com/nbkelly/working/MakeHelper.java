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

    private BooleanCommand overwriteMain;
    private BooleanCommand overwriteAux;

    private final String sampleClassFileName = "com/nbkelly/SampleClass.java";
    private final String baseClassFileName = "com/nbkelly/ConceptHelperV2.java";

    private String baseAuxFilePath = "com/nbkelly/";
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
	"Timer.java"	
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
	    File fileToCopy = new File(aux_path);

	    
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
	
	ArrayList<String> baseConceptHelper = new ArrayList<String>();
	DEBUG(1, t.split("Finished Processing"));

	return 0;
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
					"-l", "--location", "--primary-directory");

	auxiliaryPackageName = new RegexCommand(null, MANDATORY, validPackage, "-ap", "--aux-package-name")
	    .setName("Auxiliary Package Name")
	    .setDescription("The package name for the auxiliary project classes."
			    + " Must follow package naming  conventions");

	auxiliaryDirectoryName = new DirectoryCommand("Auxiliary Package Directory",
						 "Directory in which the auxiliary classes go",
						 MANDATORY,
						 "-ad", "--auxiliary-directory");
	

	importFileCommand = new FileCommand("Additional Imports",
					    "File from which additional imports should be taken. This is an optioanl argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--additional-imports");

	insertCodeCommand = new FileCommand("Additonal Imports",
					    "File from which additional code should be inserted (at the insert-code flag). This is an optioanl argument, and specification for the file can be found in the readme",
					    OPTIONAL,
					    "--insert-code");
	
	overwriteAux = new BooleanCommand(false, "--overwrite-aux")
	    .setName("Overwrite Auxiliary Files")
	    .setDescription("If enabled, auxiliary files that already exist will be overwritten where possible");

	overwriteMain = new BooleanCommand(false, "--overwrite-main")
	    .setName("Overwrite Main Files")
	    .setDescription("If enabled, main files that already exist will be overwritten where possible");

	
	
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

	//see that we can write to the given directories and read from the given directories
	//TODO: this
    }
    
    /* main */
    public static void main(String[] argv) {
	new MakeHelper().run(argv);
    }    
}
