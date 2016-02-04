package webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class HTMLTemplater {

	private static final String _MARK_START = "<?TLP ";
	private static final String _MARK_END = " ?>";
	
	private static int[] getIndexesOfMarks (String htmlString, int pointer){
		int StartOfMark = htmlString.indexOf(_MARK_START, pointer);
		int EndOfMark = htmlString.indexOf(_MARK_END, pointer);
		
		if(StartOfMark > -1 && EndOfMark > StartOfMark){
			int[] tuple = new int[] {StartOfMark, EndOfMark};
			return tuple;
		}
		return null;
	}
	
	private static String constructString(String htmlString, HashMap<String,String> params){
		int pointer = 0;
		int markPositions[] = getIndexesOfMarks(htmlString, pointer);
		int StartOfMark;
		int EndOfMark;
		String templatedHTMLtext = "";
		if(markPositions == null){
			return null;
		}
		
		while(markPositions != null){
			StartOfMark = markPositions[0];
			EndOfMark = markPositions[1];
			templatedHTMLtext += htmlString.substring(pointer, StartOfMark);
			
			//<?TPL _variableName ?> lineToParse = _variableName
			String lineToParse = htmlString.substring(StartOfMark + _MARK_START.length(), EndOfMark);
			
			// _variableName --> variableName
			String variableName = getParamNameFromLine(lineToParse);
			String templatedString = mapVariableNameToParamString(variableName, params);
			
			if(templatedString != null){
				templatedHTMLtext += templatedString;
			}
			pointer = EndOfMark + _MARK_END.length();
			markPositions = getIndexesOfMarks(htmlString, pointer);
		}
		
		templatedHTMLtext += htmlString.substring(pointer);
		return templatedHTMLtext;	
	}
	
	private static String mapVariableNameToParamString(String variableName, HashMap<String, String> params){
		if(params.containsKey(variableName)){
			String templatedString = " " + variableName + " = " + params.get(variableName) + " "; 
			return templatedString;
		}
		return null;
	}
	
	private static String getParamNameFromLine(String variableLine){
		int indexOfUnderScore = variableLine.indexOf('_') + 1;
		if(indexOfUnderScore > -1){
			String paramName = variableLine.substring(indexOfUnderScore);
			return paramName;
		}
		return null;
	}
	
	public static byte[] templateHTML(File file, HashMap<String,String> params){
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String htmlString = "";
			String line = "";
			while((line = reader.readLine()) != null){
				htmlString += line;
			}
			reader.close();
			String templatedFile = constructString(htmlString, params);
			return templatedFile.getBytes();
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: FILE NOT FOUND");
		} catch (IOException e) {
			System.err.println("ERROR: IO EXCEPTION");
		}
		return null;
	}
}
