/*
 * Copyright (C) 2013 Andrew Neal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.centerstage.limelight;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {

    private final SparseArray<WeakReference<Fragment>> mFragmentArray = new SparseArray<WeakReference<Fragment>>();

    private final List<Holder> mHolderList = new ArrayList<Holder>();

    private final Activity mActivity;

    private int mCurrentPage;


    /**
     * Constructor of <code>PagerAdapter<code>
     */
    public PagerAdapter(Activity Activity, FragmentManager fm) {
        super(fm);
        mActivity = Activity;
    }

    /**
     * Method that adds a new fragment class to the viewer (the fragment is
     * internally instantiate)
     * 
     * @param className The full qualified name of fragment class.
     * @param params The instantiate params.
     */
    @SuppressWarnings("synthetic-access")
    public void add(Class<? extends ListFragment> className, Bundle params) {
        final Holder holder = new Holder();
        holder.mClassName = className.getName();
        holder.mParams = params;

        final int position = mHolderList.size();
        mHolderList.add(position, holder);
        notifyDataSetChanged();
    }

    /**
     * Method that returns the Fragment in the argument
     * position.
     * 
     * @param position The position of the fragment to return.
     * @return Fragment The SherlockFragment in the argument position.
     */
    public Fragment getFragment(int position) {
        final WeakReference<Fragment> weakFragment = mFragmentArray.get(position);
        if (weakFragment != null && weakFragment.get() != null) {
            return weakFragment.get();
        }
        return getItem(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        final WeakReference<Fragment> weakFragment = mFragmentArray.get(position);
        if (weakFragment != null) {
            weakFragment.clear();
        }
        mFragmentArray.put(position, new WeakReference<Fragment>(fragment));
        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fragment getItem(int position) {
        final Holder currentHolder = mHolderList.get(position);
        final Fragment fragment = Fragment.instantiate(mActivity, currentHolder.mClassName,
                currentHolder.mParams);
        return fragment;
    }

    //-----------------------------------------------------------------------------
    // Used by ViewPager.  "Object" represents the page; tell the ViewPager where the
    // page should be displayed, from left-to-right.  If the page no longer exists,
    // return POSITION_NONE.
    @Override
    public int getItemPosition (Object object)
    {
        int index = mHolderList.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        final WeakReference<Fragment> weakFragment = mFragmentArray.get(position);
        if (weakFragment != null) {
            weakFragment.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return mHolderList.size();
    }

    /**
     * Method that returns the current page position.
     * 
     * @return int The current page.
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * Method that sets the current page position.
     * 
     * @param currentPage The current page.
     */
    protected void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    /**
     * A private class with information about fragment initialization
     */
    private static final class Holder {
        String mClassName;
        Bundle mParams;
    }
}
