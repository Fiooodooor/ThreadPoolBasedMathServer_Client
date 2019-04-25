import static java.lang.Math.pow;

/**
 * Klasa definiujaca wszystkie obsługiwane przez program symbole.
 * Między innymi rodzaj symbolu, operatory arytmetyczne oraz funkcje matematyczne.
 * Klasa SymbolMapper posiada również podstawowe funkcje parsujące
 * i zwracające odpowiedni typ danych.
 *
 * @author Milosz Linkiewicz
 * @version 0.8
 */
class SymbolMapper {
//-------------------------------------------------------------------------------------------------------------------
    /**
     * Publiczny typ enum z predefiniowanymi warosciami. Okresla rodzaj symoblu.
     * Odpowiednio mamy: wartosc liczbowa, zmienna, operator matematyczny funkcja
     * nawias otwierajacy, nawias zamykajacy, typ error wskazujacy na powstaly blad
     */
    public enum TOKEN_TYPE {
        VALUE(0),
        VARIABLE(1),
        OPERATOR(2),
        FUNCTION(3),
        OPENER(4),
        CLOSER(5),
        ERROR(-1);

        private int tokenType;
        TOKEN_TYPE(int value) {
            this.tokenType = value;
        }
    }
//-------------------------------------------------------------------------------------------------------------------
    /**
     * Publiczny typ enum wykorzystany do zdefiniowania wlasnego zbioru operatorow
     * z gory okreslonymi wlasnosciami. Odpowienio: znak reprezentujacy symbol,
     * moc rozumiana jako pierszenstwo wykonania operacji, czy symbol jest lewo-złączny.
     */
    public enum TOKEN_SYMBOL {
        VARIABLE('x',0,false),
        PLUS('+',0,false),
        MINUS('-',0,false),
        MULTIPLY('*',1,false),
        DIVISON('/',1,false),
        MODULO('%',2,false),
        POWER('^',2,true),
        OPENER('(',3,false),
        CLOSER(')',3,false),
        ERROR('#',-1,false);

        private char symbolToken;
        private int symboPower;
        private boolean symbolLeftJoint;

        TOKEN_SYMBOL(char value, int power, boolean isLeftJoint) {
            this.symbolToken = value;
            this.symboPower = power;
            this.symbolLeftJoint = isLeftJoint;
        }
        public int tokenPower() {
            return this.symboPower;
        }
        public boolean isLeftAssoc() {
            return this.symbolLeftJoint;
        }

        /**
         * Metoda zwracajaca wynik dzialania operatora wywołującego
         * na przekazane parametry a i b. Tak dla dodawania bedzie jako
         * wynik otrzymamy a+b.
         *
         * @param a to liczba stojaca po lewej stronie operatora
         * @param b to liczba stojaca po prawej stronie operatora
         * @return rezultat zadzialania a 'operator' b, np a-b.
         */
        protected double tokenCalculate(double a, double b) {
            switch(this.symbolToken) {
                case '+': return a + b;
                case '-': return a - b;
                case '*': return a * b;
                case '/': return a / b;
                case '%': return a % b;
                case '^': return pow(a, b);
            } return 0;     // should not get here. exception add
        }
    }
//-------------------------------------------------------------------------------------------------------------------
    /**
     * Ostatni z typów wyliczeniowych predefiniowanych na potrzeby parsera.
     * Odpowiedzialny jest za mapowanie funkcji oraz definiowanie ilo argumentowa
     * dan a funkcja jest, tj. ile zmiennych potrzebuje aby wyznaczyc wynik operacji
     * ktora to realizuje. I tak mamy odpowiednio: token jako String reprezentujacy
     * dana funkcje w niesparsowanym rownaniu, variables czyli ilosc wymaganych argumentow
     * oraz uid czyli unikalny identyfikator funkcji dla przyspieszenia porownan.
     * W przypadku variables = 0 mamy do czynienia tak na prawde z wartoscia jakiejsc stalej
     * i w takim przypadku token zwracany jest nie jako funkcja lecz jako zmiennoprzecinkowa wartosc.
     *
     */
    public enum TOKEN_FUNCTION {
        PI("pi", 0, 0),
        E("e", 0, 1),
        SIN("sin", 1, 2),
        COS("cos", 1, 3),
        TG("tg", 1, 4),
        CTG("ctg", 1, 5),
        ARCSIN("arcsin", 1, 6),
        ARCCOS("arccos", 1, 7),
        ARCTG("arctg", 1, 8),
        ATCCTG("arcctg", 1, 9),
        TO_RAD("torad", 1, 10),
        FROM_RAD("fromrad", 1, 11),
        LN("ln", 1, 12),
        POW("pow", 2, 13),
        SQRT("sqrt", 2, 14),
        LOG("log", 2, 15),
        ERROR("error", -1, 16);

        private String functionToken;
        private int functionVarNumber;
        private int functionId;

        TOKEN_FUNCTION(String token, int variables, int uid) {
            this.functionToken = token;
            this.functionVarNumber = variables;
            this.functionId = uid;
        }
    public int getVariablesNr() {
            return functionVarNumber;
    }

        /**
         * Przeladowana metoda zwracajaca wartosc dzialania wywolujacej funkcji na przkazane jako parametr
         * zmienne. W przypadku stalych zwraca ich wartosc z mozliwie najwieksza dostepna dokladnoscia.
         *
         * @return wartosc stalej wywolujacej metode.
         */
    protected double tokenCalculate() {
        if(this.functionId==0) {
            return Math.PI;
        }  return Math.E; }

