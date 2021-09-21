package toru.key.input;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Keys {

    public static void main(String[] args) throws Exception {
        Keys key = new Keys();
        key.call();
    }

    public void call() throws Exception {
        Robot robot = new Robot();
        robot.setAutoDelay(5);
//        while (true) {
//
//            Thread.sleep(1000);
//            robot.keyPress(KeyEvent.VK_A);
//        }

        // スタートキー入力待ち

        // 画面キャプチャー

        // 文字判定

        // キー押下
        int index = 0;
        while (true) {
            Thread.sleep(40);

            this.capture(robot, index++);
            System.out.println("call");
        }


//        this.capture(robot);
    }


    private void capture(Robot robot, int index) throws Exception {
        Rectangle screenSize = new Rectangle(565, 450, 310, 33);
        BufferedImage screenShot = robot.createScreenCapture(screenSize);

        int resizeW = screenShot.getWidth() * 3;
        int resizeH = screenShot.getHeight() * 3;

        BufferedImage scaleImg = new BufferedImage(resizeW, resizeH, BufferedImage.TYPE_3BYTE_BGR);
        scaleImg.createGraphics().drawImage(
                screenShot.getScaledInstance(resizeW, resizeH, Image.SCALE_AREA_AVERAGING),
                0, 0, resizeW, resizeH, null);

        File file = new File("image.png");
        ImageIO.write( binary(toGray(scaleImg)), "png", file);
//        ImageIO.write(scaleImg, "png", file);

//        File file = new File("image.jpg");
//        screenShot = ImageIO.read(file);

        Runtime runtime = Runtime.getRuntime();
//        Process p = runtime.exec
//                ("/opt/local/bin/tesseract image.png stdout");
        Process p = runtime.exec
                ("tesseract --psm 6 image.png stdout -c tessedit_char_blacklist='[]|l10OI cq' -c tessedit_char_whitelist='abdefghijklmnopqrstuvwxyz,-!?' --dpi 300");
        p.waitFor(); // プロセス終了を待つ

        InputStream is = p.getInputStream(); // プロセスの結果を変数に格納する
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); // テキスト読み込みを行えるようにする

        String value = null;
        while (true) {
            String line = br.readLine();
            if (line == null || line.isBlank()) {
                break; // 全ての行を読み切ったら抜ける
            } else {
                value = line;
                System.out.println("line : " + line); // 実行結果を表示
            }
        }

        p.destroy(); // プロセスを明確に終了させ、資源を回収


        if (value == null) {
            return;
        }

        for (char c : value.toCharArray()) {

            if (c == '?') {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_SLASH);
                Thread.sleep(10);
                robot.keyRelease(KeyEvent.VK_SLASH);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            } else if (c == '!') {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_1);
                Thread.sleep(10);
                robot.keyRelease(KeyEvent.VK_1);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            } else {
                try {
                    robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
                } catch (Exception e) {
                    // notiong
                }
            }
        }

    }


    public BufferedImage toGray(BufferedImage readImage) {

        // モノクロに変換
        // 元イメージの幅、高さを取得。
        int w = readImage.getWidth();
        int h = readImage.getHeight();

        // 変換結果を書き込むBufferedImageを作成する。
        // サイズは元イメージと同じ幅、高さとする。
        BufferedImage writeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // 1ピクセルづつ処理を行う
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // ピクセル値を取得
                int c = readImage.getRGB(x, y);
                // 0.299や0.587といった値はモノクロ化の定数値
                int mono = (int) (0.299 * r(c) + 0.587 * g(c) + 0.114 * b(c));
                // モノクロ化したピクセル値をint値に変換
                int rgb = (a(c) << 24) + (mono << 16) + (mono << 8) + mono;
                writeImage.setRGB(x, y, rgb);
            }
        }

        return writeImage;
    }

    public BufferedImage binary(BufferedImage readImage) {
        int w = readImage.getWidth();
        int h = readImage.getHeight();

        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = readImage.getRGB(x, y);
                int mono = (int) (0.299 * r(c) + 0.587 * g(c) + 0.114 * b(c));

                if (mono > 110) {
                    write.setRGB(x, y, 0x000000); // 黒
//                    write.setRGB(x, y, 0xFFFFFF); // 白
                } else {
                    write.setRGB(x, y, 0xFFFFFF); // 白
//                    write.setRGB(x, y, 0x000000); // 黒

                }
            }
        }
        return write;
    }


    public static int a(int c){
        return c>>>24;
    }
    public static int r(int c){
        return c>>16&0xff;
    }
    public static int g(int c){
        return c>>8&0xff;
    }
    public static int b(int c){
        return c&0xff;
    }
    public static int rgb(int r,int g,int b){
        return 0xff000000 | r <<16 | g <<8 | b;
    }
    public static int argb(int a,int r,int g,int b){
        return a<<24 | r <<16 | g <<8 | b;
    }
}
