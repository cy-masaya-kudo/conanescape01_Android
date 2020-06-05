package jp.co.cybird.escape.engine.lib.data;

/**
 * パスワード付きzipを解凍するためのコールバックリスナ
 * 
 * @author S.Kamba
 *
 */
public interface OnDecodeZipListener {

	public boolean onDecodeZip(String zipfile, String dstDir);
}
