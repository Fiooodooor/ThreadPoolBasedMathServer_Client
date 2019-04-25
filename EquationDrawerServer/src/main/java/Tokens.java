
//-------------------------------------------------------------------------------------------------------------------
public class Tokens extends SymbolMapper {

    private TOKEN_TYPE type;
    private Object data;

//-------------------------------------------------------------------------------------------------------------------
    public Tokens(String theToken) {
        this.setData(assignFunction(theToken));
        this.setType(assignType(getFunctionData()));
        if(this.getType()==TOKEN_TYPE.VALUE) {
            double tmp = this.getFunctionData().tokenCalculate();
            this.setData(tmp);
        }
    }
//-------------------------------------------------------------------------------------------------------------------
    public Tokens(char theToken) {
        this.setData(assignSymbol(theToken));
        this.setType(assignType(getSymbolData()));
    }
//-------------------------------------------------------------------------------------------------------------------
    public Tokens(double theToken) {
        this.setType(TOKEN_TYPE.VALUE);
        this.setData(theToken);
    }
//-------------------------------------------------------------------------------------------------------------------
    public Object getData() {
        return data;
    }
    public TOKEN_SYMBOL getSymbolData() {
        return (TOKEN_SYMBOL)data;
    }
    public TOKEN_FUNCTION getFunctionData() {
        return (TOKEN_FUNCTION)data;
    }
//-------------------------------------------------------------------------------------------------------------------
    public TOKEN_TYPE getType() {
        return type;
    }
    private void setType(TOKEN_TYPE type) {
        this.type = type;
    }
    private void setData(Object data) {
        this.data = data;
    }
//-------------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
             if(getType() == TOKEN_TYPE.VALUE) { return "T{value=" + getData() + '}'; }
        else if(getType() == TOKEN_TYPE.VARIABLE) { return "T{variable=" + getType().name() + '}'; }
        else if(getType() == TOKEN_TYPE.OPERATOR) { return "T{operand=" + getSymbolData().name() + '}'; }
        else if(getType() == TOKEN_TYPE.FUNCTION) { return "T{function=" + getFunctionData().name() + '}'; }
        else return "T{other=" + getType() + '}';
    }
//-------------------------------------------------------------------------------------------------------------------

}


