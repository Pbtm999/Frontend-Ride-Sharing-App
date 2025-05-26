package rsa.quad;

/**
 * The Visitor interface, part of the abstract layer of the design pattern with the same name.
 * In this case, the visitor is parameterized by HasPoint and defines visit methods for each of the types in the composite, namely LeafTrie and NodeTrie.
 * @param <T> type that extends interface HasPoint
 */
public interface Visitor<T extends HasPoint> {

    /**
     * Do a visit to a leaf in the composite structure
     * @param leaf to be visited
     */
    void visit(LeafTrie<T> leaf);

    /**
     * Do a visit to a node in the composite structure
     * @param node to be visited
     */
    void visit(NodeTrie<T> node);
}
