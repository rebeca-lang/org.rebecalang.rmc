package org.rebecalang.rmc.corerebeca.ctl;

import antlr.collections.AST;

public class PropertyNode {
    private String text;
    private Integer id;
    private AST root;


    public PropertyNode(String text, Integer id, AST root) {
        this.text = text;
        this.id = id;
        this.root = root;
    }

    public PropertyNode(String text, AST root) {
        this.text = text;
        this.root = root;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyNode that = (PropertyNode) o;

        if (!(this.root == that.root)) return false;
        if (!text.equals(that.text)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = text.hashCode();
        result = 31 * result + root.hashCode();
        return result;
    }

    public boolean hasLeft() {
        AST left = root.getFirstChild();
        if (left == null) {
            return false;
        }
        return true;
    }

    public boolean hasRight() {
        AST left = root.getFirstChild();
        if (left == null) {
            return false;
        }
        else {
            if (left.getNextSibling() == null) {
                return false;
            }
        }
        return true;
    }
}
