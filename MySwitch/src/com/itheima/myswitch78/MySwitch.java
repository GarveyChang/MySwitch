package com.itheima.myswitch78;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 开发流程:
 * 1. 重写onDraw,绘制矩形
 * 2. 重写onMeasure,修改宽高
 * 3. 绘制滑块图片
 * 4. 设置滑块点击事件
 * 5. 设置回调监听
 * 6. 设置触摸移动事件
 * 7. 点击和触摸移动同时存在
 * 8, 自定义属性
 * 
 * @author Kevin
 * @date 2015-12-16
 */
public class MySwitch extends View {

	private Paint mPaint;
	private Bitmap mBitmapBg;
	private Bitmap mBitmapSlide;
	private int MAX_LEFT;

	private int mSlideLeft;// 当前左边距

	private boolean isOpen = false;// 标记当前开关状态

	public MySwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MySwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

		// 初始化起始开关状态
		isOpen = attrs.getAttributeBooleanValue(
				"http://schemas.android.com/apk/res-auto", "isOpen", false);
		if (isOpen) {
			mSlideLeft = MAX_LEFT;
		} else {
			mSlideLeft = 0;
		}

		// 初始化滑块图片
		int slide = attrs.getAttributeResourceValue(
				"http://schemas.android.com/apk/res-auto", "slideDrawable", -1);
		if (slide > 0) {
			// 初始化滑块图片
			mBitmapSlide = BitmapFactory.decodeResource(getResources(), slide);
		}

		// 重绘界面
		invalidate();
	}

	public MySwitch(Context context) {
		super(context);
		init();
	}

	private void init() {
		// 画笔
		mPaint = new Paint();
		mPaint.setColor(Color.RED);// 设置画笔颜色

		// 初始化滑块背景图片
		mBitmapBg = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_background);
		// 初始化滑块图片
		mBitmapSlide = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_button);

		// 计算最大左边距
		MAX_LEFT = mBitmapBg.getWidth() - mBitmapSlide.getWidth();

		// 设置点击事件
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isClick) {
					if (isOpen) {
						isOpen = false;
						mSlideLeft = 0;
					} else {
						isOpen = true;
						mSlideLeft = MAX_LEFT;
					}

					invalidate();// 重绘/刷新控件, 此方法会使系统重新调用onDraw方法

					// 3. 方法回调
					if (mListener != null) {
						mListener.onCheckChanged(MySwitch.this, isOpen);
					}
				}
			}
		});
	}

	// 测量控件宽高的回调
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// setMeasuredDimension(50, 50);// 将控件尺寸改为50x50
		// 控件宽高和图片宽高一致
		setMeasuredDimension(mBitmapBg.getWidth(), mBitmapBg.getHeight());
		// System.out.println("onMeasure");
	}

	// 系统控件绘制流程: measure(测量宽高)->layout(设置位置)->draw(绘制)
	// onMeasure->onLayout->onDraw
	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制矩形 200x200
		// canvas.drawRect(0, 0, 200, 200, mPaint);
		// System.out.println("onDraw");
		canvas.drawBitmap(mBitmapBg, 0, 0, null);
		canvas.drawBitmap(mBitmapSlide, mSlideLeft, 0, null);
	}

	int startX;
	int moveX;// 总的移动距离
	boolean isClick;// 标记当前是否是点击

	// 重写触摸监听
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 1. 获取起始坐标点
			startX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			// 2. 获取移动后的坐标
			int newX = (int) event.getX();

			// 3. 计算偏移量
			int dx = newX - startX;

			moveX += Math.abs(dx);

			// 4. 根据偏移量来更新控件位置
			mSlideLeft += dx;

			// 避免超出边界
			if (mSlideLeft < 0) {
				mSlideLeft = 0;
			}
			// 避免超出边界
			if (mSlideLeft > MAX_LEFT) {
				mSlideLeft = MAX_LEFT;
			}

			// 重绘
			invalidate();

			// 5. 重新初始化起点坐标
			startX = (int) event.getX();
			break;
		case MotionEvent.ACTION_UP:
			// 判断当前是移动还是点击
			if (moveX > 5) {// 防止用户手抖动, 导致误判为移动, 所以大于5而不是0
				// 移动
				isClick = false;
			} else {
				// 点击
				isClick = true;
			}

			moveX = 0;

			if (!isClick) {
				// 根据当前滑块位置决定最终滑块状态
				if (mSlideLeft < MAX_LEFT / 2) {
					mSlideLeft = 0;
					isOpen = false;
				} else {
					mSlideLeft = MAX_LEFT;
					isOpen = true;
				}

				invalidate();

				// 3. 方法回调
				if (mListener != null) {
					mListener.onCheckChanged(MySwitch.this, isOpen);
				}
			}
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);// 必须返回super,
											// 可以保证onTouchEvent和onClick同时响应
	}

	private OnCheckChangeListener mListener;

	/**
	 * 2. 设置回调监听
	 */
	public void setOnCheckChangeListener(OnCheckChangeListener listener) {
		mListener = listener;
	}

	/**
	 * 1. 定义一个回调接口
	 */
	public interface OnCheckChangeListener {
		public void onCheckChanged(View view, boolean isOpen);
	}

}
