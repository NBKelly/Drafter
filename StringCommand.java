public class StringCommand extends Command {
    public String value;
    
    public StringCommand(String defaultValue, boolean mandatory, String... synonyms) {
	addSynonyms(synonyms).setMandatory(mandatory);
	this.value = defaultValue;
	this.takesInput = true;
    }

    public int match(String[] argv, int index) {
	String cmd = argv[index];
	if(matched == 0) { //don't match if already matched
	    if(synonyms.contains(cmd)) {
		if(index + 1 < argv.length) {
		    matched++;
		    value = argv[index+1];
		    return index+2;
		}
		return -1; //matches but invalid
	    }
	}

	return 0; //doesnt match
    }
    
    public String getValue() {
	return value;
    }
}
