package com.gmail.kyleyeeyixin.multifunction_clock.model.main;

import android.view.MenuItem;

import java.io.Serializable;

/**
 * Created by yunnnn on 2016/4/13.
 */
public class MyMenueItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private MenuItem menueItem;

    public MenuItem getMenueItem() {
        return menueItem;
    }

    public void setMenueItem(MenuItem menueItem) {
        this.menueItem = menueItem;
    }
}
