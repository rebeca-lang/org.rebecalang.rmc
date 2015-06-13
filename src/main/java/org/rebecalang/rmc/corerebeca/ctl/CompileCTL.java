package org.rebecalang.rmc.corerebeca.ctl;

import antlr.collections.AST;
import antlr.ASTFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CompileCTL {
    public static int idSeqCounter=0;
    public static ArrayList<PropertyNode> postOrder(AST input) {

        ArrayList<PropertyNode> retvalue = new ArrayList<PropertyNode>();
        if (input.getFirstChild() != null) {
            retvalue.addAll(postOrder(input.getFirstChild()));
            idSeqCounter++;
            if (input.getFirstChild().getNextSibling() != null) {
                retvalue.addAll(postOrder(input.getFirstChild().getNextSibling()));
                idSeqCounter++;
            }
        }
        retvalue.add(new PropertyNode(input.getText(),idSeqCounter, input));
        return retvalue;
    }


    public static ArrayList<PropertyNode> preOrder(AST input, ArrayList<PropertyNode> postOrderTraverse) throws Exception {
        ArrayList<PropertyNode> retvalue = new ArrayList<PropertyNode>();
        int indx = postOrderTraverse.indexOf(new PropertyNode(input.getText(), input));
        if (indx < 0) {
            throw new Exception("Invalid input in property");
        }
        retvalue.add(postOrderTraverse.get(indx));
        if (input.getFirstChild() != null) {
            retvalue.addAll(preOrder(input.getFirstChild(), postOrderTraverse));
            if (input.getFirstChild().getNextSibling() != null) {
                retvalue.addAll(preOrder(input.getFirstChild().getNextSibling(), postOrderTraverse));
            }
        }
        return retvalue;
    }

    public static void generatePropCodes(List<Property> properties, File output) {
        try {

            //BufferedWriter propCpp = new BufferedWriter(new FileWriter("C:\\Users\\Mehdi\\Thesis\\imp\\RMC-CTL\\CTLprop.cpp"));
            BufferedWriter propCpp = new BufferedWriter(new FileWriter(output.getAbsolutePath() + File.separator + "CTLprop.cpp"));

            propCpp.write("#include \"CTLprop.h\"\n" +
                    "#include \"CTLMC.h\"\n" +
                    "\n" +
                    "\n");
            for (Property property : properties) {

                propCpp.write(
                        "Operator** generateProp_" + property.getName() + "()\n" +
                                "{\n" +
                                "\t\n" +
                                "\tOperator** ops = new Operator*[" + property.getPostOrderTraverse().size() + "];\n" +
                                "\t\n");
                List<PropertyNode> input = property.getPostOrderTraverse();
                propCpp.write("\topCount= 0;\n");
                for (int cnt = 0; cnt < input.size(); cnt++) {
                    PropertyNode propertyNode = input.get(cnt);
                    String current_op = propertyNode.getText();

                    String left = "null", right = "null";
                    if (propertyNode.hasLeft()) {
                        PropertyNode pNodeL = property.getLeft(propertyNode);
                        left = "" + getOPname(pNodeL.getText()) + "_" + pNodeL.getId();
                        if (propertyNode.hasRight()) {
                            PropertyNode pNodeR = property.getRight(propertyNode);
                            right = "" + getOPname(pNodeR.getText()) + "_" + pNodeR.getId();
                        }
                    }
                    propCpp.write("\tOperator *" + getOPname(current_op) + "_" + propertyNode.getId() + " = new Operator;\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->arity = " + getArity(current_op) + ";\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->opname = " + getOPname(current_op) + ";\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->proposition = new char[" + (current_op.length() + 1) + "];\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->proposition = \"" + current_op + "\";\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->left = " + left + ";\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->right = " + right + ";\n" +
                            "\t" + getOPname(current_op) + "_" + propertyNode.getId() + "->id = opCount;\n" +
                            "\t\n" +
                            "\tops[" + cnt + "] = " + getOPname(current_op) + "_" + propertyNode.getId() + ";\n" +
                            "\t\n" +
                            "\t\n");
                    propCpp.write("\topCount++;\n");
                }
                propCpp.write("\t\n" +
                        "\treturn ops;\n" +
                        "}\n");
            }
            propCpp.write("Operator** generatePropertyArray(char* propName) {\n");
            if(properties.isEmpty())
                propCpp.write("\treturn NULL;\n");

            for (Property property : properties) {
                propCpp.write("\tif (strcmp(propName, \"" + property.getName() + "\") == 0)\n");
                propCpp.write("\t\treturn generateProp_" + property.getName() + "();\n");
            }
            propCpp.write("\treturn NULL;\n");
            propCpp.write("}\n");
            propCpp.write("int propLength(char* propName) {\n");
            if(properties.isEmpty())
                propCpp.write("\treturn -1;\n");
            for (Property property : properties) {
                propCpp.write("\tif (strcmp(propName, \"" + property.getName() + "\") == 0)\n");
                propCpp.write("\t\treturn " + property.getPostOrderTraverse().size() + ";\n");
            }
            propCpp.write("\treturn 0;\n");
            propCpp.write("}\n\n");
            propCpp.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static String getArity(String Op) {
        if (Op.compareTo("EU") == 0 || Op.compareTo("AU") == 0 || Op.compareTo("&&") == 0) return "binary";
        else if (Op.equals("AX") || Op.equals("!")) return "unary";
        else return "atomic";

    }

    public static String getOPname(String Op) {
        if (Op.equals("!")) return "neg";
        else if (Op.compareTo("&&") == 0) return "conj";
        else if (Op.compareTo("AX") == 0) return "ax";
        else if (Op.compareTo("AU") == 0) return "au";
        else if (Op.compareTo("EU") == 0) return "eu";
        else if (Op.compareTo("TRUE") == 0) return "TRUE";
        else if (Op.compareTo("FALSE") == 0) return "FALSE";
        else return "ap";
    }

    public static boolean normalizable(AST input)
    {
        if(input.getText().compareTo("EX")==0)
            return true;
        else if(input.getText().compareTo("AF")==0)
            return true;
        else if(input.getText().compareTo("EF")==0)
            return true;
        else if(input.getText().compareTo("AG")==0)
            return true;
        else if(input.getText().compareTo("EG")==0)
            return true;
        else if(input.getText().compareTo("||")==0)
            return true;
        else if(input.getText().compareTo("->")==0)
            return true;
        return false;

    }
    public static AST norm(AST input)
    {
        Stack<AST> st = new Stack<AST>();
        st.push(input);
        while(!st.empty()){

            if((st.peek().getFirstChild()!= null)&&(st.peek().getFirstChild().getType()!=-1))
            {
                AST toAdd = st.peek().getFirstChild();
                toAdd.setType(-2);
                st.push(toAdd);
            }
            else
            {
                if(st.peek().getNextSibling()!=null && st.peek().getNextSibling().getType()!=-1)
                {
                    AST toAdd = st.peek().getNextSibling();
                    toAdd.setType(-3);
                    st.push(toAdd);
                }
                else
                {
                    AST current = st.pop();
                    if(st.empty())
                        return normMainAST(current);
                    if(current.getType()==-2)
                    {
                        AST newAST = normMainAST(current);
                        newAST.setType(-1);

//                        if((newAST.getText().compareTo("!")==0) && (st.peek().getText().compareTo("!")==0))
//                        {
//                          st.pop();
//                          st.push(newAST.getFirstChild());
//                        }
//                        else
//                        {
                            //newAST.setNextSibling(st.peek().getFirstChild().getNextSibling());
                            st.peek().setFirstChild(newAST);
                            
//                        }
                    }
                    else if(current.getType()==-3)
                    {
                        AST newAST = normMainAST(current);
//                        if((newAST.getText().compareTo("!")==0) && (newAST.getFirstChild().getText().compareTo("!")==0))
//                        {
//                          newAST = newAST.getFirstChild().getFirstChild();
//                        }
                        newAST.setType(-1);
                        st.peek().setNextSibling(newAST);
                    }
                }
            }

        }
        return input;
    }

    public static AST normMainAST(AST input) {
        if(input.getText().compareTo("EX")==0)
            return normEX(input);
        else if(input.getText().compareTo("AF")==0)
            return normAF(input);
        else if(input.getText().compareTo("EF")==0)
            return normEF(input);
        else if(input.getText().compareTo("AG")==0)
            return normAG(input);
        else if(input.getText().compareTo("EG")==0)
            return normEG(input);
        else if(input.getText().compareTo("||")==0)
            return normOR(input);
        else if(input.getText().compareTo("->")==0)
            return normIF(input);

        return input;
    }

    public static AST normEX(AST input) {
        AST firstChild = input.getFirstChild();
        AST inputSibling = input.getNextSibling();
        //input.setText("!");
        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("!");
        AST axx = t.create();
        axx.setText("AX");
        AST nott2 = t.create();
        nott2.setText("!");
        nott2.setFirstChild(firstChild);
        axx.setFirstChild(nott2);
        output.setFirstChild(axx);

        output.setNextSibling(inputSibling);
        return output;
    }

    public static AST normOR(AST input) {
        AST p = input.getFirstChild();
        AST q = p.getNextSibling();
        AST inputSibling = input.getNextSibling();
        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("!");
        AST andd = t.create();
        andd.setText("&&");
        AST nottp = t.create();
        nottp.setText("!");
        AST nottq = t.create();
        nottq.setText("!");
        if(q.getText().compareTo("!")==0)
        {
            nottp.setFirstChild(p);
            //nottq.setFirstChild(q);
            nottp.setNextSibling(q.getFirstChild());
            andd.setFirstChild(nottp);
            output.setFirstChild(andd);
         }
        else
        {
            nottp.setFirstChild(p);
            nottq.setFirstChild(q);
            nottp.setNextSibling(nottq);
            andd.setFirstChild(nottp);
            output.setFirstChild(andd);
        }
        
        output.setNextSibling(inputSibling);
        return output;
    }

    public static AST normIF(AST input){
        AST p = input.getFirstChild();
        AST q = p.getNextSibling();
        AST inputSibling = input.getNextSibling();

        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("!");
        AST not1 = t.create();
        AST andd = t.create();
        not1.setText("!");
        andd.setText("&&");
        if(q.getText().compareTo("!")==0)
        {

            p.setNextSibling(q.getFirstChild());


        }
        else
        {
            not1.setFirstChild(q);
            p.setNextSibling(not1);
        }
         andd.setFirstChild(p);
         output.setFirstChild(andd);


        //output = normOR(output);
        output.setNextSibling(inputSibling);
        return output;

    }
    public static AST normAF(AST input){
        AST f = input.getFirstChild();
        AST inputSibling = input.getNextSibling();
        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("AU");
        AST truee = t.create();
        truee.setText("TRUE");
        truee.setNextSibling(f);
        output.setFirstChild(truee);

        output.setNextSibling(inputSibling);
        return output;
    }

    public static AST normEF(AST input){
        AST f = input.getFirstChild();
        AST inputSibling = input.getNextSibling();
        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("EU");
        AST truee = t.create();
        truee.setText("TRUE");
        truee.setNextSibling(f);
        output.setFirstChild(truee);

        output.setNextSibling(inputSibling);
        return output;
    }

    public static AST normAG(AST input){
        AST f = input.getFirstChild();
        AST inputSibling = input.getNextSibling();
        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("!");
        AST euu = t.create();
        euu.setText("EU");
        AST truee = t.create();
        truee.setText("TRUE");
        AST notteu = t.create();
        notteu.setText("!");
        if(f.getText().compareTo("!") == 0)
        {
            truee.setNextSibling(f.getFirstChild());
        }
        else
        {
           notteu.setFirstChild(f);
           truee.setNextSibling(notteu);
        }
        euu.setFirstChild(truee);
        output.setFirstChild(euu);

        output.setNextSibling(inputSibling);
        return output;
   }

    public static AST normEG(AST input){
        AST f = input.getFirstChild();
        AST inputSibling = input.getNextSibling();
        ASTFactory t = new ASTFactory();
        AST output = t.create();
        output.setText("!");
        AST euu = t.create();
        euu.setText("AU");
        AST truee = t.create();
        truee.setText("TRUE");
        AST notteu = t.create();
        notteu.setText("!");
        if(f.getText().compareTo("!") == 0)
        {
            truee.setNextSibling(f.getFirstChild());
        }
        else
        {
           notteu.setFirstChild(f);
           truee.setNextSibling(notteu);
        }
        euu.setFirstChild(truee);
        output.setFirstChild(euu);

        output.setNextSibling(inputSibling);
        return output;
   }

    //////////////////
    // To be completed
    //////////////////
    public static void compileCTL(AST ctlPropertysRoot, File output) {
        List<Property> properties = new ArrayList<Property>();
        if (ctlPropertysRoot != null) {
            //ctlPropertysRoot.getFirstChild().setFirstChild(norm(ctlPropertysRoot.getFirstChild()));

            //iterating through properties to normalize


            AST tt =ctlPropertysRoot.getFirstChild();

            while (tt != null) {
                try {
                    AST temp = norm(tt.getFirstChild());
                    tt.setFirstChild(temp);
                    Property property = new Property(tt.getText(), tt.getFirstChild());
                    idSeqCounter=0;
                    ArrayList<PropertyNode> postOrderTraverse = postOrder(tt.getFirstChild());
                    ArrayList<PropertyNode> preOrderTraverse = preOrder(tt.getFirstChild(), postOrderTraverse);
                    tt = tt.getNextSibling();
                    property.setPostOrderTraverse(postOrderTraverse);
                    property.setPreOrderTraverse(preOrderTraverse);
                    properties.add(property);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        generatePropCodes(properties, output);

        //generatePropCodes(properties);

    }
}
