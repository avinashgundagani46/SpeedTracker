package speedtracker.gps.com.speedtracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Iterator;

public class CustomSpeedGraph extends View {
	private Paint green = new Paint();
	private Paint red = new Paint();
	private Paint white = new Paint();
	private Context mContext;
	private int widthOfScreen;

	public CustomSpeedGraph(Context context) {
		super(context);
		init(context);
	}

	public CustomSpeedGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomSpeedGraph(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		WindowManager wm = (WindowManager) mContext
				.getSystemService(mContext.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		widthOfScreen = display.getWidth();
		white.setColor(Color.WHITE);
		white.setStrokeWidth(3);
		//white.setStyle(Paint.Style.STROKE);
		red.setColor(Color.RED);
		red.setStrokeWidth(3);
		green.setColor(Color.GREEN);
		green.setStrokeWidth(3);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Dividing canvas into six equal parts
		int heightOfRow = widthOfScreen / 6;
		// Initial position to draw graph
		int xStart = 0;
		int yStart = widthOfScreen;
		int xStop = widthOfScreen / 100;
		int yStop;
		int pixels = widthOfScreen / 60;

		// Drawing 5 lines in equal distances
		canvas.drawLine(0, heightOfRow * 1, widthOfScreen, heightOfRow * 1, white);
		canvas.drawLine(0, heightOfRow * 2, widthOfScreen, heightOfRow * 2, white);
		canvas.drawLine(0, heightOfRow * 3, widthOfScreen, heightOfRow * 3, white);
		canvas.drawLine(0, heightOfRow * 4, widthOfScreen, heightOfRow * 4, white);
		canvas.drawLine(0, heightOfRow * 5, widthOfScreen, heightOfRow * 5, white);

		// Drawing the average speed with red paint
		int averageSpeed = widthOfScreen - MainActivity.averageSpeed * pixels;
		canvas.drawLine(0, averageSpeed, widthOfScreen, averageSpeed, red);

		// To start graph at the exact position to show the graph is moving horizontally
		if(MainActivity.speed.size() == 100){
			yStart = widthOfScreen - (int)MainActivity.speed.getFirst() * pixels;
		}

		// Iterator to draw graph of last 100 positions
		Iterator<Integer> speedIterator = MainActivity.speed.iterator();
		while (speedIterator.hasNext()) {
			int actualSpeed = speedIterator.next();
			yStop = widthOfScreen - actualSpeed * pixels;
			canvas.drawLine(xStart, yStart, xStop, yStop, green);
			xStart = xStop;
			yStart = yStop;
			xStop = xStop + (widthOfScreen / 100);
		}
		super.onDraw(canvas);
	}
}
