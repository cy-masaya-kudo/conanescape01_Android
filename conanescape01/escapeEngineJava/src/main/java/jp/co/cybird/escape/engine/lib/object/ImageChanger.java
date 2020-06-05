package jp.co.cybird.escape.engine.lib.object;

/**
 * 画像変更可能オブジェクトクラス
 * 
 * @author S.Kamba
 * 
 */
public class ImageChanger extends Controllable {

	/** 画像セット */
	ImageSet mImageSet = null;
	/** 現在の画像ID */
	int mActiveImageId = 0;

	/** @return the image array */
	public ImageSet getImageSet() {
		return mImageSet;
	}

	/**
	 * @param imageList
	 *            the imageList to set
	 */
	public void setImageSet(ImageSet set) {
		mImageSet = set;
	}

	/**
	 * 画像indexを指定して画像を取得
	 * 
	 * @param index
	 *            取得したい画像のindex
	 * @return 画像ファイル名
	 */
	public String getImage(int index) {
		if (index < 0)
			index = mActiveImageId;
		if (mImageSet == null)
			return null;
		return mImageSet.getImage(index);
	}

	/** @return the imageId */
	public int getActiveImageId() {
		return mActiveImageId;
	}

	/**
	 * @param imageId
	 *            the imageId to set
	 */
	public void setActiveImageId(int imageId) {
		this.mActiveImageId = imageId;
	}

}
