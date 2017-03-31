/*
 * Copyright (C) 2016 Get Remark
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wj.rotate.com.rotate;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by weijiang on 3/31/17.
 */
public class ScaleEditText extends EditText implements GestureDetector.OnGestureListener {

    private GestureDetectorCompat gestureDetectorCompat;
    private boolean mEditStatus = false;
    private MultiTouchHelper mMultiTouchHelper;

    public ScaleEditText(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public ScaleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public ScaleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        gestureDetectorCompat = new GestureDetectorCompat(context, this);
        mMultiTouchHelper = new MultiTouchHelper(this);
        setDrawingCacheEnabled(true);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mViewEditListener != null) {
            mViewEditListener.onViewEdit(ViewEditListener.TYPE_CAPTION);
        }
        if (mEditStatus) {
            return super.onTouchEvent(ev);
        }
        gestureDetectorCompat.onTouchEvent(ev);
        return mMultiTouchHelper.onTouch(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private void restoreParams() {
        if (mEditStatus) {
            mMultiTouchHelper.reset();
        } else {
            mMultiTouchHelper.resumeToLastStatus();
        }
    }

    ViewEditListener mViewEditListener;

    public void addObserver(ViewEditListener viewEditListener){
        this.mViewEditListener = viewEditListener;
    }

    public void removeObserver() {
        this.mViewEditListener = null;
    }

    public void switchEditStatus() {
        mEditStatus = !mEditStatus;
        if (mEditStatus) {
            requestFocus();
            setCursorVisible(true);
        } else {
            clearFocus();
            setCursorVisible(false);
        }
        restoreParams();
    }

}
