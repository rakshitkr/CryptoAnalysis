package de.fraunhofer.iem.crypto.test;

import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.crypto.CogniCryptAndroidAnalysis;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class AndroidAnalysisTest
{
    @Test(expected = IllegalArgumentException.class)
    public void expectIllegalArgument()
    {
        String apkPath = "";
        String platformPath = "";
        String rulesPath = "";
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath);
    }

    @Test
    public void runAnalysis()
    {
        String apkPath = "src\\test\\resources\\NormalActivityCallbackDebug.apk";
        String rulesPath = "..\\CryptoAnalysis\\src\\test\\resources\\crySL\\JavaCryptographicArchitecture-1.4-ruleset.zip";

        // TODO: How can this be relative to use in CI ???
        String platformPath = "C:\\Android\\platforms";

        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }
}
