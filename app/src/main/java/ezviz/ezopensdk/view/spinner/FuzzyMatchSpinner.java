package ezviz.ezopensdk.view.spinner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.R;


/**
 * 带有筛选功能的Spinner
 * @author Lue
 * @since 2015-05-06
 */
public class FuzzyMatchSpinner extends FrameLayout
{
    private String TAG = "FuzzyMatchSpinner";

    private TextView tvCheckValue;

    private ImageButton dowm;

    //private Dialog mPopup;

    private AlertDialog alertDiag;

    private int checkPosition;

    private String mPrompt;

    private SpinnerAdapter listAdapter;

    //private String[] data;

    private List<LoBody> data;

    /**
     * 显示结果
     */
    private ListView lvResult;

    /**
     * 筛选输入框
     */
    private EditText etKeyWords;

    private Spinner.OnItemSelectedListener onItemSelectedListener;

    public FuzzyMatchSpinner(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public FuzzyMatchSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.widget_fuzzymatchspinner,
                this);

        tvCheckValue = (TextView) findViewById(R.id.tvcheckvalue_fuzzymatchspinner);

        dowm = findViewById(R.id.down);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.fuzzymatchspinner);

        mPrompt = a.getString(R.styleable.fuzzymatchspinner_prompt);

        a.recycle();
    }

    public FuzzyMatchSpinner(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public String getText(){
        return tvCheckValue.getText().toString();
    }
    @Override
    public boolean performClick() {
        boolean handled = super.performClick();

//        if(!handled) {
//            handled = true;
//
//            if(alertDiag == null) {
//                createAlertDialog();
//            } else if(!alertDiag.isShowing()) {
//                alertDiag.show();
//            }
//        }
//        dowm.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(alertDiag == null) {
//                    createAlertDialog();
//                } else if(!alertDiag.isShowing()) {
//                    alertDiag.show();
//                }
//            }
//        });
        return handled;
    }

//    public void setAdapter(SpinnerAdapter adapter,List<String> data)
//    {
//        if(data == null || data.size() == 0)
//        {
//            setAdapter(adapter,new String[0]);
//        }
//        else
//        {
//            String[] tmpArray = new String[data.size()];
//            data.toArray(tmpArray);
//            setAdapter(adapter,tmpArray);
//        }
//    }
//    public void setAdapter(SpinnerAdapter adapter,String[] data)
//    {
//        listAdapter = adapter;
//        this.data = data;
//
//        checkPosition = 0;
//
//        if(data != null && data.length > 0)
//        {
//            tvCheckValue.setText(data[checkPosition]);
//
//            if(onItemSelectedListener != null)
//            {
//                onItemSelectedListener.onItemSelected(lvResult, null, checkPosition, 0);
//            }
//        }
//    }

    public void setAdapter(SpinnerAdapter adapter, List<LoBody> loBodyList){
        listAdapter = adapter;
        //this.data = data;

        this.data = loBodyList;

        checkPosition = 0;

        if(loBodyList != null && loBodyList.size() > 0)
        {
            tvCheckValue.setText(loBodyList.get(checkPosition).getName());

            if(onItemSelectedListener != null)
            {
                onItemSelectedListener.onItemSelected(lvResult, null, checkPosition, 0);
            }
        }
    }

    public void setOnItemSelectedListener(Spinner.OnItemSelectedListener onItemSelectedListener)
    {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setAlertDiag(int width,int height){
        if (alertDiag!=null){
            final WindowManager.LayoutParams params = alertDiag.getWindow().getAttributes();
            params.width = width;
            params.height = height;
            alertDiag.getWindow().setAttributes(params);
            alertDiag.getWindow().setBackgroundDrawableResource(R.color.white);
        }
    }
    public AlertDialog getAlertDiag(){
        return alertDiag;
    }
    public void createAlertDialog()
    {
        //mPopup = new Dialog(getContext());
        alertDiag = new AlertDialog.Builder(getContext()).create();
        //mPopup.show();
        if(mPrompt != null)
        {
            //mPopup.setTitle(mPrompt);
            alertDiag.setTitle(mPrompt);
        }

        //mPopup.setContentView(R.layout.widget_fuzzyspiner_dialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_fuzzyspiner_dialog, null);
        alertDiag.setView(view);

//        etKeyWords = (EditText)mPopup.findViewById(R.id.etkeywords_spinnerdialog);
//        lvResult = (ListView)mPopup.findViewById(R.id.lv_results_spinnerdialog);
        etKeyWords = (EditText)view.findViewById(R.id.etkeywords_spinnerdialog);
        lvResult = (ListView)view.findViewById(R.id.lv_results_spinnerdialog);
        Log.d(TAG,"dialog.show");
        alertDiag.show();


        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
        view.setAnimation(animation);
        animation.start();



        //增加变化监听
        etKeyWords.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s)
            {
                if(s == null || s.toString() == null || "".equals(s.toString().trim()))
                {
                    if(listAdapter != null)
                    {
                        lvResult.setAdapter(new DropDownAdapter(listAdapter));
                    }
                }
                else
                {
                    String[] result = assembleMatchItems(s.toString());

                    ArrayAdapter tempAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, result);

                    tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    lvResult.setAdapter(new DropDownAdapter(tempAdapter));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after)
            {
                // TODO Auto-generated method stub

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count)
            {
                // TODO Auto-generated method stub

            }
        });

        if(data == null || data.size() == 0 || data.size() < 5)
        {
            etKeyWords.setVisibility(View.GONE);
        }
        else
        {
            etKeyWords.setVisibility(View.VISIBLE);
        }

        if(listAdapter != null)
        {
            lvResult.setAdapter(new DropDownAdapter(listAdapter));

            lvResult.setSelection(checkPosition);
            //lvResult.setSelectionFromTop(checkPosition, 0);

            lvResult.setItemChecked(checkPosition, true);
        }

        lvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                DropDownAdapter adapter = (DropDownAdapter)parent.getAdapter();
                if(adapter != null)
                {
                    String checkValue = (String)adapter.getItem(position);


                    if(checkPosition != position && onItemSelectedListener != null)
                    {
                        onItemSelectedListener.onItemSelected(parent, view, position, id);
                    }

                    setSelection(findCheckPosition(checkValue));

                    tvCheckValue.setText(checkValue);
                }

