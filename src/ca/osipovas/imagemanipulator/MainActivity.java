package ca.osipovas.imagemanipulator;

import java.util.List;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector.BaseListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity {
	private ImageView imageView;

	private com.google.android.glass.touchpad.GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setScaleType(ScaleType.MATRIX); // Needed for manipulation
													// options, can be moved to
													// xml
		mGestureDetector = createGestureDetector(this);
		// displaySpeechRecognizer();
	}

	/*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
    
	private com.google.android.glass.touchpad.GestureDetector createGestureDetector(
			Context context) {
		com.google.android.glass.touchpad.GestureDetector gestureDetector = new com.google.android.glass.touchpad.GestureDetector(context);
		 gestureDetector.setBaseListener( new BaseListener() {
	            @Override
	            public boolean onGesture(Gesture gesture) {
	                if (gesture == Gesture.TAP) {
	                	Log.i("Gesture", "TAP");
	            		displaySpeechRecognizer();
	                    return true;
	                } else if (gesture == Gesture.TWO_TAP) {
	                	Log.i("Gesture", "TWO TAP");
	            		//displaySpeechRecognizer();
	                    return true;
	                } else if (gesture == Gesture.SWIPE_RIGHT) {
	                	Log.i("Gesture", "Swipe Right/Forward");
	            		//displaySpeechRecognizer();
	                    return true;
	                } else if (gesture == Gesture.SWIPE_LEFT) {
	                	Log.i("Gesture", "Swipe Left/Backwards");
	                    return true;
	                }
	                return false;
	            }

			
	        });
		return gestureDetector;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private static final int SPEECH_REQUEST = 0;

	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			Log.i("Speech", spokenText);
			if (spokenText != null) {
				interpretText(spokenText);

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final String ROTATE_COMMAND = "rotate";
	private final String FLIP_COMMAND = "flip";
	private final int FLIP_HORIZONTAL = 1;
	private final int FLIP_VERTICAL = 2;

	/**
	 * 
	 * @param spokenText
	 * 
	 *            A string of the spoken text the user just spoke. Upon matching
	 *            a predetermined pattern
	 */
	private void interpretText(String spokenText) {
		// rotate 90 degrees
		// rotate 180 degrees
		// rotate 270 degrees
		// flip horizontal
		// flip vertical
		String[] splitText = spokenText.split("\\s+");
		String command = null;
		String parameter = null;
		if (splitText != null && splitText.length > 2) {
			Log.i("Splitting Text", "splitTextLength: " + splitText.length);
			command = splitText[0];
			parameter = splitText[1];

			if (command.equalsIgnoreCase(ROTATE_COMMAND)) {

				Integer rotationAngle = Integer.parseInt(parameter);
				switch (rotationAngle) {
				case 90:
					rotateImage(rotationAngle);
					break;
				case 180:
					rotateImage(rotationAngle);
					break;
				case 270:
					rotateImage(rotationAngle);
				default:
					// The spoken parameter was not a valid rotation Angle
					// React accordingly
					break;
				}
			}

			else if (command.equalsIgnoreCase(FLIP_COMMAND)) {

				if (parameter.equalsIgnoreCase("horizontal")) {
					flipImage(FLIP_HORIZONTAL);
				} else if (parameter.equalsIgnoreCase("vertical")) {
					flipImage(FLIP_VERTICAL);
				} else {
					// Act Accordingly
				}
			}
		}

	}

	private void flipImage(int FLIP_ORIENTATION) {
		Matrix matrix = new Matrix(imageView.getImageMatrix());
		if (FLIP_ORIENTATION == FLIP_HORIZONTAL) {
			matrix.setScale(-1.0f, 1.0f);
	
		} else if (FLIP_ORIENTATION == FLIP_VERTICAL) {
			matrix.setScale(1.0f, -1.0f);
		} else {
			// Invalid Parameter - Act Accordingly
		}
		Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
		imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false));

	}

	/**
	 * Rotates the imageView member at the center of the screen by
	 * 
	 * @param rotationAngle
	 */
	private void rotateImage(Integer rotationAngle) {
		Matrix matrix = new Matrix(imageView.getImageMatrix());
		matrix.postRotate((float) rotationAngle, imageView.getDrawable()
				.getBounds().width() / 2, imageView.getDrawable().getBounds()
				.height() / 2);
		imageView.setImageMatrix(matrix);
	}

}
