package com.company;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

enum NodeType {
    operation,
    constant,
    variable,
    function
}

enum MathOperation {
    plus,
    minus,
    multiply,
    divide,
    power
}

enum Function {
    ln,
    sin,
    cos
}

class Node {
    private double value;
    private NodeType type;
    private MathOperation operation;
    private Function function;
    private Node left;
    private Node right;
    private int id;

    static private int nextid = 0;

    Node() {
        type = NodeType.variable;
        left = null;
        right = null;
        id = nextid;
        nextid++;
    }

    Node(double value) {
        type = NodeType.constant;
        this.value = value;
        left = null;
        right = null;
        id = nextid;
        nextid++;
    }

    Node(MathOperation operation) {
        this.operation = operation;
        this.type = NodeType.operation;
        left = null;
        right = null;
        id = nextid;
        nextid++;
    }

    Node (Function function) {
        this.function = function;
        this.type = NodeType.function;
        left = null;
        right = null;
        id = nextid;
        nextid++;
    }

    Node getLeft() {
        return left;
    }

    Node getRight() {
        return right;
    }

    void addLeft(Node item) {
        left = item;
    }

    void addRight(Node item) {
        right = item;
    }
    NodeType getType() {
        return type;
    }

    double getValue() {
        return value;
    }

    MathOperation getOperation() {
        return operation;
    }

    Function getFunction() {
        return function;
    }

    void setFunction(Function function) {
        this.function = function;
    }

    void setValue(double value) {
        this.value = value;
    }

    int getId() {
        return  id;
    }

    void setType(NodeType type) {
        this.type = type;
    }

    void setOperation(MathOperation operation)
    {
        this.operation = operation;
    }

    Node copy() {
        Node item = new Node(0);
        item.type = type;
        item.operation = operation;
        item.value = value;
        item.function = function;
        if (left != null) {
            item.addLeft(left.copy());
        }
        if (right != null) {
            item.addRight(right.copy());
        }
        return item;

    }

    void set(Node node) {
        this.type = node.type;
        this.operation = node.operation;
        this.function = node.function;
        this.value = node.value;
        this.left = node.left;
        this.right = node.right;
    }

    public static  MathOperation StringToOperation(String str) {
        if (str.equals("*")) {
            return MathOperation.multiply;
        }
        if (str.equals("/")) {
            return MathOperation.divide;
        }
        if (str.equals("^")) {
            return MathOperation.power;
        }
        if (str.equals("+")) {
            return MathOperation.plus;
        }
        if (str.equals("-")) {
            return MathOperation.minus;
        }
        return MathOperation.plus;
    }

    public  static Function StringToFunction(String str) {
        if (str.equals("ln")) {
            return Function.ln;
        }
        if (str.equals("sin")) {
            return Function.sin;
        }
        if (str.equals("cos")) {
            return Function.cos;
        }
        return Function.ln;
    }

    public static String OperationToString(MathOperation operation) {
        switch (operation)
        {
            case plus:
                return "+";
            case minus:
                return "-";
            case power:
                return "^";
            case multiply:
                return "*";
            case divide:
                return "/";
        }
        return "?";
    }

    public  static String FunctionToString(Function function) {
        switch (function) {
            case ln:
                return "ln";
            case sin:
                return "sin";
            case cos:
                return "cos";
        }
        return "?";
    }

    public int getDeep() {
        int l = 0;
        int r = 0;
        if (this.getLeft() != null) {
            l = this.getLeft().getDeep();
        }
        if (this.getRight() != null) {
            r = this.getRight().getDeep();
        }
        return  Math.max(l+1,r+1);
    }

    public boolean equal(Node node) {
        if (this.type != node.type) {
            return false;
        }
        switch (this.type) {
            case variable:
                return true;
            case constant:
                return (this.value == node.value);
            case function:
                return this.getLeft().equal(node.getLeft());
            case operation:
                return (this.getLeft().equal(node.getLeft()) && (this.getRight().equal(node.getRight())) );
        }
        return false;
    }
}


class Tree {
    private Node root;
    private String logstr;

    public Tree(Node node) {
        root = node;
        logstr = "";
    }

    public Tree(Node node, String logstr) {
        this.root = node;
        this.logstr = logstr;
    }

