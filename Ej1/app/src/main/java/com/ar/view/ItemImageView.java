package com.ar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


import com.ar.R;
import com.ar.manager.ImageDownloadManager;
import com.ar.task.ImageTask;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by jucciani on 27/04/14.
 */
public class ItemImageView extends ImageView {

    // Indicates if caching should be used
    private boolean mCacheFlag;

    // Status flag that indicates if onDraw has completed
    private boolean mIsDrawn;

    /*
     * Creates a weak reference to the ImageView in this object. The weak
     * reference prevents memory leaks and crashes, because it automatically tracks the "state" of
     * the variable it backs. If the reference becomes invalid, the weak reference is garbage-
     * collected.
     * This technique is important for referring to objects that are part of a component lifecycle.
     * Using a hard reference may cause memory leaks as the value continues to change; even worse,
     * it can cause crashes if the underlying component is destroyed. Using a weak reference to
     * a View ensures that the reference is more transitory in nature.
     */
    private WeakReference<View> mThisView;

    // Contains the ID of the internal View
    private int mHideShowResId = -1;

    // The URL that points to the source of the image for this ImageView
    private URL mImageURL;

    // The Thread that will be used to download the image for this ImageView
    private ImageTask mDownloadThread;

    /**
     * Creates an ImageDownloadView with no settings
     * @param context A context for the View
     */
    public ItemImageView(Context context) {
        super(context);
    }

    /**
     * Creates an ImageDownloadView and gets attribute values
     * @param context A Context to use with the View
     * @param attributeSet The entire set of attributes for the View
     */
    public ItemImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Gets attributes associated with the attribute set
        getAttributes(attributeSet);
    }

    /**
     * Gets the resource ID for the hideShowSibling resource
     * @param attributeSet The entire set of attributes for the View
     */
    private void getAttributes(AttributeSet attributeSet) {

        // Gets an array of attributes for the View
        TypedArray attributes =
                getContext().obtainStyledAttributes(attributeSet, R.styleable.ImageDownloaderView);

        // Gets the resource Id of the View to hide or show
        mHideShowResId =
                attributes.getResourceId(R.styleable.ImageDownloaderView_hideShowSibling, -1);

        // Returns the array for re-use
        attributes.recycle();
    }

    /**
     * Sets the visibility of the ImageView
     * @param visState The visibility state (see View.setVisibility)
     */
    private void showView(int visState) {
        // If the View contains something
        if (mThisView != null) {

            // Gets a local hard reference to the View
            View localView = mThisView.get();

            // If the weak reference actually contains something, set the visibility
            if (localView != null)
                localView.setVisibility(visState);
        }
    }


    /**
     * Returns the URL of the picture associated with this ImageView
     * @return a URL
     */
    public final URL getLocation() {
        return mImageURL;
    }

    /*
     * This callback is invoked when the system attaches the ImageView to a Window. The callback
     * is invoked before onDraw(), but may be invoked after onMeasure()
     */
    @Override
    protected void onAttachedToWindow() {
        // Always call the supermethod first
        super.onAttachedToWindow();

        // If the sibling View is set and the parent of the ImageView is itself a View
        if ((this.mHideShowResId != -1) && ((getParent() instanceof View))) {

            // Gets a handle to the sibling View
            View localView = ((View) getParent()).findViewById(this.mHideShowResId);

            // If the sibling View contains something, make it the weak reference for this View
            if (localView != null) {
                this.mThisView = new WeakReference<View>(localView);
            }
        }
    }

    /*
     * This callback is invoked when the ImageView is removed from a Window. It "unsets" variables
     * to prevent memory leaks.
     */
    @Override
    protected void onDetachedFromWindow() {

        // Clears out the image drawable, turns off the cache, disconnects the view from a URL
        setImageURL(null, false, null);

        // Gets the current Drawable, or null if no Drawable is attached
        Drawable localDrawable = getDrawable();

        // if the Drawable is null, unbind it from this VIew
        if (localDrawable != null)
            localDrawable.setCallback(null);

        // If this View still exists, clears the weak reference, then sets the reference to null
        if (mThisView != null) {
            mThisView.clear();
            mThisView = null;
        }

        // Sets the downloader thread to null
        this.mDownloadThread = null;

        // Always call the super method last
        super.onDetachedFromWindow();
    }

    /*
     * This callback is invoked when the system tells the View to draw itself. If the View isn't
     * already drawn, and its URL isn't null, it invokes a Thread to download the image. Otherwise,
     * it simply passes the existing Canvas to the super method
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // If the image isn't already drawn, and the URL is set
        if ((!mIsDrawn) && (mImageURL != null)) {

            // Starts downloading this View, using the current cache setting
            mDownloadThread = ImageDownloadManager.startDownload(this, mCacheFlag);

            // After successfully downloading the image, this marks that it's available.
            mIsDrawn = true;
        }
        // Always call the super method last
        super.onDraw(canvas);
    }

    @Override
    public void setImageBitmap(Bitmap paramBitmap) {
        super.setImageBitmap(paramBitmap);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        // The visibility of the View
        int viewState;

        /*
         * Sets the View state to visible if the method is called with a null argument (the
         * image is being cleared). Otherwise, sets the View state to invisible before refreshing
         * it.
         */
        if (drawable == null) {

            viewState = View.VISIBLE;
        } else {

            viewState = View.INVISIBLE;
        }
        // Either hides or shows the View, depending on the view state
        showView(viewState);

        // Invokes the supermethod with the provided drawable
        super.setImageDrawable(drawable);
    }

    /*
     * Displays a drawable in the View
     */
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    /*
     * Sets the URI for the Image
     */
    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
    }

    /**
     * Attempts to set the picture URL for this ImageView and then download the picture.
     * <p>
     * If the picture URL for this view is already set, and the input URL is not the same as the
     * stored URL, then the picture has moved and any existing downloads are stopped.
     * <p>
     * If the input URL is the same as the stored URL, then nothing needs to be done.
     * <p>
     * If the stored URL is null, then this method starts a download and decode of the picture
     * @param pictureURL An incoming URL for a Picasa picture
     * @param cacheFlag Whether to use caching when doing downloading and decoding
     * @param imageDrawable The Drawable to use for this ImageView
     */
    public void setImageURL(URL pictureURL, boolean cacheFlag, Drawable imageDrawable) {
        // If the picture URL for this ImageView is already set
        if (mImageURL != null) {

            // If the stored URL doesn't match the incoming URL, then the picture has changed.
            if (!mImageURL.equals(pictureURL)) {

                // Stops any ongoing downloads for this ImageView
                ImageDownloadManager.removeDownload(mDownloadThread, mImageURL);
            } else {

                // The stored URL matches the incoming URL. Returns without doing any work.
                return;
            }
        }

        // Sets the Drawable for this ImageView
        setImageDrawable(imageDrawable);

        // Stores the picture URL for this ImageView
        mImageURL = pictureURL;

        // If the draw operation for this ImageVIew has completed, and the picture URL isn't empty
        if ((mIsDrawn) && (pictureURL != null)) {

            // Sets the cache flag
            mCacheFlag = cacheFlag;

            /*
             * Starts a download of the picture file. Notice that if caching is on, the picture
             * file's contents may be taken from the cache.
             */
            mDownloadThread = ImageDownloadManager.startDownload(this, cacheFlag);
        }
    }
}
