package com.demo.vjrutnat.mp3.helper;

/**
 * Created by Admin on 12/7/2017.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
