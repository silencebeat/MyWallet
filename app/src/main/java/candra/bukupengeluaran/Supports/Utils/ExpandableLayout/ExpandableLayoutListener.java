package candra.bukupengeluaran.Supports.Utils.ExpandableLayout;

/**
 * Created by Candra Triyadi on 05/09/2017.
 */

public interface ExpandableLayoutListener {

    void onAnimationStart();

    void onAnimationEnd();

    void onPreOpen();

    void onPreClose();

    void onOpened();

    void onClosed();
}