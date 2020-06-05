package jp.co.cybird.android.conanescape01.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.anim.FadeAnim;
import jp.co.cybird.android.conanescape01.anim.FlickLeftAnim;
import jp.co.cybird.android.conanescape01.anim.FlickRightAnim;
import jp.co.cybird.android.conanescape01.anim.ViewAnimController;
import jp.co.cybird.android.conanescape01.anim.ZoomAnim;
import jp.co.cybird.android.conanescape01.webapi.WebapiPointDelivery;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.CollisionView;
import jp.co.cybird.android.escape.util.ImageViewUtil;
import jp.co.cybird.android.escape.util.LayerView;
import jp.co.cybird.android.util.Debug;
import jp.co.cybird.escape.engine.lib.action.Action.MoveType;
import jp.co.cybird.escape.engine.lib.manager.OnActiveChangeCallback;
import jp.co.cybird.escape.engine.lib.manager.OnDrawCallback;
import jp.co.cybird.escape.engine.lib.manager.OnEventCallback;
import jp.co.cybird.escape.engine.lib.manager.OnSEPlayCallback;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.event.Event;
import jp.co.cybird.escape.engine.lib.object.event.EventCharacter;
import jp.co.cybird.escape.engine.lib.object.item.Item;

public class EngineCallback implements OnDrawCallback, OnActiveChangeCallback,
		OnSEPlayCallback, OnEventCallback {

	public interface EngineEventListener {
		public void setAnimationFlag(boolean animating);

		public View getItemActiveCellView(int index);

		public void clearActiveCell();

		public boolean isPlaySE();

	}

	EngineEventListener listener = null;
	Context context = null;
	GameManager gm = null;

	/** root */
	View root_view = null;

	/** node表示ビュー */
	ImageView nodeView = null;

	/** アイテム欄ルートビュー */
	View itemsLayout = null;
	/** アイテム拡大表示用ビュー */
	View itemZoomView = null;

	/** for Debug:当たり判定矩形表示用のビュー */
	CollisionView mCollisionView = null;
	CollisionView mItemCollisionView = null;

	/** node遷移アニメーション */
	// ImageView animView = null;
	FlickLeftAnim leftAnim = null;
	FlickRightAnim rightAnim = null;
	ZoomAnim zoomAnim = null;
	FadeAnim fadeAnim = null;

	/** Saveボタン */
	ImageButton saveButton = null;
	/** Hintボタン */
	ImageButton hintButton = null;

	/** effectビュー */
	View effectView = null;
	boolean enableClick = true;

	/** serif triangle image */
	View serifTriangle = null;

	public EngineCallback(Context c, EngineEventListener l, View root_view,
			GameManager gm) {
		listener = l;
		this.gm = gm;
		context = c;
		this.root_view = root_view;

		nodeView = (ImageView) root_view.findViewById(R.id.nodeImage);

		itemZoomView = root_view.findViewById(R.id.itemview_root);
		itemZoomView.setVisibility(View.INVISIBLE);
		mCollisionView = (CollisionView) root_view
				.findViewById(R.id.collisionView);
		mItemCollisionView = (CollisionView) root_view
				.findViewById(R.id.item_collisionView);
		if (Debug.showCollision) {
			mCollisionView.setVisibility(View.VISIBLE);
			mItemCollisionView.setVisibility(View.VISIBLE);
		} else {
			mCollisionView.setVisibility(View.INVISIBLE);
			mItemCollisionView.setVisibility(View.INVISIBLE);
		}

		itemsLayout = root_view.findViewById(R.id.itemsLayout);
		initNodeAnimation(root_view);

		saveButton = (ImageButton) root_view.findViewById(R.id.btn_save);
		hintButton = (ImageButton) root_view.findViewById(R.id.btn_hint);
		effectView = root_view.findViewById(R.id.effectview);

		serifTriangle = root_view.findViewById(R.id.img_triangle);
	}

	public void delete() {
		gm = null;
		leftAnim.delete();
		rightAnim.delete();
		zoomAnim.delete();
		fadeAnim.delete();
	}

	@Override
	public void onActiveNodeChanged(MoveType type) {

		if (type == null) {
			gm.draw();
		} else if (type == MoveType.FLICK_LEFT) {
			startNodeAnimation(leftAnim);
		} else if (type == MoveType.FLICK_RIGHT) {
			startNodeAnimation(rightAnim);
		} else if (type == MoveType.MOVE_ZOOM) {
			startNodeAnimation(zoomAnim);
		} else if (type == MoveType.MOVE_FADE) {
			startNodeAnimation(fadeAnim);
		} else {
			gm.draw();
		}
	}

	@Override
	public void onActiveItemChanged() {
		Item item = gm.getActiveItem();
		if (item == null) {
			listener.clearActiveCell();
		}
		// nullじゃないときはonClickItemを経由しているはずなので特に処理しない
	}

	@Override
	public void onDrawCallback(Node node) {
		String filename = gm.getImagePath() + node.getImage(-1);
		if (filename != null) {
			ImageViewUtil.setImageBitmap(nodeView, filename, 1);
		}
		// layerを描画
		drawLayer(node.getChildren());

		// debug用当たり判定表示
		if (!Debug.showCollision) {
			return;
		}

		// 判定矩形を描画
		mCollisionView.setActiveNode(node);
		mCollisionView.invalidate();
	}

	/** Layerを描画 */
	void drawLayer(ArrayList<Node> layers) {
		LayerView lv = (LayerView) root_view.findViewById(R.id.layer_view);
		lv.setVisibility(View.VISIBLE);
		lv.setGameManager(gm);
		lv.setLayerList(layers);
		lv.invalidate();
	}

	@Override
	public void onDrawItemThumb(int index, Node item) {
		if (index >= gm.getItemAreaNum()) {
			return;
		}
		// アイテム欄のアイテムを描画
		int res_id = getImageResourceId(index);
		ImageView img = (ImageView) itemsLayout.findViewById(res_id);

		if (item == null) {
			// 非表示
			img.setImageBitmap(null);
			img.setTag(R.id.TAG_FILE_NAME, null);
			Bitmap oldbmp = (Bitmap) img.getTag(R.id.TAG_BITMAP);
			if (oldbmp != null) {
				oldbmp.recycle();
			}
			img.setTag(R.id.TAG_BITMAP, null);// クリア
			img.setTag(R.id.TAG_ITEM, null);// クリア
			View acv = listener.getItemActiveCellView(index);
			acv.setVisibility(View.VISIBLE);
			acv = getItemCellLayer(index);
			acv.setVisibility(View.INVISIBLE);
			return;
		}
		View frame = getItemCellLayer(index);
		frame.setVisibility(View.VISIBLE);
		// itemオブジェクトを保存
		img.setTag(R.id.TAG_ITEM, item);
		// ファイル名を取得
		String filename = gm.getImagePath() + item.getImage(-1);
		// ImageViewにセット
		ImageViewUtil.setImageBitmap(img, filename, 8); // サムネイルなのでサンプリングサイズを調整
		// アクティブアイテムだったら枠を表示
		if (gm.getActiveItem() == item) {
			View acv = listener.getItemActiveCellView(index);
			acv.setVisibility(View.INVISIBLE);
		}
	}

	private View getItemCellLayer(int index) {
		String name = String.format("lay_item%02d", (index + 1));
		int resid = context.getResources().getIdentifier(name, "id",
				context.getPackageName());
		View frame = root_view.findViewById(resid);
		return frame;
	}

	@Override
	public void onDrawText(String str, EventCharacter character, boolean isEnd) {
		View serif_root = root_view.findViewById(R.id.serif_root);
		if (str == null || str.length() == 0) {
			// 非表示
			serif_root.setVisibility(View.INVISIBLE);
			return;
		}
		// 表示
		serif_root.setVisibility(View.VISIBLE);

		// 文字をセット
		TextView tv = (TextView) root_view.findViewById(R.id.textview_serif);
		CharSequence cs = Html.fromHtml(str);
		tv.setText(cs);

		// キャラ表示
		ImageView iv = (ImageView) root_view.findViewById(R.id.img_chara);
		if (character == null) {
			iv.setVisibility(View.INVISIBLE);
		} else {
			iv.setVisibility(View.VISIBLE);
			// 専用ImageViewの画像を貼り替え
			String filename = gm.getImagePath() + character.getImage(-1);
			ImageViewUtil.setImageBitmap(iv, filename, 1);

			// キャラ名称
			tv = (TextView) root_view.findViewById(R.id.textview_name);
			tv.setText(character.getName());
		}

		// 終了フラグ
		if (isEnd) {
			serifTriangle.setVisibility(View.VISIBLE);
		} else {
			serifTriangle.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onDrawHint(String str, EventCharacter character, boolean isEnd) {
		onDrawText(str, character, isEnd);
	}

	@Override
	public void onDrawItem(Node item) {
		itemZoomView.setVisibility(View.VISIBLE);

		ImageView img = (ImageView) root_view.findViewById(R.id.img_itemzoom);
		String filename = gm.getImagePath() + item.getImage(-1);
		if (filename != null) {
			ImageViewUtil.setImageBitmap(img, filename, 1);
		}
		// layerを描画
		drawItemLayer(item.getChildren());

		// debug用当たり判定表示
		if (!Debug.showCollision) {
			return;
		}

		// 判定矩形を描画
		mItemCollisionView.setActiveNode(item);
		mItemCollisionView.invalidate();
	}

	/** Layerを描画 */
	void drawItemLayer(ArrayList<Node> layers) {
		LayerView lv = (LayerView) root_view.findViewById(R.id.item_layerView);
		lv.setGameManager(gm);
		lv.setLayerList(layers);
		lv.invalidate();
	}

	@Override
	public void onSEPlay(SEType se_type) {
		// 音を鳴らす
		if (listener.isPlaySE()) {
			if (se_type.equals(SEType.ITEM_GET)) {
				SoundManager.getInstance().playItemSE();
			} else if (se_type.equals(SEType.ITEM_SELECT)) {
				SoundManager.getInstance().playItemSelectSE();
			} else if (se_type.equals(SEType.ITEM_ZOOM)) {
				SoundManager.getInstance().playItemZoomSE();
			} else if (se_type.equals(SEType.ZOOM_END)) {
				SoundManager.getInstance().playZoomEndSE();
			} else if (se_type.equals(SEType.ITEM_COMBINE)) {
				SoundManager.getInstance().playItemCombineSE();
			} else if (se_type.equals(SEType.SERRIF_PROCESS)) {
				SoundManager.getInstance().playSerifSE();
			}
		}
		//
	}

	@Override
	public void onDrawEvent(Event event) {
		String img_name = event.getImage(-1);
		if (img_name != null) {
			String img_filename = gm.getImagePath() + event.getImage(-1);
			ImageViewUtil.setImageBitmap(nodeView, img_filename, 1);

			LayerView lv = (LayerView) root_view.findViewById(R.id.layer_view);
			lv.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onDrawEventText(String str, int color,
			EventCharacter character, boolean isEnd) {
		onDrawText(str, character, isEnd);
	}

	/** node移動アニメーション用 */
	void initNodeAnimation(View root) {
		// animView = (ImageView) root.findViewById(R.id.animImage);
		View nodeRoot = root.findViewById(R.id.nodeRoot);
		View animRoot = root.findViewById(R.id.animRoot);
		leftAnim = new FlickLeftAnim(listener, gm, nodeRoot, animRoot);
		rightAnim = new FlickRightAnim(listener, gm, nodeRoot, animRoot);
		zoomAnim = new ZoomAnim(listener, gm, nodeRoot, animRoot);
		fadeAnim = new FadeAnim(listener, gm, nodeRoot, animRoot);
	}

	/** Node遷移アニメーション開始 */
	void startNodeAnimation(ViewAnimController anim) {
		listener.setAnimationFlag(true);
		anim.start();
	}

	@Override
	public void onDrawEventCoin(boolean flag) {
		// View img = root_view.findViewById(R.id.img_coin);
		// if (flag)
		// img.setVisibility(View.VISIBLE);
		// else
		// img.setVisibility(View.INVISIBLE);
	}

	/**
	 * アイテムリスト欄のImageViewのリソースidをインデクスから取得する
	 *
	 * @param index
	 *            ImageViewのインデクス(0〜15)
	 * @return リソースid
	 */
	private int getImageResourceId(int index) {
		String id_name = String.format(Locale.ENGLISH, "item%02d", index + 1);
		int res_id = context.getResources().getIdentifier(id_name, "id",
				context.getPackageName());
		return res_id;
	}

	@Override
	public void onFinishHint(String payloadString) {
		// コイン消費完了
		Debug.logD("コイン消費完了。　payloadString=" + payloadString);

		WebapiPointDelivery webapi = new WebapiPointDelivery(context);
		webapi.setPointTransaction(payloadString);
		webapi.execute(null);
	}

	@Override
	public void onFailHint(String payloadString) {
		// コイン消費しない
		Debug.logD("コイン消費未完了。　payloadString=" + payloadString);
	}

	@Override
	public void onStartEvent() {
		enableMenus(false);
	}

	@Override
	public void onFinishEvent() {
		enableMenus(true);
	}

	public void enableMenus(boolean flag) {
		saveButton.setEnabled(flag);
		hintButton.setEnabled(flag);
	}

	public class EffectAnimationFinishListener implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		Animation nextAnimation;

		public EffectAnimationFinishListener(Animation nextAnimation) {
			this.nextAnimation = nextAnimation;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (nextAnimation != null) {
				effectView.startAnimation(nextAnimation);
			} else {
				effectView.setVisibility(View.INVISIBLE);
				gm.setEffectFinished();
				enableClick = true;
			}
		}
	}

	@Override
	public void onDrawEffectFade(int color, long in_duration, long out_duration) {
		//
		effectView.setVisibility(View.VISIBLE);
		effectView.setBackgroundColor(color);
		AlphaAnimation in = new AlphaAnimation(0, 1);
		in.setDuration(in_duration);
		in.setFillBefore(true);
		AlphaAnimation out = new AlphaAnimation(1, 0);
		out.setDuration(out_duration);
		out.setFillAfter(true);
		out.setAnimationListener(new EffectAnimationFinishListener(null));
		in.setAnimationListener(new EffectAnimationFinishListener(out));
		effectView.startAnimation(in);
		enableClick = false;
	}

	@Override
	public void onDrawEffectVibration(long duration) {

		Animation anim = AnimationUtils.loadAnimation(context, R.anim.shake);
		long animtime = anim.getDuration();
		long repeatCount = duration / animtime;
		anim.setInterpolator(new CycleInterpolator(repeatCount));
		anim.setAnimationListener(new EffectAnimationFinishListener(null));
		root_view.setVisibility(View.VISIBLE);
		root_view.startAnimation(anim);
		enableClick = false;
	}
}
