package net.sword.engine.OSISHandler;




public class TagHandlerHelper {


    /** return verse from osis id of format book.chap.verse
     * 
     * @param ososID osis Id
     * @return verse number
     */
    public static int osisIdToVerseNum(String osisID) {
       /* You have to use "\\.", the first backslash is interpreted as an escape by the
        Java compiler, so you have to use two to get a String that contains one
        backslash and a dot, which is what you want the regexp engine to see.*/
    	if (osisID!=null) {
	        String[] parts = osisID.split("\\.");
	        if (parts.length>1) {
	            String verse =  parts[parts.length-1];
	            return Integer.valueOf(verse);
	        }
    	}
        return 0;
    }
    
}
