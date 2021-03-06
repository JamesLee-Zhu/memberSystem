package com.zhuqifeng.commons.utils.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.zhuqifeng.commons.utils.UtilBaseProperties;

public class ImageCodeUtil extends UtilBaseProperties {
	// 图片的宽度。
	private static int width = 100;
	// 图片的高度。
	private static int height = 30;
	// 验证码字符个数
	private static int codeNum = 4;
	// 验证码干扰线数
	private static int lineCount = 10;
	// 噪声率
	private static float yawpRate = 0.01f;

	public static String generateCHNCode(HttpServletResponse response) {
		return generateCode(response, true);
	}

	public static String generateENGCode(HttpServletResponse response) {
		return generateCode(response, false);
	}

	// 生成图片
	private static String generateCode(HttpServletResponse response, boolean isChinese) {
		int fontHeight = height - 5;// 字体的高度
		// 图像buffer
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = buffImg.getGraphics();
		// 设置背景色
		g.setColor(getRandomColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设置干扰线
		drawRandomLine(g);
		// 添加噪点
		drawNoisePoint(buffImg, yawpRate);
		// 获取随机字符串
		String drawRandomCode = drawRandomCode(codeNum, (Graphics2D) g, isChinese, true, true, fontHeight);
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止图像缓存
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			ImageIO.write(buffImg, "png", outputStream);
		} catch (IOException e) {
			logger.error("生产图片验证码失败：" + e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return drawRandomCode;
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2017年3月23日上午11:35:51
	 * @description 生成图片噪点
	 * @param yawpRate
	 *            ：噪声率
	 * @return TODO
	 */
	private static void drawNoisePoint(BufferedImage buffImg, float yawpRate) {
		Random random = new Random();
		int area = (int) (yawpRate * width * height);
		for (int i = 0; i < area; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			buffImg.setRGB(x, y, random.nextInt(255));
		}
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2017年3月23日上午11:36:00
	 * @description 生成随机字符串
	 * @param TODO
	 * @return TODO
	 */
	private static String drawRandomCode(int codeNum, Graphics2D g, Boolean isChinese, Boolean useBaseCode, boolean isRotation, int fontHeight) {
		StringBuffer sb = new StringBuffer();
		// 设置字体
		Font font = new Font(isChinese ? "楷体" : "Fixedsys", Font.BOLD, fontHeight);
		g.setFont(font);
		int x = 5;
		// 控制字数
		for (int i = 1; i <= codeNum; i++) {
			// 设置字体旋转角度
			// 获取随机字
			String ch = "";
			if (isChinese) {
				ch = createRandomCHN(useBaseCode);
			} else {
				ch = createRandomENG() + "";
			}
			// 设置颜色
			g.setColor(getRandomColor(1, 200));
			if (isRotation) {
				int degree = new Random().nextInt() % 30;
				// 正向角度
				g.rotate(degree * Math.PI / 180, x, 20);
				g.drawString(ch, x, 23);
				// 反向角度
				g.rotate(-degree * Math.PI / 180, x, 20);
			} else {
				g.drawString(ch, x, 23);
			}
			x += 25;
			sb.append(ch);
		}
		return sb.toString();
	}

	// 得到随机颜色
	private static Color getRandomColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2017年3月23日上午11:42:51
	 * @description 产生随机字体
	 * @param TODO
	 * @return TODO
	 */
	private static Font getRandomFont(int size) {
		Random random = new Random();
		Font font[] = new Font[5];
		font[0] = new Font("Ravie", Font.PLAIN, size);
		font[1] = new Font("Antique Olive Compact", Font.PLAIN, size);
		font[2] = new Font("Fixedsys", Font.PLAIN, size);
		font[3] = new Font("Wide Latin", Font.PLAIN, size);
		font[4] = new Font("Gill Sans Ultra Bold", Font.PLAIN, size);
		return font[random.nextInt(5)];
	}

	// 扭曲方法
	private static void shear(Graphics g, int w1, int h1, Color color) {
		shearX(g, w1, h1, color);
		shearY(g, w1, h1, color);
	}

	private static void shearX(Graphics g, int w1, int h1, Color color) {
		Random random = new Random();
		int period = random.nextInt(2);
		boolean borderGap = true;
		int frames = 1;
		int phase = random.nextInt(2);
		for (int i = 0; i < h1; i++) {
			double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			g.copyArea(0, i, w1, 1, (int) d, 0);
			if (borderGap) {
				g.setColor(color);
				g.drawLine((int) d, i, 0, i);
				g.drawLine((int) d + w1, i, w1, i);
			}
		}
	}

	private static void shearY(Graphics g, int w1, int h1, Color color) {
		Random random = new Random();
		int period = random.nextInt(40) + 10; // 50;
		boolean borderGap = true;
		int frames = 20;
		int phase = 7;
		for (int i = 0; i < w1; i++) {
			double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			g.copyArea(i, 0, 1, h1, 0, (int) d);
			if (borderGap) {
				g.setColor(color);
				g.drawLine(i, (int) d, i, 0);
				g.drawLine(i, (int) d + h1, i, h1);
			}
		}
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2017年3月23日上午11:45:14
	 * @description 设置边框
	 * @param TODO
	 * @return TODO
	 */
	@SuppressWarnings("unused")
	private static void setBorder(Graphics g) {
		// 设置边框颜色
		g.setColor(Color.BLUE);
		// 边框区域
		g.drawRect(1, 1, width - 2, height - 2);
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2017年3月23日上午11:45:03
	 * @description 画随机线条
	 * @param TODO
	 * @return TODO
	 */
	private static void drawRandomLine(Graphics g) {
		Random random = new Random();
		for (int i = 0; i < lineCount; i++) {
			int xs = random.nextInt(width);
			int ys = random.nextInt(height);
			int xe = xs + random.nextInt(width);
			int ye = ys + random.nextInt(height);
			g.setColor(getRandomColor(1, 255));
			g.drawLine(xs, ys, xe, ye);
		}
	}

	private static String createRandomCHN(Boolean useBaseCode) {
		if (useBaseCode) {
			String base_code = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97\u4e5f\u548c\u90a3\u8981\u4e0b\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1\u4e3a\u53c8\u53ef\u5bb6\u5b66\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b\u547d\u7ed9\u957f\u6c34\u51e0\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee\u4f46\u8eab\u65b9\u5b9e\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761\u767d\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f\u5148\u529b\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636\u8fde\u8f66\u91cd\u4fbf\u6597\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3\u767e\u539f\u62ff\u7fa4\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750\u63a5\u5e94\u5173\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f\u51c6\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4\u9ed1\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5\u9645\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";
			return base_code.charAt(new Random().nextInt(base_code.length())) + "";
		}
		String str = "";
		int hightPos;
		int lowPos;
		Random random = new Random();
		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();
		try {
			str = new String(b, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			str = String.valueOf(createRandomENG());
		}
		return str;
	}

	private static char createRandomENG() {
		Random r = new Random();
		String s = "ABCDEFGHJKMNPRSTUVWXYZ023456789";
		return s.charAt(r.nextInt(s.length()));
	}
}
