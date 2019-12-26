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
    	// 29 / (25) - 83 + a    + (A[a])   +A[A[1]]  +       B         [      A 			 [       C       [       asdfg[3+6*(a+b)]       ]]] 
    	//c+    xyz+ A[asdf+6] + (x + y) + (5) + w + (((2 * 73) + 97) * (30 / 52 * 50 + 13 + ((65 + 97 - 83) / 68) + 39) + 5)
    	//a-(b+A[B[2]])*d+3
    	if(expr.equals("") | expr.contentEquals(" ")) return 0; //empty expression
    	String exprNoS = removeSpaces(expr);
    	String exprV = addSpaces(exprNoS);
    	String exprNoA = replaceArrays(exprV, vars, arrays);
    	return evaluate(exprNoA, vars);
    }
    
    //helper methods
    private static boolean isLetter(char c) {
    	if((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) return true;
    	return false;
    }
    
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
    
    private static String addSpaces(String s) {
    	String toReturn = "";
    	StringTokenizer exprTokens = new StringTokenizer(s, delims, true);
    	while(exprTokens.hasMoreTokens()) {
    		String token = exprTokens.nextToken();
    		toReturn = toReturn + " " + token;
    	}
    	return toReturn.substring(1);
    }
    
    private static String removeSpaces(String s) {
    	String toReturn = "";
    	for(int i = 0; i < s.length(); i++) {
    		if(!sOrT(s.charAt(i))) toReturn += s.charAt(i);
    	}
    	return toReturn;
    }
    private static int checkPrecedence(char c) {
    	if(c == '*' || c == '/') return 3;
    	else if(c == '+' || c == '-') return 2;
    	return 1;
    }
    
    private static boolean isOperator(char c) {
    	if(c == '+' || c == '-' || c == '*' || c == '/') return true;
    	return false;
    }
    
    private static float evaluate(String s,ArrayList<Variable> vars) {
    	Stack<Float> operands = new Stack<Float>();
    	Stack<Character> operators = new Stack<Character>();
    	StringTokenizer evaluation = new StringTokenizer(s, " ", false);
    	while(evaluation.hasMoreTokens()) {
    		String token = evaluation.nextToken();
    		if(hasNumber(token)) {
    			operands.push(Float.parseFloat(token));
    		}
    		else if(isLetter(token.charAt(0))) {
    			float toPush = 0;
    			for(int i = 0; i < vars.size(); i++) {
    				if(token.equals(vars.get(i).name)) {
    					toPush = vars.get(i).value;
    					break;
    				}
    			}
    			operands.push(toPush);
    		}
    		else if(isOperator(token.charAt(0)) && operators.size() == 0) operators.push(token.charAt(0));
    		else if(isOperator(token.charAt(0)) && operators.size() != 0 && checkPrecedence(token.charAt(0)) > checkPrecedence(operators.peek())) operators.push(token.charAt(0));
    		else if(token.charAt(0) == '(') operators.push(token.charAt(0));
    		else if(token.charAt(0) == ')') {
    			while(operators.peek() != '(') {
    				char currentOperator = operators.pop();
    				float secondOperand = operands.pop();
    				float firstOperand = operands.pop();
    				float toPush = 0;
    				switch(currentOperator) {
    				case '+': toPush = firstOperand + secondOperand;
    					break;
    				case '-': toPush = firstOperand - secondOperand;
    					break;
    				case '*': toPush = firstOperand * secondOperand;
    					break;
    				case '/': toPush = firstOperand / secondOperand;
    				}
    				operands.push(toPush);
    			}
    			operators.pop();
    		}
    		else if(isOperator(token.charAt(0)) && operators.size() != 0 && checkPrecedence(token.charAt(0)) < checkPrecedence(operators.peek())) {
    			while(operators.size() != 0 && operators.peek() != '(') {
    	    		char currentOperator = operators.pop();
    				float secondOperand = operands.pop();
    				float firstOperand = operands.pop();
    				float toPush = 0;
    				switch(currentOperator) {
    				case '+': toPush = firstOperand + secondOperand;
    					break;
    				case '-': toPush = firstOperand - secondOperand;
    					break;
    				case '*': toPush = firstOperand * secondOperand;
    					break;
    				case '/': toPush = firstOperand / secondOperand;
    				}
    				operands.push(toPush);
    	    	}
    			operators.push(token.charAt(0));
    		}
    		else {
    			char currentOperator = operators.pop();
				float secondOperand = operands.pop();
				float firstOperand = operands.pop();
				float toPush = 0;
				switch(currentOperator) {
				case '+': toPush = firstOperand + secondOperand;
					break;
				case '-': toPush = firstOperand - secondOperand;
					break;
				case '*': toPush = firstOperand * secondOperand;
					break;
				case '/': toPush = firstOperand / secondOperand;
					break;
				}
				operands.push(toPush);
				operators.push(token.charAt(0));
    		}
    	}
    	while(operators.size() != 0) {
    		char currentOperator = operators.pop();
			float secondOperand = operands.pop();
			float firstOperand = operands.pop();
			float toPush = 0;
			switch(currentOperator) {
			case '+': toPush = firstOperand + secondOperand;
				break;
			case '-': toPush = firstOperand - secondOperand;
				break;
			case '*': toPush = firstOperand * secondOperand;
				break;
			case '/': toPush = firstOperand / secondOperand;
			}
			operands.push(toPush);
    	}
    	return operands.pop();
    }
    
    
    private static String replaceArrays(String s, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        String toReturn = removeSpaces(s);
        while(toReturn.indexOf(']') != -1) {
            String arrayName = "";
            int lastIndex = toReturn.indexOf(']');
            int firstBracket = toReturn.substring(0, lastIndex).lastIndexOf('[');
            int startOfArray = firstBracket-1;
            if(startOfArray == 0) {
                startOfArray = 0;
                arrayName = toReturn.charAt(0) + "";
            }
            else {
                while(startOfArray >= 0 && isLetter(toReturn.charAt(startOfArray))) {
                    startOfArray--;
                }
                if(startOfArray == -1) {
                	startOfArray++;
                	arrayName = toReturn.substring(startOfArray, firstBracket);
                }
                else arrayName = toReturn.substring(startOfArray+1, firstBracket);
            }
            
            
            String subset = toReturn.substring(firstBracket+1, lastIndex);
            int truncated = (int) evaluate(addSpaces(subset), vars);
            float valueOfArray = 0;
            for(int i = 0; i < arrays.size(); i++) {
                if(arrayName.equals(arrays.get(i).name)) valueOfArray = arrays.get(i).values[truncated];
            }
            String theArray = toReturn.substring(toReturn.substring(0, lastIndex).lastIndexOf(arrayName), lastIndex+1);
            toReturn = toReturn.replace(theArray, valueOfArray + "");
        }
        return addSpaces(toReturn);
    }
}
