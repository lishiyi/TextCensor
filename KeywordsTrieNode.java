package info.shiyi.meltwater;

import java.util.HashMap;
import java.util.Map;

/**
 * Trie
 * @author Shiyi Li
 * @date   06/29/2016
 * @version 0.2
 */
public class KeywordsTrieNode {

    // The Character of this node
    private char key;

    // Children
    private Map<Character, KeywordsTrieNode> nextNodes;

    // If it is the end of a word
    private boolean end;

    public KeywordsTrieNode(char key) {
        this.key = key;
        nextNodes = new HashMap<Character, KeywordsTrieNode>();
        end = false;
    }

    public KeywordsTrieNode getNextNode(char key) {
        return nextNodes.get(key);
    }

    public void putNextNode(KeywordsTrieNode node) {
        nextNodes.put(node.getKey(), node);
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }

    public Map<Character, KeywordsTrieNode> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(Map<Character, KeywordsTrieNode> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
