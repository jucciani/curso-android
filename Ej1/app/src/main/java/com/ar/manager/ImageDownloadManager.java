package com.ar.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.ar.R;
import com.ar.task.ImageTask;
import com.ar.view.ItemImageView;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jucciani on 26/04/14.
 */
public class ImageDownloadManager {

    /*
     * Status indicators
     */
    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DECODE_STARTED = 3;
    public static final int TASK_COMPLETE = 4;

    // Sets the size of the storage that's used to cache images
    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /*
     * Creates a cache of byte arrays indexed by image URLs. As new items are added to the
     * cache, the oldest items are ejected and subject to garbage collection.
     */
    private final LruCache<URL, byte[]> mImageCache;

    //Instancia de ImageDownloadManager para implementación de Singleton.
    private static ImageDownloadManager sInstance = null;

    // Queue para el pool de descargas.
    private final BlockingQueue<Runnable> mDownloadWorkQueue;
    // A queue of Runnables for the image decoding pool
    private final BlockingQueue<Runnable> mDecodeWorkQueue;

    // A queue of ImageDownloadManagwr tasks. Tasks are handed to a ThreadPool.
    private final Queue<ImageTask> mImageTaskWorkQueue;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mDownloadThreadPool;

    // A managed pool of background decoder threads
    private final ThreadPoolExecutor mDecodeThreadPool;



