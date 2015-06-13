package org.rebecalang.rmc.corerebeca.ctl;

import antlr.collections.AST;

import java.util.ArrayList;

public class Property {
    private String name;
    private AST propertyTree;
    private ArrayList<PropertyNode> preOrderTraverse;
    private ArrayList<PropertyNode> postOrderTraverse;


    public Property(String name, AST propertyTree) {
        this.name = name;
        this.propertyTree = propertyTree;
    }

    public String getName() {
        return name;
    }

    public AST getPropertyTree() {
        return propertyTree;
    }

    public ArrayList<PropertyNode> getPreOrderTraverse() {
        return preOrderTraverse;
    }

    public void setPreOrderTraverse(ArrayList<PropertyNode> preOrderTraverse) {
        this.preOrderTraverse = preOrderTraverse;
    }

    public ArrayList<PropertyNode> getPostOrderTraverse() {
        return postOrderTraverse;
    }

    public void setPostOrderTraverse(ArrayList<PropertyNode> postOrderTraverse) {
        this.postOrderTraverse = postOrderTraverse;
    }

    public PropertyNode getLeft(PropertyNode p) {
        if (p.hasLeft()) {
            int indx = preOrderTraverse.indexOf(p);
            return  preOrderTraverse.get(indx+1);
        }
        return null;
    }

    public PropertyNode getRight(PropertyNode p) {
        if (p.hasRight()) {
            int indx = postOrderTraverse.indexOf(p);
            return postOrderTraverse.get(indx-1);
        }
        return null;
    }
}
