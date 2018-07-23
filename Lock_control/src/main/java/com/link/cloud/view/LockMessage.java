package com.link.cloud.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.greendao.gen.CabinetNumberDao;
import com.link.cloud.greendao.gen.CabinetRecordDao;
import com.link.cloud.greendaodemo.CabinetNumber;
import com.link.cloud.greendaodemo.CabinetRecord;
import com.link.cloud.greendaodemo.LockedAdapter;
import com.link.cloud.greendaodemo.RecordAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 30541 on 2018/5/3.
 */

public class LockMessage extends Dialog{
    private Context mContext;
    private ListView listView;
    CabinetNumberDao cabinetNumberDao;
    private List<CabinetNumber> recordList = new ArrayList<CabinetNumber>();
    private List recordstate=new ArrayList();
    LockedAdapter myAdapter;
    public LockMessage(@NonNull Context context) {
        super(context);
        mContext = context;
        initDialog();
    }

    public LockMessage(@NonNull Context context, int themeResId) {
        super(context, R.style.customer_dialog);
        mContext = context;
        initDialog();
    }
    private void initDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lock_message, null);
        setContentView(view);
        listView=(ListView)view.findViewById(R.id.lock_message);
        cabinetNumberDao= BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
        recordList=cabinetNumberDao.loadAll();
        myAdapter=new LockedAdapter(recordList,mContext);
        listView.setAdapter(myAdapter);
    }
}
