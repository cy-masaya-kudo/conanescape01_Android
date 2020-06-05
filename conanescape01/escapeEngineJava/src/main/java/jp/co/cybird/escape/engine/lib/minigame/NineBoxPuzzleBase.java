package jp.co.cybird.escape.engine.lib.minigame;

import java.util.ArrayList;

import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.object.MiniGame;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.Control.ControlType;
import jp.co.cybird.escape.engine.lib.object.layer.Layer;

/**
 * 9マスパズルゲーム
 *
 * @author S.Kamba
 *
 */
public abstract class NineBoxPuzzleBase implements MiniGameRunner {

	public static final int BOX_NUM = 9; // 9マス

	protected GameManagerBase gm = null;
	protected MiniGame game = null;

	protected BoxCell boxes[] = null;
	protected PartItem parts[] = null;

	protected boolean isTouching = false;
	protected PartItem dragObject = null;
	protected PuzzleObject tapped = null;

	protected boolean isInitilized = false;

	/** パズル内オブジェクト基本クラス */
	protected class PuzzleObject {
		/** 当たり判定と画像取得用レイヤ */
		public Layer layer = null;
		/** id */
		public int id;

		public void layerDispOff() {
			layer.flagOFF(Status.FLAG_DISP);
		}

		public void layerDispOn() {
			layer.flagON(Status.FLAG_DISP);
		}
	}

	/** マスクラス */
	protected class BoxCell extends PuzzleObject {
		/** セットされたパーツのid */
		public int putPartId = -1;
		/** 正解フラグ */
		public boolean isCorrect = false;
	}

	/** 移動パーツクラス */
	protected class PartItem extends PuzzleObject {
		/** 移動済みフラグ */
		public boolean isMoved = false;
	}

	public NineBoxPuzzleBase(GameManagerBase manager) {
		this.gm = manager;
	}

	/** ドラッグイメージをセットする */
	abstract protected void setDragImage(String filename);

	/** ドラッグイメージを描画する */
	abstract protected void drawDragImage(boolean dispFlag, int x, int y);

	/** パーツ正解時の効果音再生 */
	abstract protected void onPlayCorrectSound();

	/** 正解配列の取得 */
	abstract protected int[] getAnswers();

	protected void init(MiniGame game) {
		this.game = game;
		game.setRunner(this);
		if (boxes == null) {
			// 当たり判定を作成
			boxes = new BoxCell[BOX_NUM];
			for (int i = 0; i < BOX_NUM; i++) {
				boxes[i] = new BoxCell();
			}
			// Layerから取得
			ArrayList<Node> layers = game.getChildren();
			getCollisionsFromLayers(layers, 0, boxes);
		}
		if (parts == null) {
			// 当たり判定を作成
			parts = new PartItem[BOX_NUM];
			for (int i = 0; i < BOX_NUM; i++) {
				parts[i] = new PartItem();
			}
			// Layerから取得
			ArrayList<Node> layers = game.getChildren();
			getCollisionsFromLayers(layers, 9, parts);
		}
	}

	@Override
	public void run(GameManagerBase manager, MiniGame game) {
		if (!isInitilized) {
			init(game);
			String buf[] = game.getRestoreData();
			if (buf != null) {
				restoreFromSave(buf, game);
				game.setRestoreData(null);
			}
			isInitilized = true;
		}
	}

	/** Layerから当たり判定を生成する(座標をそのまま流用) */
	private void getCollisionsFromLayers(ArrayList<Node> layers,
			int startIndex, PuzzleObject[] collisions) {
		for (int i = 0; i < BOX_NUM; i++) {
			Layer l = (Layer) layers.get(i + startIndex);
			collisions[i].layer = l;
			collisions[i].id = i;
		}
	}

	protected void onTouchDown(int x, int y) {
		// ドラッグ開始
		// タッチ位置から該当Layerを取得
		tapped = findTouchedId(x, y);
		if (tapped == null)
			return;
		int draggingId = -1;

		// アイテム欄のタッチかマスのタッチか判定
		if (tapped instanceof BoxCell) {
			// マス目をタッチ
			BoxCell bc = (BoxCell) tapped;
			// if (bc.isCorrect) { // すでに正解の場所にあれば動かせない
			// return;
			// }
			if (game.isCleared()) { // すでに正解済みなら動かせない
				return;
			}
			if (bc.putPartId < 0) { // セットされたパーツがない
				return;
			}
			draggingId = bc.putPartId;
		} else {
			// アイテム欄をタッチ
			PartItem pi = (PartItem) tapped;
			if (pi.isMoved) {
				// 使用済みなら動かせない
				return;
			}
			draggingId = pi.id;
		}
		if (draggingId < 0)
			return;

		// タッチダウン処理
		isTouching = true;
		// 移動アイテム
		dragObject = parts[draggingId];
		// タップしたLayerを非表示
		tapped.layerDispOff();
		//
		// 画像名取得(Layerが持っている大きい方の画像を表示する)
		Layer l = boxes[0].layer; // 画像セットは共有なので固定
		// ImageViewに画像をセット
		String filename = gm.getImagePath() + l.getImage(draggingId);
		setDragImage(filename);
		drawDragImage(true, x, y);
		//
		gm.draw();

	}

