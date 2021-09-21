package toru.key.input;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PickUpCharacter {

    public static void main(String[] args) throws Exception {
        PickUpCharacter p = new PickUpCharacter();
        p.call();
    }


    public void call() throws Exception {

        // 画像読み取り
        File file = new File("1image.png");
        BufferedImage img = ImageIO.read(file);

        int xMax = 930;
        int yMax = 99;
        int yHalf = 45;

        int xCount = 0;
        int sideXCount = 0;

        int blackStartX = 0;
        int blackEndX = 0;

        int imageCount = 0;

        for (int x = 0; x < xMax; x++) {
            Color color = new Color(img.getRGB(x, yHalf));
            if (sideXCount < 20) {


                // 最初は白が 20px 続くまで何もしない
                if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) {
                    sideXCount++;
                } else {
                    sideXCount = 0;
                }
                continue;
            }

            if (blackStartX == 0) {
                for (int y = 0; y < yMax; y++) {
                    Color white = new Color(img.getRGB(x, y));

                    // 縦一列のどこかで黒が出るまでチェック
                    if (white.getRed() == 0 && white.getGreen() == 0 && white.getBlue() == 0) {
                        blackStartX = x;
                        break;
                    }
                }
            }

            if (blackStartX != 0) {
                boolean isAllWhite = true;
                for(int y = 0; y < yMax; y++) {
                    Color white = new Color(img.getRGB(x, y));

                    // 縦に黒がある場合はスキップ
                    if (white.getRed() == 0 && white.getGreen() == 0 && white.getBlue() == 0) {
                        isAllWhite = false;
                        break;
                    }
                }

                if (isAllWhite) {
                    blackEndX = x;
                    BufferedImage subimg = img.getSubimage(blackStartX, 0, blackEndX - blackStartX, 99 );
                    ImageIO.write( subimg, "jpeg", new File( imageCount + "image_cut.png"));

                    imageCount++;
                    blackStartX = 0;
                    blackEndX = 0;
                }

            }

        }

    }
}
