package com.randmcnally.crashdetection.interfaces;

import com.randmcnally.crashdetection.widgets.ScrollViewExt;

public interface ScrollViewListener {
    void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy);
}