//                mPopup.dismiss();
//                mPopup = null;
                alertDiag.dismiss();
                alertDiag = null;
            }
        });
    }

    /**
     * 筛选匹配
     * @param keyWords 关键字
     * @return
     */
    private String[] assembleMatchItems(String keyWords)
    {
        String[] matchResult = new String[0];

        if(data != null)
        {
            List<String> rt = new ArrayList<String>();

            int len = data.size();
            for(int i=0; i<len; i++)
            {
                if(data.get(i).getName().toLowerCase().contains(keyWords.toLowerCase()))
                {
                    rt.add(data.get(i).getName());
                }
            }

            if(rt.size() > 0)
            {
                matchResult = new String[rt.size()];
                rt.toArray(matchResult);
            }
        }

        return matchResult;
    }

    private int findCheckPosition(String value)
    {
        int checkPosition = 0;

        if(data != null)
        {
            int len = data.size();
            for(int i=0; i<len; i++)
            {
                if(data.get(i).getName().equals(value))
                {
                    checkPosition = i;
                    break;
                }
            }
        }

        return checkPosition;
    }

    public void setSelection(int which)
    {
        this.checkPosition = which;
    }

    /**
     * <p>Wrapper class for an Adapter. Transforms the embedded Adapter instance
     * into a ListAdapter.</p>
     */
    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        /**
         * <p>Creates a new ListAdapter wrapper for the specified adapter.</p>
         *
         * @param adapter the Adapter to transform into a ListAdapter
         */
        public DropDownAdapter(SpinnerAdapter adapter) {
            this.mAdapter = adapter;
            if (adapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) adapter;

            }
        }


        @Override
        public int getCount() {
            return mAdapter == null ? 0 : mAdapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mAdapter == null ? null : mAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter == null ? -1 : mAdapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return mAdapter == null ? null :
                    mAdapter.getDropDownView(position, convertView, parent);
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        /**
         * If the wrapped SpinnerAdapter is also a ListAdapter, delegate this call.
         * Otherwise, return true.
         */
        @Override
        public boolean areAllItemsEnabled() {
            final ListAdapter adapter = mListAdapter;
            if (adapter != null) {
                return adapter.areAllItemsEnabled();
            } else {
                return true;
            }
        }

        /**
         * If the wrapped SpinnerAdapter is also a ListAdapter, delegate this call.
         * Otherwise, return true.
         */
        @Override
        public boolean isEnabled(int position) {
            final ListAdapter adapter = mListAdapter;
            if (adapter != null) {
                return adapter.isEnabled(position);
            } else {
                return true;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (alertDiag != null && alertDiag.isShowing()) {
            alertDiag.dismiss();
        }
    }
}