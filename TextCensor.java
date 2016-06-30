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
		String replaced = t.replaceCensoredWords(t.readFile());
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
            if (nextNode == null) {
                nextNode = new KeywordsTrieNode(c);
                currentNode.putNextNode(nextNode);
            }
            currentNode = nextNode;
        }
        currentNode.setEnd(true);
    }
	

	/**
	 * Initialize the Dictionary for all the keywords and phrases
	 */
	private void init() {
        //读取敏感词库
        Set<String> keyWords = keywordsFilter(KEYWORD_STRING);
        //初始化根节点
        root = new KeywordsTrieNode(' ');
        //创建敏感词
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
        //TODO
        System.out.println("Success to load file...");
        System.out.println(sb);
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
                if (startNode.isEnd()) {
                	sb.append("XXXX");
                	start = end - 1;
                	break;
                }
                startNode = startNode.getNextNode(Character.toLowerCase(text.charAt(end)));
                if (startNode == null) {
                	sb.append(text.substring(start, end));
                	start = end - 1;
                    break;
                }
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
