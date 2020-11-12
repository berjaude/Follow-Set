package rebuilt;

import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * Class implements a rule of a context-free grammar,
 * represented by a nonterminal on the left side of the rule
 * and the definition on the right side 
 */
public class Rule {
	private String token; //non terminal
	private ArrayList<String> definition; //definition
	
	public Rule() {	} //implicit constructor
	
	public Rule(String tok, String def) { //explicit constructor
		token = tok;
		definition = new ArrayList<String>();
		StringTokenizer str = new StringTokenizer(def, " ", false);
		while (str.hasMoreTokens()) {
			definition.add(str.nextToken());
		}
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ArrayList<String> getDefinition() {
		return definition;
	}

	public void setDefinition(ArrayList<String> definition) {
		this.definition = definition;
	}
}
