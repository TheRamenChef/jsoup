package org.jsoup.select;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import static org.jsoup.select.NodeFilter.FilterResult.CONTINUE;
import static org.jsoup.select.NodeFilter.FilterResult.STOP;

/**
 * Collects a list of elements that match the supplied criteria.
 *
 * @author Jonathan Hedley
 */
public class Collector {

    private Collector() {
    }

    /**
     Build a list of elements, by visiting root and every descendant of root, and testing it against the evaluator.
     @param eval Evaluator to test elements against
     @param root root of tree to descend
     @return list of matches; empty if none
     */
    public static Elements collect (Evaluator eval, Element root) {
        Elements elements = new Elements();
        NodeTraversor.traverse(new Accumulator(root, elements, eval), root);
        return elements;
    }

    private static class Accumulator implements NodeVisitor {
        private final Element root;
        private final Elements elements;
        private final Evaluator eval;

        Accumulator(Element root, Elements elements, Evaluator eval) {
            this.root = root;
            this.elements = elements;
            this.eval = eval;
        }

        @Override
        public void head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (eval.matches(root, el))
                    elements.add(el);
            }
        }

        @Override
        public void tail(Node node, int depth) {
            // void
        }
    }

    public static Element findFirst(Evaluator eval, Element root) {
        FirstFinder finder = new FirstFinder(root, eval);
        NodeTraversor.filter(finder, root);
        return finder.match;
    }

    private static class FirstFinder implements NodeFilter {
        private final Element root;
        private Element match = null;
        private final Evaluator eval;

        FirstFinder(Element root, Evaluator eval) {
            this.root = root;
            this.eval = eval;
        }

        @Override
        public FilterResult head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (eval.matches(root, el)) {
                    match = el;
                    return STOP;
                }
            }
            return CONTINUE;
        }

        @Override
        public FilterResult tail(Node node, int depth) {
            return CONTINUE;
        }
    }

}
