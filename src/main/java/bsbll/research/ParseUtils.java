package bsbll.research;

final class ParseUtils {

    /**
     * Counts the occurrences of eror indicators like "(E3)", "(13E6)", etc, in the input string. 
     */
    public static int countErrorIndicators(String s) {
        int e = 0;
        int start = s.indexOf("(");
        while (start != -1) {
            int end = s.indexOf(")", start + 1);
            if (end == -1) {
                break;
            }
            String x = s.substring(start + 1, end);
            if (x.contains("E")) {
                ++e;
            }
            start = s.indexOf("(", end + 1);
        }
        return e;
    }
    
    private ParseUtils() {/**/}

}
