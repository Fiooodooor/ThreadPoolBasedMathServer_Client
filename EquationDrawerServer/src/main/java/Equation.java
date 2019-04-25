import java.util.LinkedList;

//-------------------------------------------------------------------------------------------------------------------
public class Equation {
    private String theEquationString;
    private LinkedList<Tokens> equationTokens;
    private int Errors;

//-------------------------------------------------------------------------------------------------------------------
    Equation(String theEquationString) {
        setErrors(0);
        setEquationString(theEquationString);
        setEquationTokens(new LinkedList<>());
    }
//-------------------------------------------------------------------------------------------------------------------
    LinkedList<Tokens> parseEquationString(boolean tuneUp) {
        char pt;
        for(int i=0, j; i<getEquationString().length(); i++) {
            pt=getEquationString().charAt(i);
            if(pt==' ' || pt==',') { continue; }
            if(Character.isDigit(pt)) {
                j = skipDigits(getEquationString(), i);
                if (j<getEquationString().length() && getEquationString().charAt(j) == '.') {
                    j = skipDigits(getEquationString(), j);
                } if(i!=j) {
                    addEquationToken(new Tokens(Double.parseDouble(getEquationString().substring(i, j))));
                    i = j - 1;
                }
            } else if(pt=='+' || pt=='-' || pt=='*' || pt=='/' || pt=='%' || pt=='^' || pt=='(' || pt==')' || pt=='x') {
                addEquationToken(new Tokens(pt));
            } else if(Character.isLetter(pt)) {
                j = skipLetters(getEquationString(), i);
                if(i!=j) {
                    addEquationToken(new Tokens(getEquationString().substring(i, j)));
                    i = j - 1;
                }
            }
        } if(tuneUp) {
            tuneUpEquationTokens();
        }
        return getTokenList();
    }
//-------------------------------------------------------------------------------------------------------------------
    private Integer skipLetters(String eq, Integer i) {
        for (int j = i + 1; j < eq.length(); j++) {
            if (!Character.isLetter(eq.charAt(j))) return j;
        } return eq.length();
    }

    private Integer skipDigits(String eq, Integer i) {
        for (int j = i+1; j < eq.length(); j++) {
            if (!Character.isDigit(eq.charAt(j))) return j;
        } return eq.length();
    }
//-------------------------------------------------------------------------------------------------------------------
    private void tuneUpEquationTokens() {
        if(getErrors()!=0) return;
        SymbolMapper.TOKEN_TYPE first, second;
        for(int i=1; i<getTokenList().size();i++)
        {
            first = getTokenList().get(i-1).getType();
            second = getTokenList().get(i).getType();
            if((first == SymbolMapper.TOKEN_TYPE.VALUE || first == SymbolMapper.TOKEN_TYPE.VARIABLE ||
                first == SymbolMapper.TOKEN_TYPE.CLOSER) && (second == SymbolMapper.TOKEN_TYPE.VALUE ||
               second == SymbolMapper.TOKEN_TYPE.VARIABLE || second == SymbolMapper.TOKEN_TYPE.OPENER ||
               second == SymbolMapper.TOKEN_TYPE.FUNCTION))
            {
                equationTokens.add(i, new Tokens('*'));
            }
        }
    }
//-------------------------------------------------------------------------------------------------------------------
    private String getEquationString() {
        return theEquationString;
    }
    private void setEquationString(String theEquationString) {
        this.theEquationString = theEquationString.toLowerCase();
    }
//-------------------------------------------------------------------------------------------------------------------
    private LinkedList<Tokens> getTokenList() {
        return equationTokens;
    }
    private void setEquationTokens(LinkedList<Tokens> equationTokens) {
        this.equationTokens = equationTokens;
    }
    private void addEquationToken(Tokens newToken) {
        if(newToken.getType() == SymbolMapper.TOKEN_TYPE.ERROR)
            setErrors(1);
        this.equationTokens.add(newToken);
    }
//-------------------------------------------------------------------------------------------------------------------
    void setErrors(int errorCode) {
        this.Errors = errorCode;
    }
    int getErrors() {
        return this.Errors;
    }

    @Override
    public String toString() {
        return "Equation{" + "equationTokens=" + equationTokens + '}';
    }

}
