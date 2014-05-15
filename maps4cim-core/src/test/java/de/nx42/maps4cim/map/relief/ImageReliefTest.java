package de.nx42.maps4cim.map.relief;



public class ImageReliefTest {

//    @Test
//    public void scaleComparisonTest() throws Exception {
//        int sizeX = 800 * 2;
//        int sizeY = 618 * 2;
//        String base = "D:\\Eigenes\\Programming\\projects\\maps4cim\\heightmaps\\test\\";
//        String input = base + "test-16.tif";
//        File outImgj = new File(base, "out-imgj.png");
//        File outOwn = new File(base, "out-own.png");
//
//        // open image
//        ImagePlus img = IJ.openImage(input);
//        ImageProcessor ip = img.getProcessor();
//
//        // resize using ImageJ
//        Stopwatch stopIJ = Stopwatch.createStarted();
//
//        ip.setInterpolationMethod(ImageProcessor.BICUBIC);
//        BufferedImage bi = ip.resize(sizeX, sizeY).getBufferedImage();
//
//        System.out.println("ImageJ: " + stopIJ.stop().elapsed(TimeUnit.MILLISECONDS));
//        ImageIO.write(bi, "png", outImgj);
//
//        // resize using own implementation
//
//
//        short[] pixels = (short[]) ip.getPixels();
//        int width = ip.getWidth();
//        int height = ip.getHeight();
//        float[][] img2d = new float[height][width];
//        for (int i = 0; i < pixels.length; i++) {
//            img2d[i / width][i % width] = pixels[i];
//        }
//
//        Stopwatch stopOwn = Stopwatch.createStarted();
//
//        Interpolation it = new Bicubic(img2d);
//        float[][] scaled = it.resize(sizeX, sizeY);
//
//        System.out.println("Own: " + stopOwn.stop().elapsed(TimeUnit.MILLISECONDS));
//
//        short[] pixelsResized = new short[sizeX * sizeY];
//        for (int i = 0; i < scaled.length; i++) {
//            for (int j = 0; j < scaled[i].length; j++) {
//                pixelsResized[i * sizeX + j] = (short) scaled[i][j];
//            }
//        }
//
//        ImageStack is = ImageStack.create(sizeX, sizeY, 1, 16);
//        is.setPixels(pixelsResized, 1);
//        ImagePlus imgRes = new ImagePlus("resized", is);
//        ImageIO.write(imgRes.getBufferedImage(), "png", outOwn);
//
//
//    }



}
