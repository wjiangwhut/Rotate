/*
 * Copyright (C) 2017 Get Remark
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

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by weijiang on 3/31/17.
 */
public class MultiTouchHelper {

    private static final int INVALID_POINTER_ID = -1;
    public boolean isRotateEnabled = true;
    public boolean isTranslateEnabled = true;
    public boolean isScaleEnabled = true;
    public float minimumScale = 0.6f;
    public float maximumScale = Float.MAX_VALUE;

    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;

    private Transform mLastTransform = new Transform();
    private View mTargetView;

    public MultiTouchHelper(View targetView) {
        this.mTargetView = targetView;
        this.mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    }

    private float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private void move(TransformInfo info) {
        computeRenderOffset(info.pivotX, info.pivotY);
        adjustTranslation(info.deltaX, info.deltaY);

        // Assume that scaling still maintains aspect ratio.
        float scale = mTargetView.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        mTargetView.setScaleX(scale);
        mTargetView.setScaleY(scale);

        float rotation = adjustAngle(mTargetView.getRotation() + info.deltaAngle);
        mTargetView.setRotation(rotation);
        mLastTransform.rotate = rotation;
        mLastTransform.scale = scale;
    }

    private void adjustTranslation(float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        mTargetView.getMatrix().mapVectors(deltaVector);
        float translationX = mTargetView.getTranslationX() + deltaVector[0];
        float translationY = mTargetView.getTranslationY() + deltaVector[1];
        mTargetView.setTranslationX(translationX);
        mTargetView.setTranslationY(translationY);
        mLastTransform.translationX = translationX;
        mLastTransform.translationY = translationY;
    }

    private void computeRenderOffset(float pivotX, float pivotY) {
        if (mTargetView.getPivotX() == pivotX && mTargetView.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        mTargetView.getMatrix().mapPoints(prevPoint);

        mTargetView.setPivotX(pivotX);
        mTargetView.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        mTargetView.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        mTargetView.setTranslationX(mTargetView.getTranslationX() - offsetX);
        mTargetView.setTranslationY(mTargetView.getTranslationY() - offsetY);
    }

    public boolean onTouch(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(mTargetView, event);

        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();
        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mPrevX = event.getX();
                mPrevY = event.getY();

                // Save the ID of this pointer.
                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position.
                int pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex != -1) {
                    float currX = event.getX(pointerIndex);
                    float currY = event.getY(pointerIndex);

                    // Only move if the ScaleGestureDetector isn't processing a
                    // gesture.
                    if (!mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(currX - mPrevX, currY - mPrevY);
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor.
                int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mPrevX = event.getX(newPointerIndex);
                    mPrevY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }

                break;
            }
        }

        return true;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return true;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            TransformInfo info = new TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = isTranslateEnabled ? detector.getFocusX() - mPivotX : 0.0f;
            info.deltaY = isTranslateEnabled ? detector.getFocusY() - mPivotY : 0.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = minimumScale;
            info.maximumScale = maximumScale;
            move(info);
            return false;
        }
    }

    public void reset() {
        mTargetView.setRotation(0);
        mTargetView.setScaleX(1);
        mTargetView.setScaleY(1);
        mTargetView.setTranslationX(0);
        mTargetView.setTranslationY(0);
    }

    public void resumeToLastStatus() {
        if (mLastTransform.rotate != 0) {
            mTargetView.setRotation(mLastTransform.rotate);
        }
        if (mLastTransform.scale != 0) {
            mTargetView.setScaleX(mLastTransform.scale);
            mTargetView.setScaleY(mLastTransform.scale);
        }
        if (mLastTransform.translationX != 0) {
            mTargetView.setTranslationX(mLastTransform.translationX);
        }
        if (mLastTransform.translationY != 0) {
            mTargetView.setTranslationY(mLastTransform.translationY);
        }
    }

    private class TransformInfo {
        public float deltaX;
        public float deltaY;
        public float deltaScale;
        public float deltaAngle;
        public float pivotX;
        public float pivotY;
        public float minimumScale;
        public float maximumScale;
    }

    public class Transform {
        public float translationX;
        public float translationY;
        public float scale;
        public float rotate;
    }
}