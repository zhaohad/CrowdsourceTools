package demo.hw.ziyinghttp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTxvResult;
    private Button mBtnBaidu;
    //声明ViewPager
    private ViewPager mViewpager;
    //声明ViewPager的适配器
    private PagerAdapter mAdpater;
    //用于装载2个Tab的List
    private List<View> mTabs = new ArrayList<View>();

    private TextView txt_tab1;
    private TextView txt_tab2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAdapter();
        initEvents();
    }
    private void initEvents() {
        mBtnBaidu.setOnClickListener(this.mOnClickListener);
        txt_tab1.setOnClickListener(mOnClickListener);
        txt_tab2.setOnClickListener(mOnClickListener);
        //添加ViewPager的切换Tab的监听事件
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //获取ViewPager的当前Tab
                int currentItem = mViewpager.getCurrentItem();
                //将所以的tab设置成灰色
                resetTabColor();
                switch (currentItem) {
                    case 0:
                        setTabColor(txt_tab1);
                        break;
                    case 1:
                        setTabColor(txt_tab2);
                        break;
                }
        }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void initView() {
        mViewpager = (ViewPager) findViewById(R.id.id_viewpager);
        //获取到2个Tab
        LayoutInflater inflater = LayoutInflater.from(this);
        View tab1 = inflater.inflate(R.layout.tab1_layout, null);
        View tab2 = inflater.inflate(R.layout.tab2_layout, null);
        txt_tab1 = (TextView) findViewById(R.id.tab1);
        txt_tab2 = (TextView) findViewById(R.id.tab2);
        //添加集合
        mTabs.add(tab1);
        mTabs.add(tab2);
        mTxvResult = (TextView) tab1.findViewById(R.id.txv_result);
        mBtnBaidu = (Button) tab1.findViewById(R.id.btn_cancel1);
    }

    private void initAdapter() {
        //初始化ViewPager的适配器
        mAdpater = new PagerAdapter() {
            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mTabs.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mTabs.get(position));
            }
        };
        //设置ViewPager的适配器
        mViewpager.setAdapter(mAdpater);
        resetTabColor();
        mViewpager.setCurrentItem(0);
        setTabColor(txt_tab1);
    }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel1:
                    new HttpTask("https://www.baidu.com/").execute();
                    break;
                case R.id.btn_cancel2:
                    break;
                case R.id.tab1:
                    resetTabColor();
                    mViewpager.setCurrentItem(0);
                    setTabColor(txt_tab1);
                    break;
                case R.id.tab2:
                    resetTabColor();
                    mViewpager.setCurrentItem(1);
                    setTabColor(txt_tab2);
                    break;
            }
        }
    };
    private int txtColor1 = Color.parseColor("#949494");
    private int txtColor2 = Color.parseColor("#CCCCCC");

    private void setTabColor(TextView tab) {
        tab.setBackgroundColor(Color.parseColor(txtColor2));
        tab.setTextColor(Color.parseColor("#949494"));
    }
    private void resetTabColor() {
        txt_tab1.setBackgroundColor(Color.parseColor("#949494"));
        txt_tab2.setBackgroundColor(Color.parseColor("#949494"));
        txt_tab1.setTextColor(Color.parseColor("#CCCCCC"));
        txt_tab2.setTextColor(Color.parseColor("#CCCCCC"));
    }
    private class HttpTask extends AsyncTask {
        String mUrl;

        HttpTask(String url) {
            mUrl = url;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            return NetworkUtils.requestFromNetwork(mUrl, null, NetworkUtils.REQUEST_METHOD_GET);
        }

        @Override
        protected void onPostExecute(Object o) {
            String result = (String) o;
            mTxvResult.setText(result);
        }
    }
}
