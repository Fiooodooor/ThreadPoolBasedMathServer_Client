import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class PostfixEquation extends Equation {
    private LinkedList<Tokens> equationONP;

    public PostfixEquation(String theEquation) {
        super(theEquation);
        setONP( shuntingYardDijkstra( parseEquationString(true) ) );
    }

    public double calculateValue(double x) {
        if(getErrors()!=0) return 0;
        if (equationONP==null || equationONP.size() == 0) return 0;
        int i;
        double a, b;
        Tokens pt;
        Stack<Double> theStack;
        theStack = new Stack<>();
        try {
            for (i = 0; i < equationONP.size(); i++) {
                pt = equationONP.get(i);
                if (pt.getType() == SymbolMapper.TOKEN_TYPE.VALUE) {
                    theStack.push((double)pt.getData());
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.VARIABLE) {
                    theStack.push(x);
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.OPERATOR) {
                    if (theStack.size() >= 2) {
                        b = theStack.pop();
                        a = theStack.pop();
                        theStack.push(pt.getSymbolData().tokenCalculate(a, b));
                    }
                    else {
                        throw new IndexOutOfBoundsException("The stack is empty! ONP calculate error!");
                    }
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.FUNCTION) {
                    switch(pt.getFunctionData().getVariablesNr()) // get number of needed params
                    {
                        case 1:
                            theStack.push(pt.getFunctionData().tokenCalculate(theStack.pop()));
                            break;
                        default:    // 2 params intended
                            b = theStack.pop();
                            if(!theStack.isEmpty()) {
                                a = theStack.pop();
                            } else {
                                throw new IndexOutOfBoundsException("The stack is empty! ONP calculate error!");
                            }
                            theStack.push(pt.getFunctionData().tokenCalculate(a, b));
                    }
                }
            }   // END FOR
            if (theStack.isEmpty()) {
                throw new IndexOutOfBoundsException("The stack is empty! ONP calculate error!");
            } else {
                return theStack.pop();
            }
        } catch (IndexOutOfBoundsException e) {
            setErrors(3);
            return 0;
        }
    }

    private LinkedList<Tokens> shuntingYardDijkstra(LinkedList<Tokens> tokenizedExpression)
    {
        if(getErrors()!=0) return null;
        List<Tokens> theStack = new ArrayList<>();
        LinkedList<Tokens> theOnpEquation = new LinkedList<>();
        Tokens pt, tp;
        try {
            for (int i = 0; i < tokenizedExpression.size(); i++) {
                pt = tokenizedExpression.get(i);
                if (pt.getType() == SymbolMapper.TOKEN_TYPE.VALUE) {
                    theOnpEquation.add(pt);
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.VARIABLE) {
                    theOnpEquation.add(pt);
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.FUNCTION) {
                    theStack.add(pt);
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.OPERATOR) {
                    if (!theStack.isEmpty()) {
                        tp = theStack.get(theStack.size() - 1);
                        while ((tp.getType() == SymbolMapper.TOKEN_TYPE.FUNCTION ||
                                (tp.getType() == SymbolMapper.TOKEN_TYPE.OPERATOR &&
                                        ((pt.getSymbolData().tokenPower() < tp.getSymbolData().tokenPower()) ||
                                         (pt.getSymbolData().tokenPower() == tp.getSymbolData().tokenPower() && tp.getSymbolData().isLeftAssoc()))))
                                && tp.getType() != SymbolMapper.TOKEN_TYPE.OPENER) {
                            theOnpEquation.add(theStack.remove(theStack.size() - 1));
                            if (!theStack.isEmpty()) tp = theStack.get(theStack.size() - 1);
                            else break;
                        }
                    }
                    theStack.add(pt);
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.OPENER) {
                    theStack.add(pt);
                } else if (pt.getType() == SymbolMapper.TOKEN_TYPE.CLOSER) {
                    while (!theStack.isEmpty() && theStack.get(theStack.size() - 1).getType() != SymbolMapper.TOKEN_TYPE.OPENER) {
                        theOnpEquation.add(theStack.remove(theStack.size() - 1));
                    }
                    if (!theStack.isEmpty()) theStack.remove(theStack.size() - 1);
                    else throw new IndexOutOfBoundsException("ERROR: Couldn't find the close bracket");
                }
            }
            while (!theStack.isEmpty()) {
                if (theStack.get(theStack.size() - 1).getType() != SymbolMapper.TOKEN_TYPE.OPENER) {
                    theOnpEquation.add(theStack.remove(theStack.size() - 1));
                } else throw new IndexOutOfBoundsException("ERROR: Couldn't find the close bracket");
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error in shuntingYard algorithm");
            setErrors(2);
            return null;
        }
        return theOnpEquation;
    }

    public LinkedList<Tokens> getONP() {
        return equationONP;
    }
    private void setONP(LinkedList<Tokens> equationONP) {
        this.equationONP = equationONP;
    }
}

