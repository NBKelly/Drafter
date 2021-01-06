import java.util.TreeSet;

public abstract class Command {
    public TreeSet<String> synonyms = new TreeSet<String>();
    public int count = 0;
    public boolean mandatory = false;
    public int matched = 0;
    private String name = "";
    private String description = "";
    protected boolean takesInput = false;
    //by default this is empty
    public Command addSynonyms(String... args) {
	for(int i = 0; i < args.length; i++)
	    synonyms.add(args[i]);

	return this;
    }

    //by default this is false
    public Command setMandatory(boolean mandatory) {
	this.mandatory = mandatory;

	return this;
    }

    public abstract int match(String[] argv, int index);

    //public abstract String us
    public String usage() {
	//return: name : list of synonyms - mandatory - description
	String res = name;
	res += " : { ";

	for(String s : synonyms)
	    res += s + " ";

	if(takesInput)
	    res += "} [input]";
	else
	    res += "}";

	res += "\n    | Mandatory: " + mandatory;
	res += "\n    | Input:  " + takesInput;
	res += "\n    | > " + description;

	return res;
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T setName(String name) {
	this.name = name;
	return (T)this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T setDescription(String desc) {
	this.description = wrapString(desc, "\n", 80).replaceAll("\\n", "\n    | > ");
	return (T)this;
    }

    private static String wrapString(String s, String deliminator, int length) {
	String result = "";
	int lastdelimPos = 0;
	for (String token : s.split(" ", -1)) {
	    if (result.length() - lastdelimPos + token.length() > length) {
		result = result + deliminator + token;
		lastdelimPos = result.length() + 1;
	    }
	    else {
		result += (result.isEmpty() ? "" : " ") + token;
	    }
	}
	return result;
    }
}
