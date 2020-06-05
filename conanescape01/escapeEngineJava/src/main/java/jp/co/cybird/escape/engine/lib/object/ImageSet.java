package jp.co.cybird.escape.engine.lib.object;

import java.util.ArrayList;

/**
 * ImageSetクラス
 * 
 * @author S.Kamba
 * 
 */
public class ImageSet extends ESCObject {
	/** 画像リスト */
	ArrayList<String> mImages = null;

	public ImageSet() {
	}

	/**
	 * @return the image array
	 */
	public ArrayList<String> getImageSet() {
		return mImages;
	}

	/**
	 * @param imageList
	 *            the imageList to set
	 */
	public void setImages(ArrayList<String> imageList) {
		mImages = imageList;
	}

	/**
	 * @param path
	 *            the path to add
	 */
	public void addImage(String path) {
		if (mImages == null) {
			mImages = new ArrayList<String>();
		}
		mImages.add(path);
	}

	/**
	 * 画像indexを指定して画像を取得
	 * 
	 * @param index
	 *            取得したい画像のindex
	 * @return 画像ファイル名
	 */
	public String getImage(int index) {
		if (mImages == null || mImages.size() <= index) {
			return null;
		}
		return mImages.get(index);
	}
}
