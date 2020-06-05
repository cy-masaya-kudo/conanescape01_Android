package jp.co.cybird.android.escape.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.util.Debug;

public class ImageViewUtil {
	/**
	 * ImageViewにbitmapをセットする<br>
	 * 何度も同じBitmapを作成するのを防ぐ<br>
	 * また、サムネイルサイズ用に、サンプルサイズを指定できる
	 */
	static public void setImageBitmap(ImageView img, String filename,
			int sampleSize) {
		String tag = (String) img.getTag(R.id.TAG_FILE_NAME);
		if (filename.equals(tag)) // ファイル名が一緒なら変更しない
			return;
		img.setTag(R.id.TAG_FILE_NAME, filename);
		// 古いbitmapを解放
		Bitmap oldbmp = (Bitmap) img.getTag(R.id.TAG_BITMAP);
		if (oldbmp != null) {
			img.setTag(R.id.TAG_BITMAP, null);
			img.setImageBitmap(null);
			oldbmp.recycle();
			oldbmp = null;
		}
		// 新しいbitmapに設定
		Bitmap bmp = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		try {
			bmp = BitmapFactory.decodeFile(filename, options);
		} catch (OutOfMemoryError e) {
			// Toast.makeText(img.getContext(), "メモリーが足りません。",
			// Toast.LENGTH_SHORT)
			// .show();
			Debug.logD("Try System.gc()");
			try {
				System.gc();
				bmp = BitmapFactory.decodeFile(filename, options);
			} catch (OutOfMemoryError e2) {
				Debug.logD("OutOfMemoryError!!!!");
				e2.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bmp != null) {
			img.setImageBitmap(bmp);
			img.setTag(R.id.TAG_BITMAP, bmp);
		}
	}
}