    public Tree(String str) {
        logstr = "";
        logstr = "\\mbox{Generating tree from string: }\\mbox{ " + str + "}\\\\";
        String[] parts = str.split("\\s+");
        str = "";
        for (String part : parts) {
            str += part;
        }
        root = parseString(str, 0);
        logstr += "\\mbox{Generated: }" + buildLatex(root) + "\\\\";
    }

    public Tree(Tree tree) {
        this.logstr = tree.logstr;
        this.root = tree.root.copy();
    }

    public void saveImage(String filename) {
        String latex = buildLatex(root);
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(22).build();
        icon.setInsets(new Insets(5, 5, 5, 5));
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);
        try {
            File file = new File(filename+".png");
            ImageIO.write(image, "png", file.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image getImage() {
        String latex = buildLatex(root);
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(18).build();
        icon.setInsets(new Insets(5, 5, 5, 5));
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);
        return image;
    }

    public void logImage(String filename) {
        String latex = logstr;
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(22).build();
        icon.setInsets(new Insets(5, 5, 5, 5));
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);
        try {
            File file = new File(filename+".png");
            ImageIO.write(image, "png", file.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void optimise() {
        logstr += "\\mbox{Expression to optimise: }" + buildLatex(root) + "\\\\";
        while (simplify(root)) {
            logstr += "\\mbox{optimised: }" + buildLatex(root) + "\\\\";
        }
        while(upVars(root)) {
            logstr += "\\mbox{variables positions optimised: }" + buildLatex(root) + "\\\\";
        }
        while (simplify(root)) {
            logstr += "\\mbox{optimised: }" + buildLatex(root) + "\\\\";
        }
        while (swapArgs(root)) {
            logstr += "\\mbox{optimised: }" + buildLatex(root) + "\\\\";
        }
        logstr += "\\mbox{Optimisation result: }" + buildLatex(root) + "\\\\";
    }

    public Tree differentiate() {
        logstr += "\\mbox{Expression to differentiate: }" + buildLatex(root) + "\\\\";
        Tree result = this;
        d(result.root);
        logstr += "\\mbox{Differentiation result: }" + buildLatex(root) + "\\\\";
        return result;
    }

    public double calculate(double value) {
        return  calc(root, value);
    }

    private Node parseString(String str, int opIndex) {
        String[] ops = {"+", "-", "*", "/", "^"};
        int index = str.indexOf(ops[opIndex]);
        while (index > 0) {
            String left = str.substring(0, index);
            String right = str.substring(index + 1, str.length());
            if ( (countBrackets(left) == 0) && (countBrackets(right) == 0)) {
                break;
            }
            index = str.indexOf(ops[opIndex], index+1);
        }
        if (index >= 0) {
            Node root = new Node(Node.StringToOperation(ops[opIndex]));
            if ((index == 0) && (Node.StringToOperation(ops[opIndex]) == MathOperation.minus) ) {
                root.addLeft(new Node(0));
            } else {
                String left = str.substring(0, index);
                root.addLeft(parseString(left, 0));
            }
            String right = str.substring(index + 1, str.length());
            root.addRight(parseString(right, 0));
            return root;
        } else if (opIndex <= ops.length - 2) {
            return parseString(str, opIndex + 1);
        } else if ((str.charAt(0) == '(') && (str.charAt(str.length()-1) == ')')) {
            return parseString(str.substring(1,str.length()-1), 0);
        } else if (str.equals("x")) {
            return new Node();
        }  else if (isDouble(str)) {
            return new Node(Double.parseDouble(str));
        } else {
            return parseFunction(str);
        }
    }

    private static int countBrackets(String str)
    {
        int count = 0;
        for (int i = 0; i < str.length(); i++)
        {
            if (str.charAt(i) == '(') {
                count++;
            }
            if (str.charAt(i) == ')') {
                count--;
            }
        }
        return count;
    }

    private static boolean isDouble(String str){
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Node parseFunction(String str) {
        String[] functions = {"ln", "sin", "cos"};
        String name = str.substring(0, str.indexOf("("));
        Node root = new Node(Node.StringToFunction(name));
        root.addLeft(parseString(str.substring(str.indexOf("(")+1, str.length()-1), 0));
        return root;
    }

    private String buildLatex(Node node) {
        String result = "";
        if (node != null) {
            if (node.getType() == NodeType.variable) {
                result += "x";
            } else if (node.getType() == NodeType.constant) {
                if (node.getValue() < 0) {
                    result += "(";
                }
                if (((int) (node.getValue())) == node.getValue()) {
                    result += (int) (node.getValue());
                } else {
                    result += node.getValue();
                }
                if (node.getValue() < 0) {
                    result += ")";
                }
            } else if (node.getType() == NodeType.function) {
                result += "{" + Node.FunctionToString(node.getFunction()) + "(" + buildLatex(node.getLeft()) + ")}";
            } else {
                switch (node.getOperation()) {
                    case plus:
                        result += "{" + buildLatex(node.getLeft()) + "+" + buildLatex(node.getRight()) + "}";
                        break;
                    case minus:
                        result += "{" + buildLatex(node.getLeft()) + "-";
                        if ((node.getRight().getDeep() > 1) && (node.getRight().getType() == NodeType.operation) && ((node.getRight().getOperation() == MathOperation.plus) || (node.getRight().getOperation() == MathOperation.minus))) {
                            result += "{(";
                        }
                        result += buildLatex(node.getRight());
                        if ((node.getRight().getDeep() > 1) && (node.getRight().getType() == NodeType.operation) && ((node.getRight().getOperation() == MathOperation.plus) || (node.getRight().getOperation() == MathOperation.minus))) {
                            result += ")}";
                        }
                        result += "}";
                        break;
                    case multiply:
                        result += "{{";
                        if ((node.getLeft().getDeep() > 1) && (node.getLeft().getType() == NodeType.operation) && ((node.getLeft().getOperation() == MathOperation.plus) || (node.getLeft().getOperation() == MathOperation.minus))) {
                            result += "(";
                        }
                        result += buildLatex(node.getLeft());
                        if ((node.getLeft().getDeep() > 1) && (node.getLeft().getType() == NodeType.operation) && ((node.getLeft().getOperation() == MathOperation.plus) || (node.getLeft().getOperation() == MathOperation.minus))) {
                            result += ")";
                        }
                        if (node.getLeft().getType() == NodeType.constant) {
                            if ((node.getRight().getType() == NodeType.function) || (node.getRight().getType() == NodeType.variable)) {
                                result += "}{";
                            } else if ((node.getRight().getType() == NodeType.operation) && (node.getRight().getOperation() == MathOperation.power) && (node.getRight().getLeft().getType() == NodeType.variable)) {
                                result += "}{";
                            } else if ((node.getRight().getDeep() > 1) && (node.getRight().getType() == NodeType.operation) && ((node.getRight().getOperation() == MathOperation.plus) || (node.getRight().getOperation() == MathOperation.minus))) {
                                result += "}{";
                            } else {
                                result += "}\\times{";
                            }
                        } else if (node.getRight().getType() == NodeType.constant) {
                            if ((node.getLeft().getDeep() > 1) && (node.getLeft().getType() == NodeType.operation) && ((node.getLeft().getOperation() == MathOperation.plus) || (node.getLeft().getOperation() == MathOperation.minus))) {
                                result += "}{";
                            } else {
                                result += "}\\times{";
                            }
                        } else {
                            result += "}\\times{";
                        }
                        if ((node.getRight().getDeep() > 1) && (node.getRight().getType() == NodeType.operation) && ((node.getRight().getOperation() == MathOperation.plus) || (node.getRight().getOperation() == MathOperation.minus))) {
                            result += "(";
                        }
                        result += buildLatex(node.getRight());
                        if ((node.getRight().getDeep() > 1) && (node.getRight().getType() == NodeType.operation) && ((node.getRight().getOperation() == MathOperation.plus) || (node.getRight().getOperation() == MathOperation.minus))) {
                            result += ")";
                        }
                        result += "}}";
                        break;
                    case divide:
                        result += "{\\frac{" + buildLatex(node.getLeft()) + "}{" + buildLatex(node.getRight()) + "}}";
                        break;
                    case power:
                        result += "{";
                        if (node.getLeft().getDeep() > 1) {
                            result += "{(";
                        }
                        result += buildLatex(node.getLeft());
                        if (node.getLeft().getDeep() > 1) {
                            result += ")}";
                        }
                        result += "^{" + buildLatex(node.getRight()) + "}}";
                        break;
                }
            }
        }
        return result;
    }

    private boolean simplify(Node item) {
        if (item != null) {
            //logstr += "\\mbox{Optimising: }" + buildLatex(item) + "\\\\";
            if ((item.getLeft() != null) && (item.getRight() != null) && (item.getLeft().getType() == NodeType.constant) && (item.getRight().getType() == NodeType.constant)) {
                Node old = item.copy();
                double left = item.getLeft().getValue();
                double right = item.getRight().getValue();
                double temp = 0;
                switch (item.getOperation()) {
                    case plus:
                        temp = left + right;
                        break;
                    case minus:
                        temp = left - right;
                        break;
                    case multiply:
                        temp = left * right;
                        break;
                    case divide:
                        temp = left / right;
                        break;
                    case power:
                        temp = Math.pow(left, right);
                        break;
                }
                item.setType(NodeType.constant);
                item.setValue(temp);
                item.addLeft(null);
                item.addRight(null);
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.power) && (item.getRight().getType() == NodeType.constant) && (item.getRight().getValue() == 1)) {
                Node old = item.copy();
                item.set(item.getLeft());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.power) && (item.getRight().getType() == NodeType.constant) && (item.getRight().getValue() == 0)) {
                Node old = item.copy();
                item.set(new Node(1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.power) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 0)) {
                Node old = item.copy();
                item.set(new Node(0));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.power) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 1)) {
                Node old = item.copy();
                item.set(new Node(1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.multiply) && (item.getRight().getType() == NodeType.constant) && (item.getRight().getValue() == 0)) {
                Node old = item.copy();
                item.set(new Node(0));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.multiply) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 0)) {
                Node old = item.copy();
                item.set(new Node(0));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.multiply) && (item.getRight().getType() == NodeType.constant) && (item.getRight().getValue() == 1)) {
                Node old = item.copy();
                item.set(item.getLeft());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.multiply) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 1)) {
                Node old = item.copy();
                item.set(item.getRight());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.divide) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 0)) {
                Node old = item.copy();
                item.set(new Node(0));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getRight().getType() == NodeType.constant) && (item.getRight().getValue() == 0)) {
                Node old = item.copy();
                item.set(item.getLeft());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 0)) {
                Node old = item.copy();
                item.set(item.getRight());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.minus) && (item.getRight().getType() == NodeType.constant) && (item.getRight().getValue() == 0)) {
                Node old = item.copy();
                item.set(item.getLeft());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.minus) && (item.getLeft().getType() == NodeType.constant) && (item.getLeft().getValue() == 0)) {
                Node old = item.copy();
                item.setOperation(MathOperation.multiply);
                item.addLeft(new Node(-1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.minus) && (item.getLeft().getType() == NodeType.variable) && (item.getRight().getType() == NodeType.variable)) {
                Node old = item.copy();
                item.set(new Node(0));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.divide) && (item.getLeft().getType() == NodeType.variable) && (item.getRight().getType() == NodeType.variable)) {
                Node old = item.copy();
                item.set(new Node(1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getLeft().getType() == NodeType.variable) && (item.getRight().getType() == NodeType.variable)) {
                Node old = item.copy();
                item.setOperation(MathOperation.multiply);
                item.addLeft(new Node(2));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.multiply) && (item.getLeft().getType() == NodeType.variable) && (item.getRight().getType() == NodeType.variable)) {
                Node old = item.copy();
                item.setOperation(MathOperation.power);
                item.addRight(new Node(2));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.minus) && (item.getRight().getType() == NodeType.constant) /*&& (item.getRight().getValue() < 0)*/) {
                Node old = item.copy();
                item.setOperation(MathOperation.plus);
                item.getRight().setValue(-item.getRight().getValue());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.multiply) && (item.getLeft().getType() == NodeType.variable) && (item.getRight().getType() == NodeType.operation) && (item.getRight().getOperation() == MathOperation.power) && (item.getRight().getLeft().getType() == NodeType.variable)) {
                Node old = item.copy();
                item.setOperation(MathOperation.power);
                item.addLeft(new Node());
                item.addRight(new Node(MathOperation.plus));
                item.getRight().addLeft(old.getRight().getRight());
                item.getRight().addRight(new Node(1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getRight().getType() == NodeType.operation) && (item.getRight().getOperation() == MathOperation.multiply) && (item.getLeft().equal(item.getRight().getLeft()))) {
                Node old = item.copy();
                item.set(new Node(MathOperation.multiply));
                item.addLeft(old.getLeft());
                item.addRight(new Node(MathOperation.plus));
                item.getRight().addLeft(new Node(1));
                item.getRight().addRight(old.getRight().getRight());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getRight().getType() == NodeType.operation) && (item.getRight().getOperation() == MathOperation.multiply) && (item.getLeft().equal(item.getRight().getRight()))) {
                Node old = item.copy();
                item.set(new Node(MathOperation.multiply));
                item.addLeft(old.getLeft());
                item.addRight(new Node(MathOperation.plus));
                item.getRight().addLeft(new Node(1));
                item.getRight().addRight(old.getRight().getRight());
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getLeft().getType() == NodeType.operation) && (item.getLeft().getOperation() == MathOperation.multiply) && (item.getRight().equal(item.getLeft().getLeft()))) {
                Node old = item.copy();
                item.set(new Node(MathOperation.multiply));
                item.addLeft(old.getRight());
                item.addRight(new Node(MathOperation.plus));
                item.getRight().addLeft(old.getLeft().getRight());
                item.getRight().addRight(new Node(1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getType() == NodeType.operation) && (item.getOperation() == MathOperation.plus) && (item.getLeft().getType() == NodeType.operation) && (item.getLeft().getOperation() == MathOperation.multiply) && (item.getRight().equal(item.getLeft().getRight()))) {
                Node old = item.copy();
                item.set(new Node(MathOperation.multiply));
                item.addLeft(old.getRight());
                item.addRight(new Node(MathOperation.plus));
                item.getRight().addLeft(old.getLeft().getLeft());
                item.getRight().addRight(new Node(1));
                Log(old, item, "optimisation: ");
                return true;
            } else if ((item.getLeft() != null) && (item.getRight() != null) && (item.getLeft().equal(item.getRight()))) {
                Node old = item.copy();
                Node left = item.getLeft();
                Node right = item.getRight();
                boolean temp = false;
                switch (item.getOperation()) {
                    case plus:
                        item.set(new Node(MathOperation.multiply));
                        item.addLeft(new Node(2));
                        item.addRight(right);
                        temp = true;
                        break;
                    case minus:
                        item.set(new Node(0));
                        temp = true;
                        break;
                    case multiply:
                        item.set(new Node(MathOperation.power));
                        item.addLeft(left);
                        item.addRight(new Node(2));
                        temp = true;
                        break;
                    case divide:
                        item.set(new Node(1));
                        temp = true;
                        break;
                    case power:
                        break;
                }
                if (temp) {
                    Log(old, item, "optimisation: ");
                }
                return temp;
            } else {
                return simplify(item.getLeft()) || simplify(item.getRight());
            }
        }
        return false;
    }

    private boolean upVars(Node item) {
        boolean result = false;
        if (item != null) {
            if (item.getType() == NodeType.operation) {
                Node left = item.getLeft();
                Node right = item.getRight();
                if ((left.getType() == NodeType.operation) && (item.getOperation() == left.getOperation()) && ((item.getOperation() == MathOperation.multiply) || (item.getOperation() == MathOperation.plus))) {
                    if (right.getType() == NodeType.constant) {
                        if (left.getLeft().getType() != NodeType.constant) {
                            Node old = item.copy();
                            Node temp = right.copy();
                            right.set(left.getLeft().copy());
                            left.getLeft().set(temp);
                            Log(old, item, "moving: ");
                            result = true;
                        } else if (left.getRight().getType() != NodeType.constant) {
                            Node old = item.copy();
                            Node temp = right.copy();
                            right.set(left.getRight().copy());
                            left.getRight().set(temp);
                            Log(old, item, "moving: ");
                            result = true;
                        }
                    }
                } else if ((right.getType() == NodeType.operation) && (item.getOperation() == right.getOperation()) && ((item.getOperation() == MathOperation.multiply) || (item.getOperation() == MathOperation.plus))) {
                    if (left.getType() == NodeType.constant) {
                        if (right.getLeft().getType() != NodeType.constant) {
                            Node old = item.copy();
                            Node temp = left.copy();
                            left.set(right.getLeft().copy());
                            right.getLeft().set(temp);
                            Log(old, item, "moving: ");
                            result = true;
                        } else if (right.getRight().getType() != NodeType.constant) {
                            Node old = item.copy();
                            Node temp = left.copy();
                            left.set(right.getRight().copy());
                            right.getRight().set(temp);
                            Log(old, item, "moving: ");
                            result = true;
                        }
                    }
                }
            }
            result = result | upVars(item.getLeft());
            result = result | upVars(item.getRight());
        }
        return result;
    }

    private boolean swapArgs(Node item) {
        boolean result = false;
        if (item != null) {
            if ((item.getType() == NodeType.operation) && ((item.getOperation() == MathOperation.multiply) /*|| (item.getOperation() == MathOperation.plus)*/)) {
                if ((item.getRight().getType() == NodeType.constant) && (item.getLeft().getType() != NodeType.constant)) {
                    Node old = item.copy();
                    Node temp = item.getRight().copy();
                    item.getRight().set(item.getLeft().copy());
                    item.getLeft().set(temp);
                    Log(old, item, "Swapping: ");
                    result = true;
                }
            }
            result = result | swapArgs(item.getLeft());
            result = result | swapArgs(item.getRight());
        }
        return result;
    }

    private double calc(Node item, double value) {
        if (item != null) {
            if (item.getType() == NodeType.operation) {
                switch (item.getOperation()) {
                    case plus:
                        return calc(item.getLeft(), value) + calc(item.getRight(), value);
                    case minus:
                        return calc(item.getLeft(), value) - calc(item.getRight(), value);
                    case multiply:
                        return calc(item.getLeft(), value) * calc(item.getRight(), value);
                    case divide:
                        return calc(item.getLeft(), value) / calc(item.getRight(), value);
                    case power:
                        return Math.pow(calc(item.getLeft(), value), calc(item.getRight(), value));

                }
            } else if (item.getType() == NodeType.function) {
                switch (item.getFunction()) {
                    case ln:
                        return Math.log(calc(item.getLeft(), value));
                    case sin:
                        return Math.sin(calc(item.getLeft(), value));
                    case cos:
                        return Math.cos(calc(item.getLeft(), value));
                }
            } else if (item.getType() == NodeType.constant){
                return item.getValue();
            } else if (item.getType() == NodeType.variable) {
                return value;
            }
        }
        return Double.NaN;
    }

    private void d(Node item)
    {
        if (item != null) {
            logstr += "\\mbox{Differentiating: }" + buildLatex(item) + "\\\\";
            if (item.getType() == NodeType.constant) {
                Node old = item.copy();
                item.setValue(0);
                Log(old, item, "\\frac{d}{dx}");
            }
            if (item.getType() == NodeType.variable) {
                Node old = item.copy();
                item.setType(NodeType.constant);
                item.setValue(1);
                Log(old, item, "\\frac{d}{dx}");
            }
            if (item.getType() == NodeType.operation) {
                Node old = item.copy();
                Node left = null;
                Node right = null;
                Node temp = null;
                switch (item.getOperation()) {
                    case plus:
                    case minus:
                        d(item.getLeft());
                        d(item.getRight());
                        break;
                    case multiply:
                        item.setOperation(MathOperation.plus);
                        left = new Node(MathOperation.multiply);
                        left.addLeft(item.getLeft().copy());
                        left.addRight(item.getRight().copy());
                        right = new Node(MathOperation.multiply);
                        right.addLeft(item.getLeft().copy());
                        right.addRight(item.getRight().copy());
                        item.addLeft(left);
                        item.addRight(right);
                        d(left.getRight());
                        d(right.getLeft());
                        logstr += "\\mbox{as " + "\\frac{d}{dx} (u*v) = u*\\frac{dv}{dx} + v*\\frac{du}{dx}" + " so}\\\\";
                        break;
                    case divide:
                        right = new Node(MathOperation.power);
                        right.addLeft(item.getRight().copy());
                        right.addRight(new Node(2));
                        temp = new Node(MathOperation.multiply);
                        temp.addLeft(item.getLeft().copy());
                        temp.addRight(item.getRight().copy());
                        left = new Node(MathOperation.minus);
                        left.addLeft(temp.copy());
                        left.addRight(temp.copy());
                        item.addLeft(left);
                        item.addRight(right);
                        d(item.getLeft().getLeft().getRight());
                        d(item.getLeft().getRight().getLeft());
                        logstr += "\\mbox{as " + "\\frac{d}{dx} (\\frac{u}{v}) = \\frac{u*\\frac{dv}{dx} + v*\\frac{du}{dx}}{v^{2}}" + " so}\\\\";
                        break;
                    case power:
                        right = new Node(MathOperation.multiply);
                        right.addLeft(new Node(MathOperation.power));
                        right.getLeft().addLeft(item.getLeft().copy());
                        right.getLeft().addRight(item.getRight().copy());
                        right.addRight(new Node(MathOperation.multiply));
                        right.getRight().addLeft(new Node(Function.ln));
                        right.getRight().getLeft().addLeft(item.getLeft().copy());
                        right.getRight().addRight(item.getRight().copy());
                        temp = new Node(MathOperation.power);
                        temp.addLeft(item.getLeft().copy());
                        temp.addRight(new Node(MathOperation.minus));
                        temp.getRight().addLeft(item.getRight().copy());
                        temp.getRight().addRight(new Node(1));
                        left = new Node(MathOperation.multiply);
                        left.addLeft(item.getRight().copy());
                        left.addRight(new Node(MathOperation.multiply));
                        left.getRight().addLeft(temp);
                        left.getRight().addRight(item.getLeft().copy());
                        item.setType(NodeType.operation);
                        item.setOperation(MathOperation.plus);
                        item.addLeft(left);
                        item.addRight(right);
                        d(item.getLeft().getRight().getRight());
                        d(item.getRight().getRight().getRight());
                        logstr += "\\mbox{as " + "\\frac{d}{dx} (u^{v}) = v*u^{v-1}*\\frac{du}{dx}+u^{v}*ln(u)*\\frac{dv}{dx}" + " so}\\\\";
                        break;
                }
                Log(old, item, "\\frac{d}{dx}");
            } else if (item.getType() == NodeType.function) {
                Node left = null;
                Node right = null;
                Node temp = null;
                Node old = item.copy();
                switch (item.getFunction()) {
                    case ln:
                        left = new Node(MathOperation.divide);
                        left.addLeft(new Node(1));
                        left.addRight(item.getLeft().copy());
                        right = item.getLeft().copy();
                        item.setType(NodeType.operation);
                        item.setOperation(MathOperation.multiply);
                        item.addLeft(left);
                        item.addRight(right);
                        d(item.getRight());
                        break;
                    case sin:
                        left= new Node(Function.cos);
                        left.addLeft(item.getLeft().copy());
                        right = item.getLeft().copy();
                        item.setType(NodeType.operation);
                        item.setOperation(MathOperation.multiply);
                        item.addLeft(left);
                        item.addRight(right);
                        d(item.getRight());
                        break;
                    case cos:
                        left = new Node(-1);
                        temp = new Node(Function.sin);
                        temp.addLeft(item.getLeft().copy());
                        right = new Node(MathOperation.multiply);
                        right.addLeft(temp);
                        right.addRight(item.getLeft().copy());
                        item.setType(NodeType.operation);
                        item.setOperation(MathOperation.multiply);
                        item.addLeft(left);
                        item.addRight(right);
                        d(item.getRight().getRight());
                        break;
                }
                Log(old, item, "\\frac{d}{dx}");
            }
        }
    }

    private void Log(Node node0, Node node1, String str) {
        logstr += str + " (" + buildLatex(node0) + ")  =  " + buildLatex(node1) + "\\\\";
    }
}




