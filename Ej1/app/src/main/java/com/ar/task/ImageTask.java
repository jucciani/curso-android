package com.ar.task;


import com.ar.manager.ImageDownloadManager;
import com.ar.runnable.ImageDecodeRunnable;
import com.ar.runnable.ImageDecodeRunnable.TaskRunnableDecodeMethods;
import com.ar.runnable.ImageDownloadRunnable;
import com.ar.runnable.ImageDownloadRunnable.TaskRunnableDownloadMethods;
import com.ar.view.ItemImageView;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.URL;


public class ImageTask implements
        TaskRunnableDownloadMethods, TaskRunnableDecodeMethods {

    /*
     * Creates a weak reference to the ImageView that this Task will populate.
     * The weak reference prevents memory leaks and crashes, because it
     * automatically tracks the "state" of the variable it backs. If the
     * reference becomes invalid, the weak reference is garbage- collected. This
     * technique is important for referring to objects that are part of a
     * component lifecycle. Using a hard reference may cause memory leaks as the
     * value continues to change; even worse, it can cause crashes if the
     * underlying component is destroyed. Using a weak reference to a View
     * ensures that the reference is more transitory in nature.
     */
    private WeakReference<ItemImageView> mImageWeakRef;

    // The image's URL
    private URL mImageURL;

    // The width and height of the decoded image
    private int mTargetHeight;
    private int mTargetWidth;

    // Is the cache enabled for this transaction?
    private boolean mCacheEnabled;

    /*
     * Field containing the Thread this task is running on.
     */
    public Thread mThreadThis;

    /*
     * Fields containing references to the two runnable objects that handle downloading and
     * decoding of the image.
     */
    private Runnable mDownloadRunnable;
    private Runnable mDecodeRunnable;

    // A buffer for containing the bytes that make up the image
    byte[] mImageBuffer;

    // The decoded image
    private Bitmap mDecodedImage;

    // The Thread on which this task is currently running.
    private Thread mCurrentThread;

    /*
     * An object that contains the ThreadPool singleton.
     */
    private static ImageDownloadManager sImageManager;

    /**
     * Creates an ImageTask containing a download object and a decoder object.
     */
    public ImageTask() {
        // Create the runnables
        mDownloadRunnable = new ImageDownloadRunnable(this);
        mDecodeRunnable = new ImageDecodeRunnable(this);
        sImageManager = ImageDownloadManager.getInstance();
    }

    /**
     * Initializes the Task
     *
     * @param imageDownloadManager A ThreadPool object
     * @param itemImageView An ImageView instance that shows the downloaded image
     * @param cacheFlag Whether caching is enabled
     */
    public void initializeDownloaderTask(
            ImageDownloadManager imageDownloadManager,
            ItemImageView itemImageView,
            boolean cacheFlag)
    {
        // Sets this object's ThreadPool field to be the input argument
        sImageManager = imageDownloadManager;

        // Gets the URL for the View
        mImageURL = itemImageView.getLocation();

        // Instantiates the weak reference to the incoming view
        mImageWeakRef = new WeakReference<ItemImageView>(itemImageView);

        // Sets the cache flag to the input argument
        mCacheEnabled = cacheFlag;

        // Gets the width and height of the provided ImageView
        mTargetWidth = itemImageView.getWidth();
        mTargetHeight = itemImageView.getHeight();


    }

    // Implements HTTPDownloaderRunnable.getByteBuffer
    @Override
    public byte[] getByteBuffer() {

        // Returns the global field
        return mImageBuffer;
    }

    /**
     * Recycles an ImageTask object before it's put back into the pool. One reason to do
     * this is to avoid memory leaks.
     */
    public void recycle() {

        // Deletes the weak reference to the imageView
        if ( null != mImageWeakRef ) {
            mImageWeakRef.clear();
            mImageWeakRef = null;
        }

        // Releases references to the byte buffer and the BitMap
        mImageBuffer = null;
        mDecodedImage = null;
    }

    // Implements ImageDownloadRunnable.getTargetWidth. Returns the global target width.
    @Override
    public int getTargetWidth() {
        return mTargetWidth;
    }

    // Implements ImageDownloadRunnable.getTargetHeight. Returns the global target height.
    @Override
    public int getTargetHeight() {
        return mTargetHeight;
    }

    // Detects the state of caching
    public boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    // Implements ImageDownloadRunnable.getImageURL. Returns the global Image URL.
    @Override
    public URL getImageURL() {
        return mImageURL;
    }

    // Implements ImageDownloadRunnable.setByteBuffer. Sets the image buffer to a buffer object.
    @Override
    public void setByteBuffer(byte[] imageBuffer) {
        mImageBuffer = imageBuffer;
    }

    // Delegates handling the current state of the task to the ImageDownloadManager object
    void handleState(int state) {
        sImageManager.handleState(this, state);
    }

    // Returns the image that ImageDecodeRunnable decoded.
    public Bitmap getImage() {
        return mDecodedImage;
    }

    // Returns the instance that downloaded the image
    public Runnable getHTTPDownloadRunnable() {
        return mDownloadRunnable;
    }

    // Returns the instance that decode the image
    public Runnable getImageDecodeRunnable() {
        return mDecodeRunnable;
    }

    // Returns the ImageView that's being constructed.
    public ItemImageView getImageView() {
        if ( null != mImageWeakRef ) {
            return mImageWeakRef.get();
        }
        return null;
    }

    /*
     * Returns the Thread that this Task is running on. The method must first get a lock on a
     * static field, in this case the ThreadPool singleton. The lock is needed because the
     * Thread object reference is stored in the Thread object itself, and that object can be
     * changed by processes outside of this app.
     */
    public Thread getCurrentThread() {
        synchronized(sImageManager) {
            return mCurrentThread;
        }
    }

    /*
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        synchronized(sImageManager) {
            mCurrentThread = thread;
        }
    }

    // Implements ImageCoderRunnable.setImage(). Sets the Bitmap for the current image.
    @Override
    public void setImage(Bitmap decodedImage) {
        mDecodedImage = decodedImage;
        Log.d("ImageTask - size:", mDecodedImage.getHeight() + "x" +mDecodedImage.getHeight());
    }

    // Implements ImageDownloadRunnable.setHTTPDownloadThread(). Calls setCurrentThread().
    @Override
    public void setDownloadThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    /*
     * Implements ImageDownloadRunnable.handleHTTPState(). Passes the download state to the
     * ThreadPool object.
     */

    @Override
    public void handleDownloadState(int state) {
        int outState;

        // Converts the download state to the overall state
        switch(state) {
            case ImageDownloadRunnable.HTTP_STATE_COMPLETED:
                outState = ImageDownloadManager.DOWNLOAD_COMPLETE;
                break;
            case ImageDownloadRunnable.HTTP_STATE_FAILED:
                outState = ImageDownloadManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = ImageDownloadManager.DOWNLOAD_STARTED;
                break;
        }
        // Passes the state to the ThreadPool object.
        handleState(outState);
    }

    // Implements ImageDecodeRunnable.setImageDecodeThread(). Calls setCurrentThread().
    @Override
    public void setImageDecodeThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    /*
     * Implements ImageDecodeRunnable.handleDecodeState(). Passes the decoding state to the
     * ThreadPool object.
     */
    @Override
    public void handleDecodeState(int state) {
        int outState;

        // Converts the decode state to the overall state.
        switch(state) {
            case ImageDecodeRunnable.DECODE_STATE_COMPLETED:
                outState = ImageDownloadManager.TASK_COMPLETE;
                break;
            case ImageDecodeRunnable.DECODE_STATE_FAILED:
                outState = ImageDownloadManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = ImageDownloadManager.DECODE_STARTED;
                break;
        }
        // Passes the state to the ThreadPool object.
        handleState(outState);
    }
}

