package org.jsoup.nodes;

import org.jsoup.SerializationException;
import org.jsoup.internal.StringUtil;
import org.jsoup.helper.Validate;

import java.io.IOException;

/**
 * An XML Declaration.
 */
public class XmlDeclaration extends LeafNode {
    // todo this impl isn't really right, the data shouldn't be attributes, just a run of text after the name
    private final boolean isProcessingInstruction; // <! if true, <? if false, declaration (and last data char should be ?)

    /**
     * Create a new XML declaration
     * @param name of declaration
     * @param isProcessingInstruction is processing instruction
     */
    public XmlDeclaration(String name, boolean isProcessingInstruction) {
        Validate.notNull(name);
        value = name;
        this.isProcessingInstruction = isProcessingInstruction;
    }

    /**
     * Create a new XML declaration
     * @param name of declaration
     * @param baseUri Leaf Nodes don't have base URIs; they inherit from their Element
     * @param isProcessingInstruction is processing instruction
     * @see XmlDeclaration#XmlDeclaration(String, boolean)
     * @deprecated
     */
    public XmlDeclaration(String name, String baseUri, boolean isProcessingInstruction) {
        this(name, isProcessingInstruction);
    }

    @Override
    public String nodeName() {
        return "#declaration";
    }

    /**
     * Get the name of this declaration.
     * @return name of this declaration.
     */
    public String name() {
        return coreValue();
    }

    /**
     * Get the unencoded XML declaration.
     * @return XML declaration
     */
    public String getWholeDeclaration() {
        StringBuilder sb = StringUtil.borrowBuilder();
        try {
            getWholeDeclaration(sb, new Document.OutputSettings());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
        return StringUtil.releaseBuilder(sb).trim();
    }

    private void getWholeDeclaration(Appendable accum, Document.OutputSettings out) throws IOException {
        for (Attribute attribute : attributes()) {
            if (!attribute.getKey().equals(nodeName())) { // skips coreValue (name)
                accum.append(' ');
                attribute.html(accum, out);
            }
        }
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<")
            .append(isProcessingInstruction ? "!" : "?")
            .append(coreValue());
        getWholeDeclaration(accum, out);
        accum
            .append(isProcessingInstruction ? "!" : "?")
            .append(">");
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    public String toString() {
        return outerHtml();
    }
}
