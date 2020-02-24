package com.anand.simplevideoview

import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

//    private val VIDEO_SAMPLE = "samplesong"
    private var mVideoView: VideoView? = null
    private var mCurrentPosition = 0
    private val PLAYBACK_TIME = "play_time"

    private val VIDEO_SAMPLE1 =
        "https://player.vimeo.com/video/291648067?title=0&portrait=0&byline=0&autoplay=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mVideoView = findViewById(R.id.videoview)
        val controller = MediaController(this)
        controller.setMediaPlayer(mVideoView)
        mVideoView?.setMediaController(controller)
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView!!.pause()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYBACK_TIME, mVideoView!!.currentPosition)
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }


    private fun getMedia(mediaName: String): Uri? {
        if (URLUtil.isValidUrl(mediaName)) {
            // media name is an external URL
            return Uri.parse(mediaName);
        } else { // media name is a raw resource embedded in the app
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }

    private fun initializePlayer() {
        buffering_textview.visibility = VideoView.VISIBLE
        val videoUri = getMedia(VIDEO_SAMPLE1)
        mVideoView?.setVideoURI(videoUri)
        if (mCurrentPosition > 0) {
            mVideoView?.seekTo(mCurrentPosition)
        } else {
            // Skipping to 1 shows the first frame of the video.
            mVideoView?.seekTo(1)
        }
        mVideoView?.start()

        mVideoView!!.setOnPreparedListener {
            buffering_textview.visibility = VideoView.INVISIBLE
            if (mCurrentPosition > 0) {
                mVideoView!!.seekTo(mCurrentPosition)
            } else {
                mVideoView!!.seekTo(1)
            }
            mVideoView!!.start()
        }

        mVideoView?.setOnCompletionListener(OnCompletionListener {
            Toast.makeText(
                this@MainActivity, "Playback completed",
                Toast.LENGTH_SHORT
            ).show()
            mVideoView?.seekTo(1)
        })
    }

    private fun releasePlayer() {
        mVideoView?.stopPlayback()
    }
}
