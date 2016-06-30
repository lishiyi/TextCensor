package info.shiyi.meltwater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Text Censor
 * @author Shiyi Li
 * @date   06/29/2016
 * @version 0.2
 */
public class TextCensor {
	
	private static final String FILE_PATH = "D:\\textToCensor.txt";
	private static final String OUTPUT_PATH = "D:\\textToCensor_output.txt";
	private static final String KEYWORD_STRING = "Hello world \"Boston Red Sox\" 'Pepperoni Pizza', "
			+ "'Cheese Pizza', beer, ,,, drink'   '  Apple Yoyo se 'see me' 'You can' ";
	private static KeywordsTrieNode root = null;
	/**
	 * Main method, for test
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		TextCensor t = new TextCensor();
		t.init();
		String input = t.readFile();
		// Print the input file for test
		System.out.println(input);
		String replaced = t.replaceCensoredWords(input);
		// Print the output file for test
		System.out.println(replaced);
		t.writeFile(replaced);
	}
	
	/**
	 * Filter the string, and make a set
	 * @param keywordsString
	 * @return The set includes all keywords and phrases
	 */
	private Set<String> keywordsFilter(String keywordsString){
		Set<String> result = new HashSet<String>();
		if(keywordsString == null || keywordsString.length() == 0){
			return result;
		}
		Pattern regex = Pattern.compile("[^\\s\"',]+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(keywordsString);
		//Put lower case for all the keywords into the set.
		while (regexMatcher.find()) {
		    if (regexMatcher.group(1) != null) {
		        // Add double-quoted phrase 
		        result.add(regexMatcher.group(1).trim().toLowerCase());
		    } else if (regexMatcher.group(2) != null) {
		        // Add single-quoted phrase
		        result.add(regexMatcher.group(2).trim().toLowerCase());
		    } else {
		        // Add unquoted word
		        result.add(regexMatcher.group().toLowerCase());
		    }
		}
		// Remove the empty string
		if(result.contains("")){
			result.remove("");
		}
		return result;
	}
	
	
	/**
     * Build the Trie dictionary for one keyword 
     * @param keyWord
     */
    private void buildKeywordTrie(String keyWord) {
        if (root == null) {
            System.out.println("Not Intialized");
            return;
        }
        KeywordsTrieNode currentNode = root;
        for (Character c : keyWord.toCharArray()) {
        	KeywordsTrieNode nextNode = currentNode.getNextNode(c);
        	// If next character does not exist in the trie, create a new next-node.
            if (nextNode == null) {
                nextNode = new KeywordsTrieNode(c);
                currentNode.putNextNode(nextNode);
            }
            currentNode = nextNode;
        }
        // Word ends, set the end flag to true.
        currentNode.setEnd(true);
    }
	

	/**
	 * Initialize the Dictionary for all the keywords and phrases
	 */
	private void init() {
        // Create keywords set
        Set<String> keyWords = keywordsFilter(KEYWORD_STRING);
        // Initialize root
        root = new KeywordsTrieNode(' ');
        // Create trie
        for (String keyWord : keyWords) {
        	buildKeywordTrie(keyWord);
        }
    }
	
	/**
	 * Read the file, return the string.
	 * @return The file as a String.
	 * @throws Exception 
	 */
	private String readFile() throws Exception{
		
        StringBuilder sb = new StringBuilder();
        // Read the file
        File file = new File(FILE_PATH);    
        InputStreamReader read = new InputStreamReader(new FileInputStream(file));
        try {
        	// Check if exists
            if(file.isFile() && file.exists()){      
                BufferedReader bufferedReader = new BufferedReader(read);
                String txt = null;
                while((txt = bufferedReader.readLine()) != null){
                    sb.append(txt);
                }
            }
            else{         
                throw new Exception("No files exist");
            }
        } catch (Exception e) {
            throw e;
        } finally{
            read.close();     // Close the file
        }
        System.out.println("Success to load file...");
        return sb.toString();
    }
	
    /**
     * Replace all the words in the list
     * @return Replaced string
     */
    private String replaceCensoredWords(String text) {
    	
        if (root == null) {
            init();
        }
        StringBuilder sb = new StringBuilder();
        // Check each character in the text
        for (int start = 0; start < text.length(); start++) {
            Character c = Character.toLowerCase(text.charAt(start));
            KeywordsTrieNode startNode = root.getNextNode(c);
            // If this char not in the list, append it. 
            if (startNode == null) {
            	sb.append(text.charAt(start));
                continue;
            }
            int end = start + 1;
            // Else, continue to search it in the trie
            while (end < text.length()) {
            	// If it is the end of the word, add "XXXX"
                if (startNode.isEnd()) {
                	sb.append("XXXX");
                	start = end - 1;
                	break;
                }
                startNode = startNode.getNextNode(Character.toLowerCase(text.charAt(end)));
                // If the character is not matched in the keywords trie, add this word.
                if (startNode == null) {
                	sb.append(text.substring(start, end));
                	start = end - 1;
                    break;
                }
                //Else, that is, the character is matched and not the end, continue.
                end++;
            }
        }
        return sb.toString();
    }
    
    /**
     * Write output file
     * @param input
     */
    private void writeFile(String input) {
        // Create a new file, and write the result.
        File writename = new File(OUTPUT_PATH);
        try {
			writename.createNewFile();
	        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
	        out.write(input);
	        out.flush(); 
	        out.close(); 
	        System.out.println("Success to save file...");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
