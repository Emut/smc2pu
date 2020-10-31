package com.baeldung;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.List;

@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.COMPILE)
public class DependencyCounterMojo extends AbstractMojo {

    @Parameter
    Params params;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hi mom!");
        getLog().info(params.a);
        getLog().info(params.b);

        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(params.a));
            for (Node node : cu.findAll(MethodDeclaration.class)) {
                MethodDeclaration md = (MethodDeclaration) node;
                System.out.println("MD:" + md.getName());
                List<com.github.javaparser.ast.body.Parameter> params = md.getParameters();
                if (params.size() == 1) {
                    if(params.get(0).getTypeAsString().matches("StateMachineStateConfigurer<.*>")){
                        System.out.println("States:" + md.findAll(MethodCallExpr.class));
                        for(MethodCallExpr mce : md.findAll(MethodCallExpr.class)){
                            System.out.println(mce);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(params.b));
            writer.write("@startuml\n");
            writer.write("[*]-->STARTED\n");
            writer.write("STARTED : asd\n");
            writer.write("@enduml\n");
            writer.close();
        } catch (Exception e) {

        }
    }
}


