package com.softnauts.visionauts.common

import android.widget.SeekBar

interface SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}