package com.baeldung;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodNamePrinter extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(MethodDeclaration md, Void arg){
        super.visit(md, arg);
        System.out.println("Visited:" + md.getName());
    }

    @Override
    public void visit(MethodCallExpr me, Void arg){
        super.visit(me, arg);
        System.out.println("Call:" + me);
    }
}