        /**
         * Przeladowanie metody obliczajacej wynik dzialania funkcji wywolujacej
         *
         * @param a - wartosc liczby na ktorej wykonana powinna byc funkcja
         * @return wynik dzialania funkcji na wskazanej zmiennej, np. return sin(a);
         */
    protected double tokenCalculate(double a) {
        switch(this.functionId) {
            case 2: return Math.sin(a);
            case 3: return Math.cos(a);
            case 4: return Math.tan(a);
            case 5: if(Math.tan(a)==0) { return Double.MAX_VALUE; }
                                  else { return 1/Math.tan(a); }
            case 6: return Math.asin(a);
            case 7: return Math.acos(a);
            case 8: return Math.atan(a);//
            case 9: return (Math.PI/2) - Math.atan(a);
            case 10: return Math.toRadians(a);
            case 11: return Math.toDegrees(a);
            case 12: return Math.log(a);
        } return 0;
    }
        /**
         * Przeladowanie metody obliczajacej wynik dzialania funkcji wywolujacej
         *
         * @param a - wartosc pierwszej liczby na ktorej wykonana powinna byc funkcja
         * @param b - wartosc drugiej liczby na ktorej wykonana powinna byc funkcja
         * @return wynik dzialania funkcji na wskazanych zmiennych, np. return pow(2, 5) --> return 32
         */
    protected double tokenCalculate(double a, double b) {
        switch(this.functionId) {
            case 13: return pow(a, b);
            case 14: if(b==0) { return pow(a, Double.MAX_VALUE); }
                        else  { return pow(a, 1/b); }
            case 15: if(Math.log(a)==0) { return Double.MAX_VALUE; }
                                   else { return Math.log(b)/Math.log(a); }
            default: return 0; }
        }
    }

//-------------------------------------------------------------------------------------------------------------------
    /**
     * Metoda parsujaca pojedyncy znak i zwracajaca na jego podstawie odpowiedni typ operatora
     * @param theToken pojedynczy znak ktory chcemy sprobowac parsowac na symbol typu operator
     * @return zwraca odpowiednio dobrany symbol reprezentujacy przekazany znak
     */
    TOKEN_SYMBOL assignSymbol(char theToken) {
        for(TOKEN_SYMBOL S : TOKEN_SYMBOL.values()) {
            if(S .symbolToken == theToken) {
                return S;
            }
        } return TOKEN_SYMBOL.ERROR;
    }
//-------------------------------------------------------------------------------------------------------------------
    /**
     * Metoda parsujaca wiecej nic jeden znak, tj String. Na jego podstawie zwracajaca odpowiedni typ
     * symbolu, w tym przypadku symbol typu funkcja. Lub jezeli nie powiodlo sie parsowanie zwraca symbol
     * typu error.
     *
     * @param theToken ciag znakow ktory chcemy sprobowac sparsowac i zamienic na symbol
     * @return zwraca odpowiednia funkcje jezeli parsowanie sie powiodlo lub typ error gdy symbol nie zostal odnaleziony
     */
    TOKEN_FUNCTION assignFunction(String theToken) {
        for(TOKEN_FUNCTION S : TOKEN_FUNCTION.values()) {
            if(S.functionVarNumber!=-1 && S.functionToken.compareTo(theToken) == 0) {
                return S;
            }
        } return TOKEN_FUNCTION.ERROR;
    }
//-------------------------------------------------------------------------------------------------------------------
    /**
     * Metoda pomocnicza, uzywana do przekazania wartosci stalej jezeli takowa zostala sparsowana lub
     * w przeciwnym przypadku definiuje typ symoblu zgodnie z wynikiem parsowania jako funkcje.
     *
     * @param theFunction przekazany symbol typu funkcja jako parametr
     * @return zwracamy typ symolujako wartosc jezeli wywolano metode na funkcji typu stala
     * lub zwracamy typ sumbolu funkcja w przeciwnym wypadku.
     */
    TOKEN_TYPE assignType(TOKEN_FUNCTION theFunction) {
        if(theFunction.functionVarNumber == 0) {
            if(theFunction==TOKEN_FUNCTION.PI || theFunction==TOKEN_FUNCTION.E) {
                return TOKEN_TYPE.VALUE;
            }
        }
        return TOKEN_TYPE.FUNCTION;
    }
//-------------------------------------------------------------------------------------------------------------------
    /**
     * Metoda pomocnicza, uzywana do okreslenia rodzaju symoblu jaki zostal sparsowany na podstawie
     * przekazanego znaku. Tj. okresla typ operatora.
     *
     * @param theSymbol sparsowany uprzednio symbol
     * @return zwraca rodzaj sparsowanego symbolu. Tj okresla czy jest to operator, zmienna, nawias czy tez nieznany symbol
     */
    TOKEN_TYPE assignType(TOKEN_SYMBOL theSymbol) {
        if(theSymbol==TOKEN_SYMBOL.VARIABLE) {
            return TOKEN_TYPE.VARIABLE;
        } else if(theSymbol==TOKEN_SYMBOL.OPENER) {
            return TOKEN_TYPE.OPENER;
        } else if(theSymbol==TOKEN_SYMBOL.CLOSER) {
            return TOKEN_TYPE.CLOSER;
        } else if(theSymbol==TOKEN_SYMBOL.ERROR) {
            return TOKEN_TYPE.ERROR;
        } else {
            return TOKEN_TYPE.OPERATOR;
        }
    }
//-------------------------------------------------------------------------------------------------------------------
}
