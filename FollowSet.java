package rebuilt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Class implements production of follow set of every nonterminal symbol on the
 * left side of a given grammar. The grammar looks like the following format: S
 * -> A S b S -> C A -> a C -> c C C -> lambda
 */
public class FollowSet {
	private static Set<String> T, N;
	private List<Rule> production;

	public FollowSet(String file) throws FileNotFoundException {
		T = new HashSet<>(); // set of terminal symbols
		N = new HashSet<>(); // set of nonterminal symbols
		production = new ArrayList<>(); // list of every rule of the grammar

		Scanner sc = new Scanner(new FileReader(file));

		while (sc.hasNext()) {
			String line = sc.nextLine();
			int index = line.indexOf("->"); // get the nonterminal symbol of the rule
			String tok = line.substring(0, index).trim();

			if (isNonTerminal(tok))
				N.add(tok); // adds nonterminal symbol to N set

			String str = line.substring(index + 2).trim(); // get the definition of the rule
			StringTokenizer stk = new StringTokenizer(str, " ", false);
			while (stk.hasMoreTokens()) {
				String te = stk.nextToken();
				if (isNonTerminal(te))
					N.add(te); // adds nonterminal symbol to N set
				else
					T.add(te); // adds terminal symbol to T set
			}

			Rule rule = new Rule(tok, str);
			production.add(rule); // adds rule to production
		}

		sc.close(); // close scanner
	}

	/*
	 * Method to find first set of a nonterminal and terminal symbol Returns a set
	 * of first set
	 */
	private Set<String> firstSet(String tok) {
		Set<String> ret = new HashSet<>();

		for (Rule rl : production) {
			if (rl.getToken().equals(tok)) { // first set of a nonterminal symbol
				List<String> tempList = rl.getDefinition();

				if (tempList.get(0).equals("lambda"))
					ret.add("lambda"); // adds terminal symbols to first set

				else if (T.contains(tempList.get(0)))
					ret.add(tempList.get(0)); // adds terminal symbols to first set

				else if (N.contains(tempList.get(0)) && !rl.getToken().equals(tempList.get(0))) {
					Set<String> temp = firstSet(tempList.get(0));

					if (tempList.size() > 1) {
						int last = tempList.size() - 1;
						boolean stop = false;
						if (temp.contains("lambda")) {
							remove(temp, "lambda");
							for (int i = 1; i < tempList.size() && !stop; i++) {
								temp.addAll(firstSet(tempList.get(i)));

								if (!temp.contains("lambda"))
									stop = true;

								if (temp.contains("lambda") && i != last) {
									remove(temp, "lambda");
								}
							}
						}
					}

					ret.addAll(temp); // adds terminal symbols to first set
				}
			}

			else if (T.contains(tok)) { // first set of a terminal symbol
				ret.add(tok);
			}
		}

		return ret;
	}

	/*
	 * Prints out the first set for every nonterminal symbol in the grammar
	 */
	public void listFirstSet() {
		List<String> used = new ArrayList<>(); // used nonterminal symbol
		System.out.println("First Sets");
		System.out.println("=========");
		for (Rule rl : production) {
			// prints first set of a nonterminal symbol only once
			if (!used.contains(rl.getToken())) {
				Set<String> f = firstSet(rl.getToken());
				Iterator<String> itr = f.iterator();
				System.out.print(rl.getToken() + ": {");
				while (itr.hasNext()) {
					System.out.print(itr.next() + " ");
				}
				System.out.print("}\n");

				// marks a nonterminal symbol as used after printing its first set
				used.add(rl.getToken());
			}
		}
	}

	/*
	 * Method to find follow set of a nonterminal symbol
	 * Returns the follow set
	 */
	private Set<String> followSet(String tok) {
		Set<String> ret = new HashSet<>();
		String start = production.get(0).getToken();

		if (tok.equals(start))
			ret.add("$"); //adds $ to the starting symbol

		for (Rule rl : production) {
			List<String> tempList = rl.getDefinition(); //get the definition
			int last = tempList.size() - 1; //needs to know the index of the last symbol of the definition

			if (tok.equals(tempList.get(last)) && !rl.getToken().equals(tok))
				ret.addAll(followSet(rl.getToken()));

			else if (tempList.size() > 1) {
				boolean stop = false;
				int ind = tempList.indexOf(tok); //finds symbol in the definition
				if (ind >= 0) {
					for (int i = ind + 1; i < tempList.size() && !stop; i++) {
						Set<String> temp = firstSet(tempList.get(i));
						ret.addAll(temp);
						
						//stops search if firstSet of the following symbol doesn't contain lambda
						if (!temp.contains("lambda"))
							stop = true; 
						//if the last symbol's first set contains lambda,
						//then includes followSet(Rule's left side token) into followSet(tok)
						else if (temp.contains("lambda") && i == last)
							ret.addAll(followSet(rl.getToken()));
					}
				}
			}
		}

		remove(ret, "lambda"); //exclude lambda from follow set

		return ret;
	}

	// exclude lambda from first set
	private void remove(Set<String> set, String tok) {
		Iterator<String> itr = set.iterator();
		boolean search = true;
		while (itr.hasNext() && search) {
			if (itr.next().equals(tok)) {
				itr.remove();
				search = false;
			}
		}
	}

	/*
	 * Prints out the first set for every nonterminal symbol in the grammar
	 */
	public void listfollowSet() {
		List<String> used = new ArrayList<>(); //used nonterminal symbol
		System.out.println("Follow Sets");
		System.out.println("=========");
		for (Rule rl : production) {
			//prints first set of a nonterminal symbol only once
			if (!used.contains(rl.getToken())) {
				Set<String> f = followSet(rl.getToken());
				Iterator<String> itr = f.iterator();
				System.out.print(rl.getToken() + ": {");
				while (itr.hasNext()) {
					System.out.print(itr.next() + " ");
				}
				System.out.print("}\n");
				
				//marks a nonterminal symbol as used after printing its follow set
				used.add(rl.getToken());
			}
		}
	}
	
	//prints the grammar
	public void printProduction() {
		System.out.println("Production");
		System.out.println("===========");
		for (Rule rl : production)
			System.out.println(rl.getToken() + "-->" + rl.getDefinition());
	}
	//prints sets of nonterminal and terminal symbols
	public void printSets() {
		Iterator<String> itr = N.iterator();
		System.out.print("Non-Terminal Set: {");
		while (itr.hasNext()) {
			System.out.print(itr.next() + " ");
		}
		System.out.print("}\n");

		itr = T.iterator();
		System.out.print("Terminal Set: {");
		while (itr.hasNext()) {
			System.out.print(itr.next() + " ");
		}
		System.out.print("}\n");
	}

	//Returns true if token is a nonterminal symbol
	private boolean isNonTerminal(String str) {
		char[] charArr = str.toCharArray();
		boolean ret = true;

		for (int i = 0; i < charArr.length && ret; i++) {
			if (!Character.isLetter(charArr[i]) || !Character.isUpperCase(charArr[i]))
				ret = false; // is terminal
		}

		return ret;
	}

	public static void main(String[] args) throws FileNotFoundException {
		FollowSet first = new FollowSet("G6.txt");
		first.printProduction();
		System.out.println();
		first.printSets();

		System.out.println("\n");
		first.listfollowSet();
	}
}
