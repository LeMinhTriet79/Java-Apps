package exercisetimer;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 *
 * @author Minh Triet
 */
public class FontLoader {
     public static Font loadCustomFont(String fontFileName, float fontSize) {
        try {
            // Lấy InputStream của file font từ resources
            // Đường dẫn này là đường dẫn tương đối trong classpath của ứng dụng
            // Đảm bảo "fonts/" khớp với thư mục bạn đã tạo trong NetBeans
            InputStream is = FontLoader.class.getResourceAsStream("/font/" + fontFileName);
            
            if (is == null) {
                System.err.println("Lỗi: Không tìm thấy file font ở đường dẫn /font/" + fontFileName);
                return null;
            }

            // Tạo đối tượng Font từ InputStream
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is); // Hoặc Font.OPENTYPE_FONT nếu là .otf

            // Đăng ký font với GraphicsEnvironment để nó có thể được sử dụng bởi AWT/Swing
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // Trả về font với kích thước mong muốn
            return customFont.deriveFont(fontSize);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải hoặc đăng ký font '" + fontFileName + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