    // An object that manages Messages in a Thread
    private Handler mHandler;

    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        //Creo una instancia estática única de ImageDownloadManager.
        sInstance = new ImageDownloadManager();
    }

    //Constructor privado
    private ImageDownloadManager(){
        //Inicilializo queues
        mDownloadWorkQueue = new LinkedBlockingQueue<Runnable>();
        mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        mImageTaskWorkQueue = new LinkedBlockingQueue<ImageTask>();


        //Inicializo pools de Threads
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);

        mDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);

        //Inicilizo la cache
        // Instantiates a new cache based on the cache size estimate
        mImageCache = new LruCache<URL, byte[]>(IMAGE_CACHE_SIZE) {

            /*
             * This overrides the default sizeOf() implementation to return the
             * correct size of each cache entry.
             */

            @Override
            protected int sizeOf(URL paramURL, byte[] paramArrayOfByte) {
                return paramArrayOfByte.length;
            }
        };


        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {

                // Gets the image task from the incoming Message object.
                ImageTask imageTask = (ImageTask) inputMessage.obj;

                // Sets an ImageView that's a weak reference to the
                // input ImageView
                ItemImageView localView = imageTask.getImageView();

                // If this input view isn't null
                if (localView != null) {

                    URL localURL = localView.getLocation();

                    /*
                     * Compares the URL of the input ImageView to the URL of the
                     * weak reference. Only updates the bitmap in the ImageView
                     * if this particular Thread is supposed to be serving the
                     * ImageView.
                     */
                    if (imageTask.getImageURL() == localURL)

                        /*
                         * Chooses the action to take, based on the incoming message
                         */
                        switch (inputMessage.what) {

                            /*
                             * The decoding is done, so this sets the
                             * ImageView's bitmap to the bitmap in the
                             * incoming message
                             */
                            case TASK_COMPLETE:
                                localView.setImageBitmap(imageTask.getImage());
                                recycleTask(imageTask);
                                break;
                            // The download failed, sets the background color to dark red
                            case DOWNLOAD_FAILED:
                                // Attempts to re-use the Task object
                                recycleTask(imageTask);
                                break;
                            default:
                                // Otherwise, calls the super method
                                super.handleMessage(inputMessage);
                        }
                }
            }
        };
    }

    /**
     * Recycles tasks by calling their internal recycle() method and then putting them back into
     * the task queue.
     * @param downloadTask The task to recycle
     */
    void recycleTask(ImageTask downloadTask) {

        // Frees up memory in the task
        downloadTask.recycle();

        // Puts the task object back into the queue for re-use.
        mImageTaskWorkQueue.offer(downloadTask);
    }

    public void handleState(ImageTask imageTask, int state) {
        switch (state) {

            // The task finished downloading and decoding the image
            case TASK_COMPLETE:

                // Puts the image into cache
                if (imageTask.isCacheEnabled()) {
                    // If the task is set to cache the results, put the buffer
                    // that was
                    // successfully decoded into the cache
                    mImageCache.put(imageTask.getImageURL(), imageTask.getByteBuffer());
                }

                // Gets a Message object, stores the state in it, and sends it to the Handler
                Message completeMessage = mHandler.obtainMessage(state, imageTask);
                completeMessage.sendToTarget();
                break;

            // The task finished downloading the image
            case DOWNLOAD_COMPLETE:
                /*
                 * Decodes the image, by queuing the decoder object to run in the decoder
                 * thread pool
                 */
                mDecodeThreadPool.execute(imageTask.getImageDecodeRunnable());

                // In all other cases, pass along the message without any other action.
            default:
                mHandler.obtainMessage(state, imageTask).sendToTarget();
                break;
        }

    }

    static public ImageTask startDownload(ItemImageView imageView, boolean cacheFlag){
        ImageTask downloadTask = sInstance.mImageTaskWorkQueue.poll();

        // If the queue was empty, create a new task instead.
        if (null == downloadTask) {
            downloadTask = new ImageTask();
        }

        // Initializes the task
        downloadTask.initializeDownloaderTask(ImageDownloadManager.sInstance, imageView, cacheFlag);

        //Obtengo Image cacheada
        downloadTask.setByteBuffer(sInstance.mImageCache.get(downloadTask.getImageURL()));

        // If the byte buffer was empty, the image wasn't cached
        if (null == downloadTask.getByteBuffer()) {

            /*
             * "Executes" the tasks' download Runnable in order to download the image. If no
             * Threads are available in the thread pool, the Runnable waits in the queue.
             */
            sInstance.mDownloadThreadPool.execute(downloadTask.getHTTPDownloadRunnable());

            // Sets the display to show that the image is queued for downloading and decoding.
            imageView.setImageResource(R.drawable.ic_action_picture);

            // The image was cached, so no download is required.
        } else {

            /*
             * Signals that the download is "complete", because the byte array already contains the
             * undecoded image. The decoding starts.
             */

            sInstance.handleState(downloadTask, DOWNLOAD_COMPLETE);
        }

        // Returns a task object, either newly-created or one from the task pool
        return downloadTask;
    }


    /**
     * Stops a download Thread and removes it from the threadpool
     *
     * @param downloaderTask The download task associated with the Thread
     * @param pictureURL The URL being downloaded
     */
    static public void removeDownload(ImageTask downloaderTask, URL pictureURL) {

        // If the Thread object still exists and the download matches the specified URL
        if (downloaderTask != null && downloaderTask.getImageURL().equals(pictureURL)) {

            /*
             * Locks on this class to ensure that other processes aren't mutating Threads.
             */
            synchronized (sInstance) {

                // Gets the Thread that the downloader task is running on
                Thread thread = downloaderTask.getCurrentThread();

                // If the Thread exists, posts an interrupt to it
                if (null != thread)
                    thread.interrupt();
            }
            /*
             * Removes the download Runnable from the ThreadPool. This opens a Thread in the
             * ThreadPool's work queue, allowing a task in the queue to start.
             */
            sInstance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
        }
    }


    /**
     * Cancels all Threads in the ThreadPool
     */
    public static void cancelAll() {

        /*
         * Creates an array of tasks that's the same size as the task work queue
         */
        ImageTask[] taskArray = new ImageTask[sInstance.mDownloadWorkQueue.size()];

        // Populates the array with the task objects in the queue
        sInstance.mDownloadWorkQueue.toArray(taskArray);

        // Stores the array length in order to iterate over the array
        int taskArraylen = taskArray.length;

        /*
         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
         * iterates over the array of tasks and interrupts the task's current Thread.
         */
        synchronized (sInstance) {

            // Iterates over the array of tasks
            for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

                // Gets the task's current thread
                Thread thread = taskArray[taskArrayIndex].mThreadThis;

                // if the Thread exists, post an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }


    static public ImageDownloadManager getInstance(){
        return ImageDownloadManager.sInstance;
    }
}