	protected void onTouchMove(int x, int y) {
		if (isTouching) {
			drawDragImage(true, x, y);
		}
	}

	protected void onTouchUp(int x, int y) {
		if (isTouching) {
			// タッチアップ処理
			// 一番近いマスの位置を取得
			int dropId = findDropId(x, y);
			if (dropId >= 0) {
				// すでに置かれていないかチェック
				BoxCell bc = boxes[dropId];
				if (bc.putPartId != -1 && dragObject.id != bc.putPartId) {
					dropId = -1;
				}
			}
			// 近いマスが無ければ元の位置に戻す
			if (dropId < 0) {
				if (tapped instanceof BoxCell) {
					BoxCell bc = (BoxCell) tapped;
					// パーツ欄に戻す
					PartItem pi = parts[bc.putPartId];
					pi.isMoved = false;
					pi.layerDispOn();
					bc.putPartId = -1;
				} else {
					tapped.layerDispOn();
				}
				dragObject.isMoved = false;
			} else {
				if (tapped instanceof BoxCell) {
					// 古い方をクリア
					BoxCell old = (BoxCell) tapped;
					old.putPartId = -1;
				}
				// マスの位置の画像idを変更
				dragObject.isMoved = true;
				BoxCell bc = boxes[dropId];
				bc.putPartId = dragObject.id;
				bc.layerDispOn();
				bc.layer.setActiveImageId(dragObject.id);
				// 正解かチェック
				if (checkCorrect(bc)) {
					// 正解なら音を鳴らす
					bc.isCorrect = true;
					onPlayCorrectSound();
					if (checkAllCorrect()) {
						onClear();
					}
				}
			}
			// Dragイメージを非表示
			drawDragImage(false, 0, 0);
			// Drag情報解除
			dragObject = null;
			tapped = null;

			isTouching = false;

			gm.draw();
		}
	}

	protected void onClear() {
		// クリア
		game.runControll(gm, ControlType.CLEAR_MINIGAME, 0, 0);
		game.setClearFlag(true);
		gm.miniGameStop();
	}

	/** 当たり判定：タッチ位置にあるレイヤーを検索 */
	PuzzleObject findTouchedId(int x, int y) {
		int i;
		for (i = 0; i < BOX_NUM; i++) {
			Layer l = boxes[i].layer;
			if (l.isHit(x, y)) {
				return boxes[i];
			}
		}
		for (i = 0; i < BOX_NUM; i++) {
			Layer l = parts[i].layer;
			if (l.isHit(x, y)) {
				return parts[i];
			}
		}
		return null;
	}

	/** 当たり判定：ドロップ位置にあるレイヤーを検索 */
	int findDropId(int x, int y) {
		int i;
		for (i = 0; i < BOX_NUM; i++) {
			Layer l = boxes[i].layer;
			if (l.isHit(x, y)) {
				return i;
			}
		}
		return -1;
	}

	/** 位置が正しいかチェック */
	protected boolean checkCorrect(BoxCell bc) {
		//
		final int[] answer = getAnswers();
		return (bc.putPartId == answer[bc.id]);
	}

	/** すべて正しい位置に納まったかチェック */
	protected boolean checkAllCorrect() {
		for (BoxCell bc : boxes) {
			if (!bc.isCorrect)
				return false;
		}
		return true;
	}

	@Override
	public String getSaveString() {
		//
		StringBuffer sb = new StringBuffer();
		for (BoxCell bc : boxes) {
			sb.append(bc.putPartId).append(",");
		}
		for (PartItem pi : parts) {
			sb.append(pi.isMoved ? -1 : pi.id).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public void restoreFromSave(String buf[], MiniGame game) {
		int j = 0;
		for (int i = 0; i < BOX_NUM; i++, j++) {
			boxes[i].putPartId = Integer.valueOf(buf[j]);
			boxes[i].isCorrect = checkCorrect(boxes[i]);
		}
		for (int i = 0; i < BOX_NUM; i++, j++) {
			parts[i].isMoved = buf[j].equals("-1");
		}
	}
}
