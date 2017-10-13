package candra.bukupengeluaran.Supports.Utils;

/**
 * Created by noizar on 7/11/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


public abstract class Adapter<TipeData,ViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolder> {

    protected int mLayout;
    Class<ViewHolder> mViewHolderClass;
    Class<TipeData> mModelClass;
    List<TipeData> mData;

    public Adapter(int mLayout, Class<ViewHolder> mViewHolderClass, Class<TipeData> mModelClass, List<TipeData> mData){
        this.mLayout = mLayout;
        this.mViewHolderClass = mViewHolderClass;
        this.mModelClass = mModelClass;
        this.mData = mData;
    }

    public void addData(TipeData data){
        mData.add(data);
        notifyItemInserted(getItemCount() - 1);
    }

    public void insertData(int position, TipeData data){
        mData.add(position, data);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public void removeData(int position){
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public List<TipeData> getmData() {
        return mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mLayout,parent,false);

        try{
            Constructor<ViewHolder> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TipeData model = getItem(position);
        bindView(holder,model,position);
    }

    abstract protected void bindView(ViewHolder holder,TipeData model,int position);

    private TipeData getItem(int position){
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

