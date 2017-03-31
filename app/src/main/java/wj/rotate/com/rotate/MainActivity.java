package wj.rotate.com.rotate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by weijiang on 3/31/17.
 */
public class MainActivity extends Activity implements ViewEditListener, View.OnClickListener {

    private EmojiImageView mEmojiImageView;
    private ScaleEditText mScaleEditText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmojiImageView = (EmojiImageView)findViewById(R.id.edit_emoji_view);
        mScaleEditText = (ScaleEditText)findViewById(R.id.edit_text);
        mButton = (Button)findViewById(R.id.switch_text_mode);
        mButton.setOnClickListener(this);
        mEmojiImageView.addObserver(this);
        mScaleEditText.addObserver(this);
        mScaleEditText.clearFocus();
        mScaleEditText.setCursorVisible(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEmojiImageView.removeObserver();
        mScaleEditText.removeObserver();
    }

    private int mEditType = -1;
    @Override
    public void onViewEdit(int type) {
        mEditType = type;
        setViewOrder();
    }

    private void setViewOrder() {
        if (mEditType == ViewEditListener.TYPE_CAPTION){
            mScaleEditText.bringToFront();
        } else if (mEditType == ViewEditListener.TYPE_IMAGE){
            mEmojiImageView.bringToFront();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_text_mode:
                mScaleEditText.switchEditStatus();
                break;
        }
    }
}
