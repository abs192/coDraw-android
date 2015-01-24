package com.abs192.codraw.util;

import java.util.Random;

public class RandomColor {

	public static int allColors[] = { 0xff111111, 0xff33B5E5, 0xffAA66CC,
			0xff99CC00, 0xffFFBB33, 0xffFF4444 };

	public static int nextColor() {
		return allColors[new Random().nextInt(allColors.length)];
	}
}
