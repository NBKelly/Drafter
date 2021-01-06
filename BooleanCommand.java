public class BooleanCommand extends Command {
    public boolean value;
    
    public BooleanCommand(boolean defaultValue, String... synonyms) {
	addSynonyms(synonyms).setMandatory(false);
	this.value = defaultValue;
    }

    public int match(String[] argv, int index) {
	String cmd = argv[index];
	if(matched == 0) { //don't match if already matched
	    if(synonyms.contains(cmd)) {
		matched++;
		value = !value;
		return index+1;	
	    }
	}

	return 0; //doesnt match
    }
    
    public boolean getValue() {
	return value;
    }
}
