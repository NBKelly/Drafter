package easy;

/* imports */
import easy.Drafter;
import easy.Command;
import easy.StringCommand;
import easy.Timer;

public class EasyDrafter extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */

    StringCommand className;
    StringCommand auxPackage;
    
    /* solve problem here */
    @Override public int solveProblem() {
	Timer t = makeTimer();

	//what is my workflow?
	//I want to take a classname/path, and a package name/path
	//given a classname (com.nbkelly.formerly.Chucks),
	//and an auxiliary packagename (com.nbkelly.aux),
	//then call:
	//  drafter.sh -n [classname] -p [packagename] -l [packagedirectory] -ap [aux-package-name]
	//

	String name = "";
	String packageName = "";
	String packageDir = "";
	if(className.getValue().contains(".")) {
	    String[] s = className.getValue().split("\\.");
	    name = s[s.length-1];

	    for(int i = 0; i < s.length - 1; i++) {
		if(i == 0) {
		    packageName += s[i];
		    packageDir  += s[i];
		}
		else {
		    packageName += "." + s[i];
		    packageDir  += "/" + s[i];
		}
	    }

	    DEBUG(1, "NAME = " + name);
	    DEBUG(1, "PACK = " + packageName);
	    DEBUG(1, " DIR = " + packageDir);
	}
	else {
	    ERR("A qualified class name is required");
	    FAIL(1);
	}

	String auxDirName = auxPackage.getValue().replaceAll("\\.", "/");
	String auxPackageName = auxPackage.getValue();

	if(auxPackage.getValue().length() < 1) {
	    ERR("A qualified auxiliary package name is required");
	    FAIL(1);
	}
	
	DEBUG(1, "AUXD = " + auxDirName);
	DEBUG(1, "AUXP = " + auxPackageName);

	DEBUG(1, t.split("Finished Processing"));

	//  drafter.sh -n [classname] -p [packagename] -l [packagedirectory] -ap [aux-package-name]
	printf("-n %s -p %s -l %s -ap %s -ad %s -d %d%n", name, packageName, packageDir, auxPackageName, auxDirName, GET_DEBUG_LEVEL());
	
	return 0;
    }

    
    /* set commands */
    @Override public Command[] setCommands() {
	_PAGE_OPTIONAL = false; //page does not show up as a user input command
	_PAGE_ENABLED = false;  //page is set to disabled by default

	className = new StringCommand(null, true, "-c", "--classname")
	    .setName("Fully Qualified Class Name")
	    .setDescription("A fully qualified classname in the form 'a.b.c.Name'");
	auxPackage= new StringCommand(null, true, "-a", "--auxiliary-package-name")
	    .setName("Fully Qualified Auxiliary Package Name")
	    .setDescription("A fully qualified package name for the auxiliary classes, in the form a.b.aux");
	
	return new Command[] { className, auxPackage };
    }
    
    /* act after commands processed */
    @Override public void actOnCommands() {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
    }
    
    public static void main(String[] argv) {
        new EasyDrafter().run(argv);
    }
}
