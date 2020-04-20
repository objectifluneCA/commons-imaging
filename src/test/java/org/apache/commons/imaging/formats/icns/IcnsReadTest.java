/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.imaging.formats.icns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class IcnsReadTest extends IcnsBaseTest {

    public static Stream<File> data() throws Exception {
        return getIcnsImages().stream();
    }

    /**
     * Provide data for tests of ICNS images with mono and/or JPEG png data. The
     * logo in the issue IMAGING-248 was the groovy.icns. But the Python project
     * also provides an image that contains a ic09 image.
     *
     * <p>icns2png was used to retrieve the number of images (i.e. icns2png -l
     * image_file.icns, then counted the number of ico entries, ignoring the masks).</p>
     * @return stream of test arguments
     */
    public static Stream<Arguments> provideIcnsImagesWithMonoAndJpegPngData() {
    	return Arrays
    			.asList(
    					Arguments.of("/images/icns/IMAGING-248/python.icns", 7),
    					Arguments.of("/images/icns/IMAGING-248/groovy.icns", 3))
    			.stream();
    }

    @Disabled(value = "RoundtripTest has to be fixed befor implementation can throw UnsupportedOperationException")
    @ParameterizedTest
    @MethodSource("data")
    public void testImageMetadata(File imageFile) throws Exception {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            Imaging.getMetadata(imageFile);
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testImageInfo(File imageFile) throws Exception {
        final ImageInfo imageInfo = Imaging.getImageInfo(imageFile, Collections.emptyMap());
        assertNotNull(imageInfo);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testBufferedImage(File imageFile) throws Exception {
        final BufferedImage image = Imaging.getBufferedImage(imageFile);
        assertNotNull(image);
        // TODO assert more
    }

    /**
     * Test ICNS types such as mono (ICON) and some types for either JPEG2000 or PNG
     * (icp4, icp5, ic11, etc). For IMAGING-248.
     * @throws IOException if it fails to read the input stream
     * @throws ImageReadException if the image is corrupted or invalid
     */
    @ParameterizedTest()
    @MethodSource("provideIcnsImagesWithMonoAndJpegPngData")
    public void testIcnsElementMonoPngJpeg(String file, int numberOfImages) throws ImageReadException, IOException {
    	File testFile = new File(IcnsReadTest.class.getResource(file).getFile());
    	List<BufferedImage> images = new IcnsImageParser().getAllBufferedImages(testFile);
    	assertEquals(numberOfImages, images.size());
    }
}
