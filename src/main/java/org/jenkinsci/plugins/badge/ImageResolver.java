/*
* The MIT License
*
* Copyright 2013 Kohsuke Kawaguchi, Dominik Bartholdi
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
 */
package org.jenkinsci.plugins.badge;

import hudson.model.BallColor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.net.MalformedURLException;
import java.net.URL;

import jenkins.model.Jenkins;
import org.apache.commons.io.IOUtils;
/**
 *
 * @author dkersch
 */
public class ImageResolver {
    /**
     *
     */
    private final HashMap<String, StatusImage[]> styles;
    /**
     *
     */
    private final StatusImage[] defaultStyle;
    /**
     *
     */
    public static final String RED = "#cc0000";
    /**
     *
     */
    public static final String YELLOW = "#b2b200";
    /**
     *
     */
    public static final String GREEN = "#008000";
    /**
     *
     */
    public static final String GREY = "#808080";

    /**
     *
     * @throws IOException
     */
    public ImageResolver() throws IOException {
        styles = new HashMap<String, StatusImage[]>();
        // shields.io "flat" style (new default from Feb 1 2015)
        StatusImage[] flatImages;
        flatImages = new StatusImage[]{
            new StatusImage("build-failing-red-flat.svg"),
            new StatusImage("build-unstable-yellow-flat.svg"),
            new StatusImage("build-passing-brightgreen-flat.svg"),
            new StatusImage("build-running-blue-flat.svg"),
            new StatusImage("build-aborted-lightgrey-flat.svg"),
            new StatusImage("build-unknown-lightgrey-flat.svg")
        };
        defaultStyle = flatImages;
        styles.put("default", defaultStyle);
    }

    public StatusImage getCoverageImage(Integer codeCoverage) {

        // TODO don't read file everytime, store this as a static variable in
        // TODO memory with the constructor
        URL image = null;
        try {
            image = new URL(
                    Jenkins.getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    "status/build-coverage-flat.svg");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(image.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String replacedImage = replaceCodeCoverageSVG(sb.toString(), codeCoverage);
        InputStream is = IOUtils.toInputStream(replacedImage);
        String etag = "status/build-coverage-flat.svg" + codeCoverage;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    /**
     *
     * @param image
     * @param codeCoverage
     * @return
     */
    private String replaceCodeCoverageSVG(String image, Integer codeCoverage) {

        if (codeCoverage == null) {
            String modifiedColor = image.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{code-coverage-to-change}", "n/a");

        } else if (codeCoverage < 20) {
            String modifiedColor = image.replace("{hex-color-to-change}", RED);
            return modifiedColor.replace("{code-coverage-to-change}", codeCoverage.toString());
        } else if (codeCoverage < 80) {
            String modifiedColor = image.replace("{hex-color-to-change}", YELLOW);
            return modifiedColor.replace("{code-coverage-to-change}", codeCoverage.toString());
        } else {
            String modifiedColor = image.replace("{hex-color-to-change}", GREEN);
            return modifiedColor.replace("{code-coverage-to-change}", codeCoverage.toString());
        }

    }
    /**
     *
     * @param testPass
     * @param testTotal
     * @return
     */
    public StatusImage getTestResultImage(Integer testPass, Integer testTotal) {

        // TODO don't read file everytime
        // TODO store this as a static variable in memory with the constructor
        URL image = null;
        try {
            image = new URL(
                    Jenkins.getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    "status/build-test-result-flat.svg");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(image.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String replacedImage = replaceTestResultSVG(sb.toString(), testPass, testTotal);
        InputStream is = IOUtils.toInputStream(replacedImage);
        String etag = "status/build-test-result-flat.svg" + testPass + testTotal;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    /**
     *
     * @param image
     * @param testPass
     * @param testTotal
     * @return
     */
    private String replaceTestResultSVG(String image, Integer testPass, Integer testTotal) {

        if (testTotal == null) {
            String modifiedColor = image.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{passed-tests} / {total-tests}", "n/a");

        } else if (testPass < 20) {
            String modifiedColor = image.replace("{hex-color-to-change}", RED);
            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
            return modifiedPass.replace("{total-tests}", testTotal.toString());
        } else if (testPass < 80) {
            String modifiedColor = image.replace("{hex-color-to-change}", YELLOW);
            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
            return modifiedPass.replace("{total-tests}", testTotal.toString());
        } else {
            String modifiedColor = image.replace("{hex-color-to-change}", GREEN);
            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
            return modifiedPass.replace("{total-tests}", testTotal.toString());
        }

    }
    /**
     *
     * @param color
     * @return
     */
    public StatusImage getImage(BallColor color) {
        StatusImage[] images = styles.get("default");

        if (color.isAnimated()) {
            return images[3];
        }

        switch (color) {
            case RED:
                return images[0];
            case YELLOW:
                return images[1];
            case BLUE:
                return images[2];
            case ABORTED:
                return images[4];
            default:
                return images[5];

        }
    }

}