public class Main extends Applet {

    private Button buttonParse;
    private Button buttonDiff;
    private Button buttonOpt;
    private TextField inputString;
    private Panel panel;
    private boolean drawIn;
    private boolean drawInOpt;
    private boolean drawOut;
    private boolean drawOutOpt;
    private Tree tree_in;
    private Tree tree_in_opt;
    private Tree tree_out;
    private Tree tree_out_opt;
    private String oldstr;

    public void init() {
        setLayout(new FlowLayout());
        buttonParse = new Button("Parse");
        buttonOpt = new Button("optimise");
        buttonDiff = new Button("Differentiate");
        inputString = new TextField("((1+2+3-4*5/2+3^2)*x^4)/(3*x^3+x/x+x^5)", 60);
        //Rectangle r = new Rectangle(100,100,200,200);
        panel = new Panel();
        //panel.setBounds(r);

        buttonOpt.setEnabled(false);
        buttonDiff.setEnabled(false);

        add(inputString);
        add(buttonParse);
        add(buttonOpt);
        add(buttonDiff);
        add(panel);

        buttonParse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oldstr = inputString.getText();
                tree_in = new Tree(inputString.getText());
                tree_in_opt = null;
                tree_out = null;
                tree_out_opt = null;
                drawIn = true;
                buttonOpt.setEnabled(true);
                buttonDiff.setEnabled(true);
                repaint();
            }
        });

        buttonOpt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*if (tree_out_opt != null) {
                    tree_out_opt.optimise();
                    drawOutOpt = true;
                } else*/
                if (tree_out != null) {
                    tree_out_opt = new Tree(tree_out);
                    tree_out_opt.optimise();
                    drawOutOpt = true;
                } else if (tree_in != null) {
                    tree_in_opt = new Tree(tree_in);
                    tree_in_opt.optimise();
                    drawInOpt = true;
                }
                repaint();
            }
        });

        buttonDiff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tree_in_opt != null) {
                    tree_out = new Tree(tree_in_opt);
                } else {
                    tree_out = new Tree(tree_in);
                }
                tree_out = tree_out.differentiate();
                tree_out_opt = null;
                drawOut = true;
                repaint();
            }
        });

        inputString.addTextListener(new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                boolean changed = oldstr.equals(inputString.getText());
                buttonOpt.setEnabled(changed);
                buttonDiff.setEnabled(changed);
                drawIn = (changed && (tree_in != null));
                drawInOpt = (changed &&(tree_in_opt != null));
                drawOut = (changed && (tree_out != null));
                drawOutOpt = (changed &&(tree_out_opt != null));
                repaint();
            }
        });

        tree_in = null;
        tree_in_opt = null;
        tree_out = null;
        drawIn = false;
        drawOut = false;
        drawInOpt = false;
        drawOutOpt = false;
        oldstr = "";
    }


    public void paint(Graphics g) {
        if ((drawIn) && (tree_in != null)) {
            Image image = tree_in.getImage();
            g.drawImage(image, 50, 150, panel);
        }
        if ((drawInOpt) && (tree_in_opt != null)) {
            Image image = tree_in_opt.getImage();
            g.drawImage(image, 50, 300, panel);
        }
        if ((drawOut) && (tree_out != null)) {
            Image image = tree_out.getImage();
            g.drawImage(image, 50, 450, panel);
        }
        if ((drawOutOpt) && (tree_out_opt != null)) {
            Image image = tree_out_opt.getImage();
            g.drawImage(image, 50, 600, panel);
        }
        g.drawRect(10, 115, 1800, 145);
        g.drawString("Input:", 20, 140);
        g.drawRect(10, 265, 1800, 145);
        g.drawString("Optimised Input:", 20, 290);
        g.drawRect(10, 415, 1800, 145);
        g.drawString("Output:", 20, 440);
        g.drawRect(10, 565, 1800, 145);
        g.drawString("Optimised Output:", 20, 590);
    }


    public static void main(String[] args) {
        String str = "((1+2+3-4*5/2+3^2)*x^4)/(3*x^3+x/x+x^5)";
        Tree t = new Tree(str);
        t.saveImage("out-1-input");
        t.optimise();
        t.saveImage("out-2-optimised_input");
        Tree r = t.differentiate();
        r.saveImage("out-3-differentiated");
        r.optimise();
        r.saveImage("out-4-optimised_result");
        r.logImage("log");
        /*
        Node root = new Node(MathOperation.multiply);
        root.addLeft(new Node());
        root.addRight(new Node(MathOperation.plus));
        root.getRight().addLeft(new Node(1));
        root.getRight().addRight(new Node(1));
        System.out.println(root.equal(root));
        System.out.println(root.getLeft().equal(root.getRight()));
        System.out.println(root.getRight().getLeft().equal(root.getRight().getRight()));*/
    }
}