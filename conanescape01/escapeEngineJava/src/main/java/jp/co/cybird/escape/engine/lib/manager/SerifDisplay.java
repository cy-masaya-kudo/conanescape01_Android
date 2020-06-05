package jp.co.cybird.escape.engine.lib.manager;

import jp.co.cybird.escape.engine.lib.object.TextSet;
import jp.co.cybird.escape.engine.lib.object.event.EventCharacter;

/**
 * セリフ表示制御クラス
 */
public class SerifDisplay implements OnTimerDoneOnUIThread {
	int index = 0;
	TextSet textSet = null;
	String str = null;
	EventCharacter chr = null;

	/** set the TextSet */
	public void init(TextSet ts) {
		textSet = ts;
		index = 0;
		chr = null;
		str = ts.getString(TextSet.getLanguage());
	}

	/** 表示スタート */
	public void start(GameManagerBase manager) {
		index = 0;
		display(manager);
		manager.startTimerDoneOnUIThread(TextSet.SHOW_SERIF_DELAYTIME, this);
	}

	/** 全表示 */
	public void setDiaplayAll(GameManagerBase manager) {
		index = str.length() - 1;
		display(manager);
	}

	/** 表示処理 */
	void display(GameManagerBase manager) {
		// 次に表示する文字が"<"だったら<br>タグかチェックする
		int num = index + 4;
		if (num > str.length())
			num = str.length();
		String next = str.substring(index, num);
		if (next.equals("<br>")) {
			index += 4;
		}
		String s = str.substring(0, index + 1);
		boolean isEnd = (index + 1) >= str.length() ? true : false;
		if (chr == null) {
			chr = manager.getCharacter(textSet.getCharacterID());
		}
		if (manager.isSerifShowing()) {
			// セリフ表示中
			manager.drawText(s, chr, isEnd);
		} else if (manager.isEventRunning()) {
			// イベント中
			manager.drawEventText(str, textSet.getColor(), chr, isEnd);
		} else {
			// ヒント表示中
			manager.drawHint(s, chr, isEnd);
		}
	}

	@Override
	public void onTimerDone(GameManagerBase manager) {
		if (++index >= str.length()) {
			// 終了
			return;
		}
		display(manager);
		manager.startTimerDoneOnUIThread(TextSet.SHOW_SERIF_DELAYTIME, this);
	}

	/** 全文字が表示終了しているかチェック */
	public boolean isEnd() {
		if (str == null)
			return true;
		if (index >= str.length()) {
			return true;
		}
		return false;
	}

	/** 終了をセット */
	public void setEnd(boolean flag) {
		if (str == null) {
			index = 0;
		} else {
			index = str.length();
		}
	}
}
