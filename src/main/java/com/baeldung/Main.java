package com.baeldung;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class Main {
    public static void main(String[] args) throws MojoFailureException, MojoExecutionException {
        System.out.println("Hi");
        DependencyCounterMojo dependencyCounterMojo = new DependencyCounterMojo();

        dependencyCounterMojo.params = new Params();
        dependencyCounterMojo.params.a = "/home/umutek/IdeaProjects/PluginDeneme/SMC.java";
        dependencyCounterMojo.params.b = "/home/umutek/IdeaProjects/PluginDeneme/out.pu";

        dependencyCounterMojo.execute();
    }
}
