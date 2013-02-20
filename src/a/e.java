package a;

import java.util.logging.Level;
import java.util.logging.Logger;


public class e {
	public static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static boolean __DEBUG = true;
	public static boolean __INFO  = true;
	public static boolean __FATAL = true;
	public static boolean __HIGHDETAILS = false;
	
	public static boolean __LOGGER = true;
	
	public static final int FATAL = 5;
	public static final int DEBUG = 3;
	public static final int INFO  = 1;
	public static final int OFF   = 0;
	
	static final int __DEFAULTDISPLAY = INFO;
	public static String endl = System.getProperty("line.separator");
	public static String tab  = "\t";
	
	public static int indent = 0;
	
	public static void incIndent(){ ++indent; }
	public static void decIndent(){ --indent; if(indent < 0) indent = 0;}
	
	public static String dent() { String result = ""; for(int i = 0; i < indent; i++) { result += tab; }; return result; }
	
	public static String write(String msg)   { return dent() + msg ; } 
	public static String writeln(String msg) { return write(msg) + endl; }
	public static String print(String msg)   { return print(msg, __DEFAULTDISPLAY); } 
	public static String println(String msg) { return println(msg, __DEFAULTDISPLAY); }
	public static String print(String msg,   int displayLevel)   { String result = write(msg); log(result);  System.out.print(result); return result; }
	public static String println(String msg, int displayLevel) { String result = writeln(msg); log(result); System.out.print(result); return result; }
	
	public static void log(String msg){
		if(__LOGGER){switch(__DEFAULTDISPLAY){
			case INFO:	LOGGER.info(msg);break;
			case DEBUG:	LOGGER.warning(msg);break;
			case FATAL:	LOGGER.severe(msg);break;	}	}   }
	
	/**
	 * Init Function, not assumed to be executed
	 */
	static boolean __initDone = false;
	public e(){
		if(!__initDone){
			LOGGER.setLevel(Level.SEVERE);
			__initDone = true;
		}
	}
}
