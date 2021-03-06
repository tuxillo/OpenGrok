/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */

package org.opensolaris.opengrok.analysis.perl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import org.opensolaris.opengrok.analysis.FileAnalyzer;
import org.opensolaris.opengrok.analysis.WriteXrefArgs;
import static org.opensolaris.opengrok.util.CustomAssertions.assertLinesEqual;

/**
 * Tests the {@link PerlXref} class.
 */
public class PerlXrefTest {

    @Test
    public void sampleTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayOutputStream baosExp = new ByteArrayOutputStream();

        InputStream res = getClass().getClassLoader().getResourceAsStream(
            "org/opensolaris/opengrok/analysis/perl/sample.pl");
        assertNotNull("sample.pl should get-as-stream", res);
        writePerlXref(res, new PrintStream(baos));
        res.close();

        InputStream exp = getClass().getClassLoader().getResourceAsStream(
            "org/opensolaris/opengrok/analysis/perl/samplexrefres.html");
        assertNotNull("samplexrefres.html should get-as-stream", exp);
        copyStream(exp, baosExp);
        exp.close();
        baosExp.close();
        baos.close();

        String ostr = new String(baos.toByteArray(), "UTF-8");
        String estr = new String(baosExp.toByteArray(), "UTF-8");
        assertLinesEqual("Perl xref", estr, ostr);
    }

    private void writePerlXref(InputStream iss, PrintStream oss) throws IOException {
        InputStream begin = getClass().getClassLoader().getResourceAsStream(
            "org/opensolaris/opengrok/analysis/perl/samplebeginhtml.txt");
        assertNotNull("samplebeginhtml.txt should get-as-stream", begin);
        copyStream(begin, oss);

        Writer sw = new StringWriter();
        PerlAnalyzerFactory fac = new PerlAnalyzerFactory();
        FileAnalyzer analyzer = fac.getAnalyzer();
        analyzer.writeXref(new WriteXrefArgs(
            new InputStreamReader(iss, "UTF-8"), sw));
        oss.print(sw.toString());

        InputStream end = getClass().getClassLoader().getResourceAsStream(
            "org/opensolaris/opengrok/analysis/perl/sampleendhtml.txt");
        assertNotNull("sampleendhtml.txt should get-as-stream", end);
        copyStream(end, oss);
    }

    private void copyStream(InputStream iss, OutputStream oss) throws IOException {
        byte buffer[] = new byte[8192];
        int read;
        do {
            read = iss.read(buffer, 0, buffer.length);
            if (read > 0) {
                oss.write(buffer, 0, read);
            }
        } while (read >= 0);
    }
}
