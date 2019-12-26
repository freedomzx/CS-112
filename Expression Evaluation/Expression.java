package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
	public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	//a-(b+A[B[2]])*d+3
    	//a         +       B         [      A 			 [       C       [       D       ]]]+     c+    xyz+ xyz[asdf+6] + (x + y)
    	char[] charExp = expr.toCharArray();
    	int counter = 0, lagger = 0;
    	while(counter < charExp.length){
        	String temp = "";
        	boolean noDupe = true;
        	if(counter == charExp.length-1) {
        		if(isLetter(charExp[counter])) {
        			temp += charExp[counter];
        			Variable varAdd = new Variable(temp);
					for(int j = 0; j < vars.size(); j++) {
						if(varAdd.equals(vars.get(j))) {
							noDupe = false;
							break;
						}
					}
					if(noDupe) vars.add(varAdd);
					break;
        		}
        		else {
        			break;
        		}
        	}
        	while(lagger < charExp.length && isLetter(charExp[lagger])) {
        		if(lagger + 1 > charExp.length) break;
        		else lagger++;
        	}
    		if(counter != lagger) {
    			for(int i = counter; i < lagger; i++) temp += charExp[i];
    			if(lagger < charExp.length && sOrT(charExp[lagger])) {
    				while(lagger < charExp.length && sOrT(charExp[lagger])) lagger++;
    				if(lagger < charExp.length && charExp[lagger] == '[') {//add arr
    					Array arrAdd = new Array(temp);
    					for(int j = 0; j < arrays.size(); j++) {
    						if(arrAdd.equals(arrays.get(j))) {
    							noDupe = false;
    							break;
    						}
    					}
    					if(noDupe) arrays.add(arrAdd);
    				}
    				else {//add var
    					Variable varAdd = new Variable(temp);
    					for(int j = 0; j < vars.size(); j++) {
    						if(varAdd.equals(vars.get(j))) {
    							noDupe = false;
    							break;
    						}
    					}
    					if(noDupe) vars.add(varAdd);
    				}
    			}
    			else if(lagger < charExp.length && charExp[lagger] == '[') {//add arr
    				Array arrAdd = new Array(temp);
    				for(int j = 0; j < arrays.size(); j++) {
						if(arrAdd.equals(arrays.get(j))) {
							noDupe = false;
							break;
						}
					}
					if(noDupe) arrays.add(arrAdd);
    			}
    			else {//add var
    				Variable varAdd = new Variable(temp);
    				for(int j = 0; j < vars.size(); j++) {
						if(varAdd.equals(vars.get(j))) {
							noDupe = false;
							break;
						}
					}
					if(noDupe) vars.add(varAdd);
    			}
    		}
    		lagger++;
    		counter = lagger;
    	}
    	//printing to see if the things actually filled up
    	for(Variable var: vars) System.out.println(var.toString());
    	for(Array array: arrays) System.out.println(array.toString());
}
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	//a    + (A[a])     +       B         [      A 			 [       C       [       D[3+6*(a+b)]       ]]]+     c+    xyz+ A[asdf+6] + (x + y) + (5) + w
    	//variables are a, c, xyz, asdf, x, y, w
    	//arrays are B, A, C, D
    	////a-(b+A[B[2]])*d+3
    	if(expr.equals("")) return 0; //empty expression
    	
    	
    	String exprNoS = ""; //DELETING ALL SPACES AND TABS
    	for(int i = 0; i < expr.length(); i++) {
    		if(!sOrT(expr.charAt(i))) exprNoS+= expr.charAt(i);
    	} //this for loop just deletes all the spaces and tabs out of this bullshit and puts it into exprNoS (expression no strings)
    	System.out.println(exprNoS);
    	
    	
    	/*String exprV = ""; //replacing every variable with a constant
    	StringTokenizer exprToken = new StringTokenizer(exprNoS, delims, true);
    	while(exprToken.hasMoreTokens()) {
    		boolean isVariable = false;
    		String token = exprToken.nextToken();
    		for(int i = 0; i < vars.size(); i++) {
    			if(token.equals(vars.get(i).name)) {
    				isVariable = true;
    				break;
    			}
    		}
    		if(isVariable) {
    			for(int i = 0; i < vars.size(); i++) {
    				if(token.equals(vars.get(i).name)) {
    					exprV = exprV + " " + vars.get(i).value;
    				}
    			}
    		}
    		else exprV = exprV + " " + token;
    	}
    	exprV = exprV.substring(1); //rmeoves space at the beginning
    	System.out.println(exprV);
    	
    	
    	String exprNoA = replaceArrays(exprV);//replaces arrays with true values
    	System.out.println(exprNoA);
    	
    	float weDidIt = evaluate(exprNoA); //evaluate.*/
    	return 0;
    }
    
    //helper methods
    /**
     * Returns true/false depending on whether or not an input character is a letter
     * @param c The input character
     * @return true/false depending on whether or not the thing is a letter
     */
    private static boolean isLetter(char c) {
    	if((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) return true;
    	return false;
    }
    
    /**
     * Returns true/false depending on whether or not an input character is a white space
     * @param c The input character
     * @return true/false if its a space ' ' or a tab '\t'
     */
    private static boolean sOrT(char c) {
    	if(c == '\t' || c == ' ') return true;
    	return false;
    }
    
    private static boolean hasNumber(String s) {
    	for(int i = 0; i < s.length(); i++) {
    		if(Character.isDigit(s.charAt(i))) return true;
    	}
    	return false;
    }
    private static boolean isOperator(String s) {
    	if(s.charAt(0) == '+' || s.charAt(0) == '-'|| s.charAt(0) == '*' ||s.charAt(0) == '/') return true;
    	return false;
    }
    /**
     * Evaluates an expression that is only numbers and operators, given in a string
     * @param s The input string to be evaluated
     * @return the resulting float
     */
    private static float evaluate(String s) {
    	Stack<Float> operands = new Stack<Float>();
    	Stack<Character> operators = new Stack<Character>();
    	StringTokenizer evaluation = new StringTokenizer(s, " ", false);
    	while(evaluation.countTokens() != 0) {
    		String token = evaluation.nextToken();
    		if(hasNumber(token)) {
    			float toPush = Float.parseFloat(token);
    			operands.push(toPush);
    		}
    		else if(token.charAt(0) == '(') operators.push(token.charAt(0));
    		else if(token.charAt(0) == ')') {
    			while(operators.peek() != '(') {
    				char currentOperator = operators.pop();
    				float secondOperand = operands.pop();
    				float firstOperand = operands.pop();
    				float resultOfOp = 0;
    				switch(currentOperator) {
    				case '+': resultOfOp = firstOperand + secondOperand;
    				break;
    				case '-': resultOfOp = firstOperand - secondOperand;
    				break;
    				case '*': resultOfOp = firstOperand * secondOperand;
    				break;
    				case '/': resultOfOp = firstOperand / secondOperand;
    				break;
    				}
    				operands.push(resultOfOp);
    			}
    			//operators.pop();//get rid of that parantheses
    		}
    		else {
    			while(operators.size() != 0) {
    				if(token.charAt(0) == '+' || token.charAt(0) == '-') {
    					if(operators.peek() == '*' || operators.peek() == '/') break; //higher precedence in the stack, can't do
    				}
    				char currentOperator = operators.pop();
    				float secondOperand = operands.pop();
    				float firstOperand = operands.pop();
    				float resultOfOp = 0;
    				switch(currentOperator) {
    				case '+': resultOfOp = firstOperand + secondOperand;
    				break;
    				case '-': resultOfOp = firstOperand - secondOperand;
    				break;
    				case '*': resultOfOp = firstOperand * secondOperand;
    				break;
    				case '/': resultOfOp = firstOperand / secondOperand;
    				break;
    				}
    				operands.push(resultOfOp);
    			}
    			operators.push(token.charAt(0)); //pushes current operator into this thing
    		}
    	}
    	while(operators.size() != 0) {
    		char currentOperator = operators.pop();
			float secondOperand = operands.pop();
			float firstOperand = operands.pop();
			float resultOfOp = 0;
			switch(currentOperator) {
			case '+': resultOfOp = firstOperand + secondOperand;
			break;
			case '-': resultOfOp = firstOperand - secondOperand;
			break;
			case '*': resultOfOp = firstOperand * secondOperand;
			break;
			case '/': resultOfOp = firstOperand / secondOperand;
			break;
			}
			operands.push(resultOfOp);
    	}
    	return operands.pop();
    }
    
    
    private static String replaceArrays(String s) {
    	String toReturn = s;
    	while(toReturn.indexOf(']') != -1 || toReturn.indexOf('[') != -1) {
    		int lastIndex = toReturn.indexOf(']');
    		int firstIndex = toReturn.substring(0, lastIndex).lastIndexOf('[');
    		String subset = toReturn.substring(firstIndex+1, lastIndex);
    	}
    	return toReturn;
    }
}
