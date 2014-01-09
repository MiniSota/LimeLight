package com.centerstage.limelight;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tabcarousel.CarouselContainer;
import com.android.tabcarousel.CarouselPagerAdapter;

/**
 * Created by Smitesh Kharat on 12/5/13.
 */
public class HomeCarouselFragment extends Fragment {

    /* First tab index */
    private static final int FIRST_TAB = CarouselContainer.TAB_INDEX_FIRST;

    /* Second tab index */
    private static final int SECOND_TAB = CarouselContainer.TAB_INDEX_SECOND;

    public static final String ARG_SECTION_NUMBER = "section_number";


    public static HomeCarouselFragment newInstance(int sectionNumber) {
        HomeCarouselFragment homeCarouselFragment = new HomeCarouselFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        homeCarouselFragment.setArguments(args);
        return homeCarouselFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LimelightApp) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.carousel_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Resources
        final Resources res = getResources();

        // Initialize the header
        final CarouselContainer carousel = (CarouselContainer) getView().findViewById(R.id.carousel_header);
        // Indicates that the carousel should only show a fraction of the secondary tab
        carousel.setUsesDualTabs(true);
        // Add some text to the labels
        carousel.setLabel(FIRST_TAB, "In Theatres");
        carousel.setLabel(SECOND_TAB, "Coming Soon");
        // Add some images to the tabs
        carousel.setImageDrawable(FIRST_TAB, res.getDrawable(R.drawable.in_theaters));
        carousel.setImageDrawable(SECOND_TAB, res.getDrawable(R.drawable.coming_soon));

        // Initialize the pager adapter
        final PagerAdapter pagerAdapter = new PagerAdapter(getActivity(), getChildFragmentManager());
        Bundle args = new Bundle();
        args.putInt("section_number", 1);
        pagerAdapter.add(HomeFragment1.class, args);
        pagerAdapter.add(HomeFragment2.class, args);

        // Initialize the pager
        final ViewPager carouselPager = (ViewPager) getActivity().findViewById(R.id.carousel_pager);
        // This is used to communicate between the pager and header
        carouselPager.setOnPageChangeListener(new CarouselPagerAdapter(carouselPager, carousel));
        carouselPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LimelightApp) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
